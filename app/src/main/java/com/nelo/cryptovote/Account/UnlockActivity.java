package com.nelo.cryptovote.Account;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nelo.cryptovote.ApiAdapter;
import com.nelo.cryptovote.Communities.CommunityListActivity;
import com.nelo.cryptovote.R;
import com.nelo.cryptovote.Signer;
import com.nelo.cryptovote.Storage;

import java.util.List;

public class UnlockActivity extends AppCompatActivity {
    EditText serverEditText, passwordEditText, userNameEditText;
    AppCompatButton unlockButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);

        final Context context = this;

        serverEditText = findViewById(R.id.server);
        userNameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        unlockButton = findViewById(R.id.unlock);

        passwordEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    unlockButton.performClick();
                    return true;
                }
                return false;
            }
        });

        unlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unlockButton.setEnabled(false);

                CharSequence server = serverEditText.getText();
                Storage.setLastServer(context, server);
                ApiAdapter.setServer(server);

                final byte[] password = passwordEditText.getText().toString().getBytes();
                final CharSequence username = userNameEditText.getText();

                if (!Signer.exists(context, username)) {
                    Log.i("UnlockActivity", "El usuario " + username + " no existe");

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    Signer.create(context, username, password);
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(R.string.user_not_found_create)
                            .setPositiveButton(R.string.yes, dialogClickListener)
                            .setNegativeButton(R.string.no, dialogClickListener)
                            .show();
                }

                if (Signer.exists(context, username)) {
                    if (Signer.load(context, username, password)) {
                        Intent main = new Intent(context, CommunityListActivity.class);
                        startActivity(main);
                        unlockButton.setEnabled(true);
                    } else {
                        Toast.makeText(context, "PIN inválido", Toast.LENGTH_LONG).show();
                        unlockButton.setEnabled(true);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        CharSequence server = Storage.getLastServer(this);
        serverEditText.setText(server);

        if (userNameEditText.length() == 0)
            userNameEditText.setText("me");

        List<CharSequence> identities = Signer.listIdentities(this);

        LinearLayout identitiesLayout = findViewById(R.id.identities);
        identitiesLayout.removeAllViews();
        for (CharSequence identity : identities) {
            final Button button = new Button(this);
            button.setText(identity);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userNameEditText.setText(button.getText());
                }
            });
            identitiesLayout.addView(button);
        }
        passwordEditText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}
