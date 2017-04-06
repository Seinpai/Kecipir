package com.kecipir.kecipir;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.astuetz.PagerSlidingTabStrip;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kecipir.kecipir.data.AppConfig;
import com.kecipir.kecipir.data.AppController;
import com.kecipir.kecipir.data.Utils;
import com.kecipir.kecipir.fragment.MenuCategoryFragment;
import com.kecipir.kecipir.fragment.TabMainFragment;
import com.kecipir.kecipir.gcm.MyFcmListenerService;
import com.kecipir.kecipir.gcm.MyInstanceIDListenerService;
import com.kecipir.kecipir.gcm.RegistrationIntentService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView titleCategory, txtCategory, txtTglPanen;
    ImageView btnCategory;
    RelativeLayout btnCategoryText;
    LinearLayout layoutTglPanen;

    SessionManager sessionManager;
    HashMap<String, String> user;
    Spinner spinnerCategory;

    String host;
    String email;
    String password;
    int position;

    int menuCategoryOff;
    String currentCategory;


    private int mNotificationCount;
    private Tracker mTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        checkVersion();

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        if (savedInstanceState==null)
        {
            position = 0;
            getSupportFragmentManager().beginTransaction().add(R.id.home_replace, new TabMainFragment().newInstance(position))
                    .commit();
        }

        btnCategory = (ImageView) findViewById(R.id.btn_category);
        btnCategoryText = (RelativeLayout) findViewById(R.id.btn_category_text);
//        txtCategory = (TextView) findViewById(R.id.txt_category);
        txtTglPanen = (TextView) findViewById(R.id.txt_tglpanen);
//        titleCategory = (TextView) findViewById(R.id.current_category);
        layoutTglPanen = (LinearLayout) findViewById(R.id.layout_tglpanen);
        spinnerCategory = (Spinner) findViewById(R.id.spinner_category);
//        btnCategory.setImageResource(R.drawable.ic_btn_kategori);

        AppController appController = (AppController) getApplication();
        mTracker = appController.getDefaultTracker();
        sendScreenName();

//        txtCategory.setText("Seuua");
        List<String> pengantaranList = new ArrayList<>();

        pengantaranList.add("Semua");
        pengantaranList.add("Paket");
        pengantaranList.add("Sayur Daun");
        pengantaranList.add("Sayur Buah");
        pengantaranList.add("Buah");
        pengantaranList.add("Bumbu");
        pengantaranList.add("Extra");
        pengantaranList.add("Herbal");
        pengantaranList.add("Terlaris");

        ArrayAdapter<String> pengantaranAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pengantaranList);
        pengantaranAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(pengantaranAdapter);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                position = pos;
                FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.home_replace, new TabMainFragment().newInstance(position));
                fragmentTransaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.commit();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        setMenuCategory(0);

