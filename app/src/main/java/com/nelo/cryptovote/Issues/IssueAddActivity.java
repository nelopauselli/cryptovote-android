package com.nelo.cryptovote.Issues;

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
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.nelo.cryptovote.Domain.Issue;
import com.nelo.cryptovote.Domain.IssueChoice;
import com.nelo.cryptovote.MyActivity;
import com.nelo.cryptovote.R;
import com.nelo.cryptovote.Signer;
import com.nelo.cryptovote.WebApiAdapters.IssueApiAdapter;
import com.nelo.cryptovote.WebApiAdapters.RequestListener;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class IssueAddActivity extends MyActivity {
    private IssueApiAdapter issueApiAdapter;
    private UUID communityId;
    private byte type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_add);

        initToolbar();

        final Context context = this;

        issueApiAdapter = new IssueApiAdapter(this, null);

        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null) {
            Log.d(getClass().getSimpleName(), data.toString());

            String address = data.getQueryParameter("address");
            if (address != null) {
                Toast.makeText(context, "TODO: buscar la comunidad en la blockchain y agregarla a la base de datos", Toast.LENGTH_SHORT).show();
            }
        } else {

            Spinner spinner = findViewById(R.id.issue_type);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.issue_types_array, android.R.layout.simple_spinner_item);
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

            AppCompatButton choicesAddButton = findViewById(R.id.issue_choices_add);
            choicesAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LinearLayout choices = findViewById(R.id.issue_choices);
                    View.inflate(context, R.layout.choice_add_item, choices);
                }
            });

            final EditText issueCloseDate = findViewById(R.id.issue_close_date);
            issueCloseDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Calendar cldr = Calendar.getInstance();
                    int day = cldr.get(Calendar.DAY_OF_MONTH);
                    int month = cldr.get(Calendar.MONTH);
                    int year = cldr.get(Calendar.YEAR);
                    // date picker dialog
                    DatePickerDialog picker = new DatePickerDialog(IssueAddActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    issueCloseDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                }
                            }, year, month, day);
                    picker.show();
                }
            });

            final EditText issueCloseTime = findViewById(R.id.issue_close_time);
            issueCloseTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Calendar cldr = Calendar.getInstance();
                    int hour = cldr.get(Calendar.HOUR_OF_DAY);
                    int minutes = cldr.get(Calendar.MINUTE);
                    // date picker dialog
                    TimePickerDialog picker = new TimePickerDialog(IssueAddActivity.this,
                            new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker timePicker, int h, int m) {
                                    issueCloseTime.setText(h + ":" + (m < 10 ? "0" : "") + m);
                                }
                            }, hour, minutes, true);
                    picker.show();
                }
            });

            FloatingActionButton addButton = findViewById(R.id.issue_add);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(context.getClass().getSimpleName(), "Agregando asunto...");
                    final Toast working = Toast.makeText(context, "Agregando asunto...", Toast.LENGTH_LONG);
                    working.show();

                    try {
                        TextView nameTextView = findViewById(R.id.issue_name);

                        Issue issue = new Issue();
                        issue.id = UUID.randomUUID();
                        issue.communityId = communityId;
                        issue.name = nameTextView.getText().toString();

                        EditText dateEditText = findViewById(R.id.issue_close_date);
                        EditText timeEditText = findViewById(R.id.issue_close_time);
                        String end = dateEditText.getText() + " " + timeEditText.getText().toString();
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        issue.endTime = format.parse(end).getTime();

                        issue.type = type;

                        LinearLayout choices = findViewById(R.id.issue_choices);
                        for (int i = 0; i < choices.getChildCount(); i++) {
                            Log.d("IssueAddActivity", "Agregando opción " + i);
                            View child = choices.getChildAt(i);

                            IssueChoice choice = new IssueChoice();
                            choice.id = UUID.randomUUID();

                            EditText choiceTextEditText = child.findViewById(R.id.choice_text);
                            choice.text = choiceTextEditText.getText().toString();

                            EditText choiceColorEditText = child.findViewById(R.id.choice_color);
                            choice.color = Integer.parseInt(choiceColorEditText.getText().toString());

                            EditText choiceGuardianAddressEditText = child.findViewById(R.id.choice_guardian_address);
                            choice.guardianAddress = choiceGuardianAddressEditText.getText().toString();

                            Log.d("IssueAddActivity", "choice.id: " + choice.id);
                            Log.d("IssueAddActivity", "choice.text: " + choice.text);

                            issue.choices.add(choice);
                        }

                        Signer signer = new Signer();
                        signer.sign(issue);

                        issueApiAdapter.add(issue, new RequestListener<Issue>() {
                            @Override
                            public void onComplete(Issue response) {
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
                        Log.e("IssueAddActivity", ex.getMessage(), ex);
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