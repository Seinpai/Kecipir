package com.kecipir.kecipir;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kecipir.kecipir.data.AppConfig;
import com.kecipir.kecipir.data.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreditCardActivity extends AppCompatActivity {


    WebView webView;

    SessionManager sessionManager;
    HashMap<String, String> user;

    RelativeLayout frameLoading, frameBuffer;


    Toolbar toolbar;

    String ccUrl;
    String tag, chck_idUser,chck_namaBank, chck_nama, chck_email, chck_alamat, chck_telp, chck_paymentType, chck_deliveryType, chck_ongkir, chck_platform, chck_typeHost, chck_diskon, chck_subTotal, chck_subTotalFinal;
    int finalTotal;

    private Tracker mTracker;


//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card);

        frameLoading = (RelativeLayout) findViewById(R.id.frame_loading);
        frameBuffer = (RelativeLayout) findViewById(R.id.frame_buffer);
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView = (WebView)findViewById(R.id.ccWebView);


        sessionManager = new SessionManager(this);
        user = sessionManager.getUser();
        final String email = user.get("email");
        final String idUser = user.get("uid");

        Log.d("username", email+" - "+idUser);

        final Bundle b = getIntent().getExtras();
        ccUrl = b.getString("url");


        AppController appController = (AppController) getApplication();
        mTracker = appController.getDefaultTracker();
        sendScreenName();




        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportMultipleWindows(true); // This forces ChromeClient enabled.
        webSettings.setSaveFormData(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient() {


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                if (webView.getUrl().contains("KecipirThanks")){
                    Toast.makeText(CreditCardActivity.this, "Sukses", Toast.LENGTH_SHORT).show();
                    webView.setVisibility(View.INVISIBLE);
                    new AlertDialog.Builder(CreditCardActivity.this)
                            .setTitle("Transaksi Sukses")
                            .setMessage("Terimakasih Telah Berbelanja Di Kecipir")
                            .setPositiveButton("Kembali Belanja", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(CreditCardActivity.this, MainActivity.class );
                                    finish();
                                    startActivity(intent);
                                }
                            })
                            .show();
                }
                return false;
            }
        });

        if (Build.VERSION.SDK_INT >= 16) {
//            webView.getSettings().setMixedContentMode( WebSettings.MIXED_CONTENT_ALWAYS_ALLOW );
            webView.loadUrl(ccUrl);

        }else {
            Toast.makeText(CreditCardActivity.this, "Versi Android Tidak Support", Toast.LENGTH_SHORT).show();
        }




//
//
//        readCCCheckout(chck_email,chck_nama,chck_alamat,chck_telp,chck_paymentType,chck_deliveryType,chck_idUser,chck_ongkir
//                , chck_platform, chck_typeHost, chck_diskon, chck_subTotal, finalTotal);
//

//        frameBuffer.setVisibility(View.INVISIBLE);
//        frameLoading.setVisibility(View.INVISIBLE);


//        final Bundle b = getIntent().getExtras();
//        ccUrl = b.getString("url");
//
//

//
//
//        Log.d("res -> ",ccUrl);





    }


//
//    private void readCCCheckout(final String VA_email, final String VA_nama, final String VA_alamat
//            ,final String VA_telp, final String VA_payType, final String VA_delType
//            , final String VA_idUser, final String VA_ongkir, final String VA_platform
//            , final String VA_typeHost, final String VA_diskon, final String VA_subTotal, final int finalTotal)
//    {
//
//        String tag_string_req = "req_login";
//        StringRequest strReq = new StringRequest(Request.Method.POST,
//                AppConfig.URL_NICEPAYCHECKOUT, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d("TAG", "Login Response: " + response.toString());
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    ccUrl = jsonObject.getString("link_cc");
//
//                    Toast.makeText(CreditCardActivity.this, "Mohon tunggu", Toast.LENGTH_SHORT).show();
//
//                    Log.i("link - " ," link-> " + ccUrl);
//
//                    WebSettings webSettings = webView.getSettings();
//                    webSettings.setJavaScriptEnabled(true);
//                    webSettings.setSupportMultipleWindows(true); // This forces ChromeClient enabled.
//                    webSettings.setSaveFormData(true);
//                    webSettings.setDomStorageEnabled(true);
//                    webSettings.setAllowFileAccessFromFileURLs(true);
//                    webSettings.setAllowFileAccess(true);
//                    webSettings.setAllowContentAccess(true);
//                    webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//
//                    webView.setWebChromeClient(new WebChromeClient());
//
//                    webView.setWebViewClient(new WebViewClient() {
//
//
//                        @Override
//                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                            view.loadUrl(url);
//                            if (webView.getUrl().contains("KecipirThanks")){
//                                Toast.makeText(CreditCardActivity.this, "Sukses", Toast.LENGTH_SHORT).show();
//                                webView.setVisibility(View.INVISIBLE);
//                                new AlertDialog.Builder(CreditCardActivity.this)
//                                        .setTitle("Transaksi Sukses")
//                                        .setMessage("Terimakasih Telah Berbelanja Di Kecipir")
//                                        .setPositiveButton("Kembali Belanja", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialogInterface, int i) {
//                                                Intent intent = new Intent(CreditCardActivity.this, MainActivity.class );
//                                                finish();
//                                                startActivity(intent);
//                                            }
//                                        })
//                                        .show();
//                            }
//                            return false;
//                        }
//                    });
//
//                    if (Build.VERSION.SDK_INT >= 17) {
//                        webView.getSettings().setMixedContentMode( WebSettings.MIXED_CONTENT_ALWAYS_ALLOW );
//                        webView.loadUrl(ccUrl);
//
//                    }else {
//                        Toast.makeText(CreditCardActivity.this, "Versi Android Tidak Support", Toast.LENGTH_SHORT).show();
//                    }
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    frameLoading.setVisibility(View.INVISIBLE);
//                }
//
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("TAG", "Login Error: " + error.getMessage());
//                Toast.makeText(CreditCardActivity.this,
//                        error.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        }) {
//
//            @Override
//            protected Map<String, String> getParams() {
//                // Posting parameters to login url
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("tag", "credit_card");
//                params.put("email", VA_email);
//                params.put("nama", VA_nama);
//                params.put("alamat", VA_alamat);
//                params.put("no_telp", VA_telp);
//                params.put("payment_type", VA_payType);
//                params.put("delivery_type", VA_delType);
//                params.put("id_user", VA_idUser);
//                params.put("ongkir", VA_ongkir);
//                params.put("platform", VA_platform);
//                params.put("type_host", VA_typeHost);
//                params.put("diskon", VA_diskon);
//                params.put("subtotal", VA_subTotal);
//                params.put("subtotalfinal", String.valueOf(finalTotal));
//
//
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> params = new HashMap<String, String>();
//                String creds = String.format("%s:%s","green","web-indonesia");
//                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
//                params.put("Authorization", auth);
//                return params;
//            }
//
//        };
//
//        strReq.setRetryPolicy(new DefaultRetryPolicy(AppConfig.TIMEOUT_NETWORK, AppConfig.RETRY_NETWORK, AppConfig.MULTI_NETWORK));
//        // Adding request to request queue
//        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
//    }



    private void sendScreenName() {
        String name = "CreditCardActivity";

        // [START screen_view_hit]
        Log.i("Credit Card", "Screen name: " + name);
        mTracker.setScreenName("Screen" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }
}
