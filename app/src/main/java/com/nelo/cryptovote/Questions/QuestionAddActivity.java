package com.nelo.cryptovote.Questions;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.nelo.cryptovote.Domain.Question;
import com.nelo.cryptovote.Domain.QuestionChoice;
import com.nelo.cryptovote.MyActivity;
import com.nelo.cryptovote.R;
import com.nelo.cryptovote.Signer;
import com.nelo.cryptovote.WebApiAdapters.QuestionApiAdapter;
import com.nelo.cryptovote.WebApiAdapters.RequestListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class QuestionAddActivity extends MyActivity {
    private QuestionApiAdapter questionApiAdapter;
    private UUID communityId;
    private byte type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_add);

        initToolbar();

        final Context context = this;

        questionApiAdapter = new QuestionApiAdapter(this, null);

        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null) {
            Log.d(getClass().getSimpleName(), data.toString());

            String address = data.getQueryParameter("address");
            if (address != null) {
                Toast.makeText(context, "TODO: buscar la comunidad en la blockchain y agregarla a la base de datos", Toast.LENGTH_SHORT).show();
            }
        } else {

            Spinner spinner = findViewById(R.id.question_type);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.question_types_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                    type = (byte) (pos + 1);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            type = 1;

            AppCompatButton choicesAddButton = findViewById(R.id.question_choices_add);
            choicesAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LinearLayout choices = findViewById(R.id.question_choices);
                    View.inflate(context, R.layout.choice_add_item, choices);
                }
            });

            final EditText questionCloseDate = findViewById(R.id.question_close_date);
            questionCloseDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Calendar cldr = Calendar.getInstance();
                    int day = cldr.get(Calendar.DAY_OF_MONTH);
                    int month = cldr.get(Calendar.MONTH);
                    int year = cldr.get(Calendar.YEAR);
                    // date picker dialog
                    DatePickerDialog picker = new DatePickerDialog(QuestionAddActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    questionCloseDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                }
                            }, year, month, day);
                    picker.show();
                }
            });

            final EditText questionCloseTime = findViewById(R.id.question_close_time);
            questionCloseTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Calendar cldr = Calendar.getInstance();
                    int hour = cldr.get(Calendar.HOUR_OF_DAY);
                    int minutes = cldr.get(Calendar.MINUTE);
                    // date picker dialog
                    TimePickerDialog picker = new TimePickerDialog(QuestionAddActivity.this,
                            new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker timePicker, int h, int m) {
                                    questionCloseTime.setText(h + ":" + (m < 10 ? "0" : "") + m);
                                }
                            }, hour, minutes, true);
                    picker.show();
                }
            });

            FloatingActionButton addButton = findViewById(R.id.question_add);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(context.getClass().getSimpleName(), "Agregando asunto...");
                    final Toast working = Toast.makeText(context, "Agregando asunto...", Toast.LENGTH_LONG);
                    working.show();

                    try {
                        TextView nameTextView = findViewById(R.id.question_name);

                        Question question = new Question();
                        question.id = UUID.randomUUID();
                        question.communityId = communityId;
                        question.name = nameTextView.getText().toString();

                        EditText dateEditText = findViewById(R.id.question_close_date);
                        EditText timeEditText = findViewById(R.id.question_close_time);
                        String end = dateEditText.getText() + " " + timeEditText.getText().toString();
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        question.endTime = format.parse(end).getTime();

                        question.type = type;

                        LinearLayout choices = findViewById(R.id.question_choices);
                        for (int i = 0; i < choices.getChildCount(); i++) {
                            Log.d("QuestionAddActivity", "Agregando opción " + i);
                            View child = choices.getChildAt(i);

                            QuestionChoice choice = new QuestionChoice();
                            choice.id = UUID.randomUUID();

                            EditText choiceTextEditText = child.findViewById(R.id.choice_text);
                            choice.text = choiceTextEditText.getText().toString();

                            EditText choiceColorEditText = child.findViewById(R.id.choice_color);
                            choice.color = Integer.parseInt(choiceColorEditText.getText().toString());

                            EditText choiceGuardianAddressEditText = child.findViewById(R.id.choice_guardian_address);
                            choice.guardianAddress = choiceGuardianAddressEditText.getText().toString();

                            Log.d("QuestionAddActivity", "choice.id: " + choice.id);
                            Log.d("QuestionAddActivity", "choice.text: " + choice.text);

                            question.choices.add(choice);
                        }

                        Signer signer = new Signer();
                        signer.sign(question);

                        questionApiAdapter.add(question, new RequestListener<Question>() {
                            @Override
                            public void onComplete(Question response) {
                                working.cancel();

                                Toast.makeText(context, "Asunto creado! :)", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void onError(int statusCode) {
                                working.cancel();
                                Toast.makeText(context, "Error " + statusCode + " agregando asunto :(", Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (Exception ex) {
                        working.cancel();
                        Log.e("QuestionAddActivity", ex.getMessage(), ex);
                        Toast.makeText(context, "Error al agregar asunto: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

            choicesAddButton.callOnClick();
            choicesAddButton.callOnClick();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = this.getIntent();
        communityId = UUID.fromString(intent.getStringExtra("communityId"));
    }
}