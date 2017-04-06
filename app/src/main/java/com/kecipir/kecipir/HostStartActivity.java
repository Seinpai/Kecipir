package com.kecipir.kecipir;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kecipir.kecipir.data.AppController;

public class HostStartActivity extends AppCompatActivity {

    Button mulai, jadiMember, login;
    Toolbar toolbar;

    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_start);
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mulai = (Button) findViewById(R.id.btn_mulai_host);
        jadiMember = (Button) findViewById(R.id.btn_jadimember);
        login = (Button) findViewById(R.id.btn_login_host);

        AppController appController = (AppController) getApplication();
        mTracker = appController.getDefaultTracker();
        sendScreenName();

        mulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HostStartActivity.this, HostIntroductionActivity.class);
                startActivity(i);
            }
        });

        jadiMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HostStartActivity.this, MemberStartActivity.class);
                startActivity(i);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HostStartActivity.this, LoginActivity.class);
                i.putExtra("loginAs", "host");
                startActivity(i);
            }
        });
    }

    private void sendScreenName() {
        String name = "Start Host";

        // [START screen_view_hit]
        Log.i("HostStart", "Screen name: " + name);
        mTracker.setScreenName("Screen" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }
}
