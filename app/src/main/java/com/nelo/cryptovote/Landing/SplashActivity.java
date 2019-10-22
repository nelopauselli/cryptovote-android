package com.nelo.cryptovote.Landing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nelo.cryptovote.Communities.CommunityListActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent main =new Intent(this, CommunityListActivity.class);
        startActivity(main);

        finish();
    }

}
