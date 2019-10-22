package com.nelo.cryptovote.Members;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nelo.cryptovote.Domain.Member;
import com.nelo.cryptovote.MyActivity;
import com.nelo.cryptovote.R;
import com.nelo.cryptovote.Signer;
import com.nelo.cryptovote.WebApiAdapters.RequestListener;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class MemberAddActivity extends MyActivity {
    private MemberApiAdapter memberApiAdapter;
    private UUID communityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_add);

        initToolbar();

        final Context context = this;

        memberApiAdapter = new MemberApiAdapter(this, null);

        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null) {
            Log.d(getClass().getSimpleName(), data.toString());

            String address = data.getQueryParameter("address");
            if (address != null) {
                Toast.makeText(context, "TODO: buscar la comunidad en la blockchain y agregarla a la base de datos", Toast.LENGTH_SHORT).show();
            }
        } else {

            FloatingActionButton addButton = findViewById(R.id.member_add);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(context.getClass().getSimpleName(), "Agregando miembro...");
                    final Toast working = Toast.makeText(context, "Agregando miembro...", Toast.LENGTH_LONG);
                    working.show();

                    try {
                        TextView nameTextView = findViewById(R.id.member_name);
                        TextView addressTextView = findViewById(R.id.member_address);

                        Member member = new Member();
                        member.id = UUID.randomUUID();
                        member.communityId = communityId;
                        member.name = nameTextView.getText().toString();
                        member.address = addressTextView.getText().toString();

                        Signer signer = new Signer();

                        signer.sign(member);

                        memberApiAdapter.add(member, new RequestListener<Member>() {
                            @Override
                            public void onComplete(Member response) {
                                working.cancel();

                                Toast.makeText(context, "Organización creada! :)", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, MemberListActivity.class);
                                context.startActivity(intent);

                                finish();
                            }

                            @Override
                            public void onError(int statusCode) {
                                working.cancel();
                                Toast.makeText(context, "Error " + statusCode + " agregando la organización :(", Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (UnsupportedEncodingException ex) {
                        working.cancel();
                        Toast.makeText(context, "Error en encoding", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = this.getIntent();
        communityId = UUID.fromString(intent.getStringExtra("communityId"));
    }
}