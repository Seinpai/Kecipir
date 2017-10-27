package com.kecipir.kecipir;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kecipir.kecipir.data.AppController;

import io.smooch.ui.ConversationActivity;

public class AboutUsActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView aboutUs, txtVersi;
//    TextView contactUs;

    String about_us = "Kecipir adalah social enterprise untuk mewujudkan produksi, distribusi dan konsumsi pertanian " +
            "secara lebih berkeadilan dan ramah lingkungan. Impian kami sederhana: Menjadikan sayuran organik itu menjadi " +
            "'sayuran biasa'. Dari sisi harga ia bisa bersaing, dari sisi pasokan ia bisa diandalkan, " +
            "dari sisi konsumsi ia lebih sehat.";
//
//    String contact_us = "Jalan Muri Salim III no 11 Ciputat, Tangerang Selatan Banten 15419<br><br>" +
//            "SMS/Whatsapp : <b>+62 812 1236 9254</b><br><br>" +
//            "Telp: <b>+62 21 7494961</b><br><br>" +
//            "Email : <b>kecipir.info@gmail.com</b>";

    String alamat = "Jalan Muri Salim III no 11 Ciputat, Tangerang Selatan Banten 15419";
    String whatsapp = "081212369254";
    String telp = "0217494961";
    String email = "kecipir.info@gmail.com";

    private Tracker mTracker;

    LinearLayout layoutAlamat, layoutWhatsapp, layoutTelp, layoutEmail;
    TextView txtAlamat, txtWhatsapp, txtTelp, txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        aboutUs = (TextView) findViewById(R.id.txt_about_us);
        txtAlamat = (TextView) findViewById(R.id.txt_alamat);
        txtWhatsapp = (TextView) findViewById(R.id.txt_whatsapp);
        txtTelp = (TextView) findViewById(R.id.txt_telp);
        txtEmail = (TextView) findViewById(R.id.txt_email);
        txtVersi = (TextView) findViewById(R.id.txt_versi);
        layoutAlamat = (LinearLayout) findViewById(R.id.layout_alamat);
        layoutWhatsapp = (LinearLayout) findViewById(R.id.layout_whatsapp);
        layoutTelp = (LinearLayout) findViewById(R.id.layout_telp);
        layoutEmail = (LinearLayout) findViewById(R.id.layout_email);
//        contactUs = (TextView) findViewById(R.id.txt_contact_us);

        AppController appController = (AppController) getApplication();
        mTracker = appController.getDefaultTracker();
        sendScreenName();


        aboutUs.setText(Html.fromHtml(about_us));
        txtAlamat.setText(alamat);
        txtWhatsapp.setText(whatsapp);
        txtEmail.setText(email);
        txtTelp.setText(telp);
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            txtVersi.setText("v. "+version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
//        contactUs.setText(Html.fromHtml(contact_us));
        layoutEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("mailto:" + email)
                        .buildUpon()
                        .build();
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Email")
                        .build());
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
                startActivity(Intent.createChooser(emailIntent, "Send to Kecipir"));
            }
        });

        layoutTelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Telepon")
                        .build());
                String uri = "tel:0217494961" ;
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });



    }

    private void sendScreenName() {
        String name = "Tentang Kami";

        // [START screen_view_hit]
        Log.i("AboutUsActivity", "Screen name: " + name);
        mTracker.setScreenName("Screen" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }
}
