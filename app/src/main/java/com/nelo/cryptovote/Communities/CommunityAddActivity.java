package com.nelo.cryptovote.Communities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nelo.cryptovote.Base58;
import com.nelo.cryptovote.Domain.Community;
import com.nelo.cryptovote.MyActivity;
import com.nelo.cryptovote.R;
import com.nelo.cryptovote.Signer;
import com.nelo.cryptovote.WebApiAdapters.RequestListener;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class CommunityAddActivity extends MyActivity {
    private CommunityApiAdapter communityApiAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_add);

        initToolbar();

        final Context context = this;

        communityApiAdapter = new CommunityApiAdapter(this, null);

        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null) {
            Log.d(getClass().getSimpleName(), data.toString());

            String address = data.getQueryParameter("address");
            if (address != null) {
                Toast.makeText(context, "TODO: buscar la comunidad en la blockchain y agregarla a la base de datos", Toast.LENGTH_SHORT).show();
            }
        } else {

            FloatingActionButton addButton = findViewById(R.id.community_add);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Toast working = Toast.makeText(context, "Creando organización...", Toast.LENGTH_LONG);
                    working.show();

                    try {
                        Log.i(context.getClass().getSimpleName(), "Creando organización...");

                        TextView nameTextView = findViewById(R.id.community_name);

                        Community community = new Community();
                        community.id = UUID.randomUUID();
                        community.name = nameTextView.getText().toString();
                        community.createAt = System.currentTimeMillis();

                        Signer signer = new Signer();

                        signer.sign(community);

                        communityApiAdapter.add(community, new RequestListener<Community>() {
                            @Override
                            public void onComplete(Community response) {
                                working.cancel();

                                Toast.makeText(context, "Organización creada! :)", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, CommunityListActivity.class);
                                context.startActivity(intent);

                                finish();
                            }

                            @Override
                            public void onError(int statusCode) {
                                working.cancel();
                                Toast.makeText(context, "Error " + statusCode + " agregando la organización :(", Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (Exception ex) {
                        working.cancel();
                        Log.e("CommunityAdd", ex.getMessage(), ex);
                        Toast.makeText(context, "Error en enviando Comunidad: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}