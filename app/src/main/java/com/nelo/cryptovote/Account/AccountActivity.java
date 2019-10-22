package com.nelo.cryptovote.Account;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.nelo.cryptovote.Base58;
import com.nelo.cryptovote.R;
import com.nelo.cryptovote.Signer;

public class AccountActivity extends AppCompatActivity {
    TextView  usernameTextView, publicKeyTextView, privateKeyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Toolbar toolbar = findViewById(R.id.menu);
        setSupportActionBar(toolbar);

        final Context context = this;

        usernameTextView = this.findViewById(R.id.account_username);
        publicKeyTextView = this.findViewById(R.id.account_publicKey);
        privateKeyTextView = this.findViewById(R.id.account_privateKey);

        ImageButton publicKeyCopyButton = this.findViewById(R.id.copy_public_key_button);
        publicKeyCopyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                Log.d("AccountActivity", "Public Key: " + publicKeyTextView.getText());
                ClipData clip = ClipData.newPlainText("Public Key", publicKeyTextView.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, R.string.copy_in_clipboard, Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton privateKeyCopyButton = this.findViewById(R.id.copy_private_key_button);
        privateKeyCopyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                Log.d("AccountActivity", "Private Key: " + privateKeyTextView.getText());
                ClipData clip = ClipData.newPlainText("Private Key", privateKeyTextView.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, R.string.copy_in_clipboard, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Signer signer = new Signer();

        CharSequence username = Signer.getUserName();
        usernameTextView.setText(username);

        byte[] publicKey = signer.getPublicKey();
        publicKeyTextView.setText(Base58.encode(publicKey));

        byte[] privateKey = signer.getPrivateKey();
        privateKeyTextView.setText(Base58.encode(privateKey));
    }
}
