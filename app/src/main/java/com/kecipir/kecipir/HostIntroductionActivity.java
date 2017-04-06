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
import com.kecipir.kecipir.fragment.HostIntroductionFragment;
import com.kecipir.kecipir.fragment.MemberIntroductionFragment;

public class HostIntroductionActivity extends AppCompatActivity {

    Toolbar toolbar;
    ViewPager viewPager;
    MyPagerAdapter adapter;

    ImageView introJadiMember;
    ImageView introDaftarHost;


    private Tracker mTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_introduction);
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AppController appController = (AppController) getApplication();
        mTracker = appController.getDefaultTracker();
        sendScreenName();

        viewPager = (ViewPager) findViewById(R.id.hostIntroductionVP);
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        introJadiMember = (ImageView) findViewById(R.id.intro_jadimember);
        introDaftarHost = (ImageView) findViewById(R.id.intro_daftarhost);

        introDaftarHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HostIntroductionActivity.this, DaftarHostActivity.class);
                startActivity(intent);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Daftar Host from Introduction Host")
                        .build());

                finish();
            }
        });

        introJadiMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HostIntroductionActivity.this, MemberIntroductionActivity.class);
                startActivity(intent);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Jadi Member from Introduction Host")
                        .build());

                finish();
            }
        });
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 6;

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
                    return HostIntroductionFragment.newInstance("icon_host01", "Tetangga Anda akan memesan dari Kecipir");
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return HostIntroductionFragment.newInstance("icon_host02", "Petani lokal akan memanen produk yang sudah dipesan");
                case 2: // Fragment # 1 - This will show SecondFragment
                    return HostIntroductionFragment.newInstance("icon_host03", "Kami akan antar hingga rumah Anda");
                case 3: // Fragment # 0 - This will show FirstFragment
                    return HostIntroductionFragment.newInstance("icon_host04", "Kumpulkan pelanggan minimal 3 orang dari tetangga atau rekan kerja terdekat");
                case 4: // Fragment # 0 - This will show FirstFragment different title
                    return HostIntroductionFragment.newInstance("icon_host05", "Anda hanya akan melayanai pengambilan pesanan 2 kali seminggu");
                case 5: // Fragment # 1 - This will show SecondFragment
                    return HostIntroductionFragment.newInstance("icon_host06", "Komisi Sebesar 10% serta beragam manfaat menarik lainnya");
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
        String name = "Intro Host";

        // [START screen_view_hit]
        Log.i("IntroHost", "Screen name: " + name);
        mTracker.setScreenName("Screen" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }
}
