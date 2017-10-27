package com.kecipir.kecipir;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kecipir.kecipir.data.AppController;
import com.kecipir.kecipir.fragment.MemberIntroductionFragment;

public class MemberIntroductionActivity extends AppCompatActivity {

    Toolbar toolbar;
    ViewPager viewPager;
    MyPagerAdapter adapter;
    ImageView introJadiHost, introDaftarMember;
    private Tracker mTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_introduction);
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.memberIntroductionVP);
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        introJadiHost = (ImageView) findViewById(R.id.intro_jadihost);
        introDaftarMember = (ImageView) findViewById(R.id.intro_daftaruser);

        AppController appController = (AppController) getApplication();
        mTracker = appController.getDefaultTracker();
        sendScreenName();

        introDaftarMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MemberIntroductionActivity.this, DaftarMemberActivity.class);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Daftar Member from Member Intro")
                        .build());

                startActivity(intent);
                finish();
            }
        });

        introJadiHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MemberIntroductionActivity.this, HostIntroductionActivity.class);
                startActivity(intent);mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Jadi Host from Member intro")
                        .build());

                finish();
            }
        });
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return MemberIntroductionFragment.newInstance("icon_user01", "Pilih Agen terdekat anda");
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return MemberIntroductionFragment.newInstance("icon_user02", "Daftarkan diri anda");
                case 2: // Fragment # 1 - This will show SecondFragment
                    return MemberIntroductionFragment.newInstance("icon_user03", "Belanja produk sehat & murah, gratis pengantaran sampai Agen");
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }

    private void sendScreenName() {
        String name = "Intro Member";

        // [START screen_view_hit]
        Log.i("IntroMemberActivity", "Screen name: " + name);
        mTracker.setScreenName("Screen" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }
}
