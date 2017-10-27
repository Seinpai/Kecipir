package com.kecipir.kecipir;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kecipir.kecipir.adapter.NotificationAdapter;
import com.kecipir.kecipir.data.Notification;

import java.util.List;

public class NotificationDetailActivity extends AppCompatActivity {
    Toolbar toolbar;


    String id_user, email;

    WebView webView;;
    private ProgressDialog progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView = (WebView) findViewById(R.id.web_notif);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        Bundle b = getIntent().getExtras();
        getSupportActionBar().setTitle(b.getString("title"));

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        progressBar = ProgressDialog.show(NotificationDetailActivity.this, "Mohon Tunggu", "Loading...");

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i("", "Processing webview url click...");
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                Log.i("", "Finished loading URL: " +url);
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e("", "Error: " + description);
                Toast.makeText(NotificationDetailActivity.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();
//                alertDialog.show();
            }
        });
        webView.loadUrl(b.getString("url"));
    }
}