//        btnCategoryText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                if (getMenuCategory() == 0) {
//                    setMenuCategory(1);
//                    fragmentTransaction.add(R.id.home_replace, new MenuCategoryFragment(), "addfragment");
//                    fragmentTransaction.addToBackStack(null);
//                    fragmentTransaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                    fragmentTransaction.commit();
//                    Log.d("Category", "Added");
//                } else {
//                    setMenuCategory(0);
//                    fragmentManager.popBackStack();
//                    Log.d("Category", "Removed");
//                }
//            }
//        });

        sessionManager = new SessionManager(this);
        user = sessionManager.getUser();

        if (sessionManager.isLoggedIn()){
            startService(new Intent(this, MyInstanceIDListenerService.class));
            cekCart(user.get("email"), user.get("uid"));
            sendRegistrationToServer(user.get("uid"), user.get("email"), FirebaseInstanceId.getInstance().getToken());
        }


    }

    private void sendRegistrationToServer(final String id_user, final String email, final String token) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Daftar GCM Response: " + response.toString());
                try {

                    JSONObject jsonObject = new JSONObject(response);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Daftar GCM Response " + error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("tag", "daftargcm");
                params.put("id_user", id_user);
                params.put("email", email);
                params.put("token", token);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s","green","web-indonesia");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }

        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(AppConfig.TIMEOUT_NETWORK, AppConfig.RETRY_NETWORK, AppConfig.MULTI_NETWORK));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void setMenuCategory(int menuCategory){
       this.menuCategoryOff = menuCategory;
    }

    public int getMenuCategory(){
        return menuCategoryOff;
    }

    public void setTitleCategory(String currentCategory){
//        txtCategory.setText(currentCategory);
    }
    public void setTanggalPanen(String currentCategory){
        layoutTglPanen.setVisibility(View.VISIBLE);
        txtTglPanen.setText(currentCategory);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        MenuItem item = menu.findItem(R.id.action_cart);
        LayerDrawable icon = (LayerDrawable) item.getIcon();

        Utils.setBadgeCount(MainActivity.this, icon, mNotificationCount);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cart) {
            if (sessionManager.isLoggedIn()){
                Intent intent = new Intent(MainActivity.this, ShoppingCartActivity.class);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(MainActivity.this, MemberStartActivity.class);
                startActivity(intent);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateNotificationsBadge(int count) {
        mNotificationCount = count;

        // force the ActionBar to relayout its MenuItems.
        // onCreateOptionsMenu(Menu) will be called again.
        invalidateOptionsMenu();
    }

    public class FetchCountTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            // example count. This is where you'd
            // query your data store for the actual count.
            SharedPreferences sharedPreferences = getSharedPreferences("CART", MODE_PRIVATE);

            return Integer.parseInt(sharedPreferences.getString("jml_cart", null));
        }

        @Override
        public void onPostExecute(Integer count) {
            updateNotificationsBadge(count);
        }
    }

    public void loadBadge(){
        new FetchCountTask().execute();
    }

    private void cekCart(final String email, final String id_user) {

        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Cekcart Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        mNotificationCount = jObj.getInt("total_cart");

                        SharedPreferences jmlCart = getSharedPreferences("CART", MODE_PRIVATE);
                        SharedPreferences.Editor editor;
                        editor = jmlCart.edit();
                        editor.putString("jml_cart", mNotificationCount + "");
                        editor.apply();
                        new FetchCountTask().execute();
//                        relativeLayout.setVisibility(View.INVISIBLE);

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                        retry();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    retry();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "CekCart Error: " + error.getMessage());
                Toast.makeText(MainActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
               retry();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "cekcart");
                params.put("email", email);
                params.put("id_user", id_user);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s","green","web-indonesia");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }

        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(AppConfig.TIMEOUT_NETWORK, AppConfig.RETRY_NETWORK, AppConfig.MULTI_NETWORK));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void checkVersion() {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Login Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean important = jObj.getBoolean("important");
                    String versi = jObj.getString("version");
                    int code = jObj.getInt("code");
                    PackageInfo pInfo = null;
                    try {
                        pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        String version = pInfo.versionName;
                        int codeApp = pInfo.versionCode;
                        if (codeApp >= code){

                        }else {
                            if (important) {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Update Aplikasi")
                                        .setMessage("Ada Versi Terbaru di Google Play")
                                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                                try {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                } catch (android.content.ActivityNotFoundException anfe) {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                                                }
                                            }
                                        })

                                        .setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        finish();
                                                    }
                                                }
                                        )
                                        .setCancelable(false)
                                        .show();
                            } else {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Update Aplikasi")
                                        .setMessage("Ada Versi Terbaru di Google Play")
                                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                                try {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                } catch (android.content.ActivityNotFoundException anfe) {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                                                }
                                            }
                                        })
                                        .setNegativeButton("Nanti saja", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }
                                        )
                                        .setCancelable(false)
                                        .show();
                            }
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                } catch (JSONException e) {
                    // JSON error
//                    retry();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Login Error: " + error.getMessage());
                retry();
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "lastest_version");

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s","green","web-indonesia");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }

        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(AppConfig.TIMEOUT_NETWORK, AppConfig.RETRY_NETWORK, AppConfig.MULTI_NETWORK));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }



    @Override
    public void onBackPressed(){
            new AlertDialog.Builder(this)
                    .setTitle("Keluar Aplikasi")
                    .setMessage("Apa Anda yakin ingin menutup aplikasi?")
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();

    }

    @Override
    public void onStart(){
        super.onStart();
        if (sessionManager.isLoggedIn()){
            cekCart(user.get("email"), user.get("uid"));
        }
        else {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }


    private void retry() {

        new AlertDialog.Builder(this)
                .setTitle("Koneksi Bermasalah saat Cek Cart")
                .setMessage("Coba lagi")
                .setPositiveButton("Coba lagi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cekCart(user.get("email"), user.get("uid"));
                    }
                })
                .setNegativeButton("batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .show();
    }

    private void sendScreenName() {
        String name = "Store List";

        // [START screen_view_hit]
        Log.i("MainActivity", "Screen name: " + name);
        mTracker.setScreenName("Screen" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }

}
