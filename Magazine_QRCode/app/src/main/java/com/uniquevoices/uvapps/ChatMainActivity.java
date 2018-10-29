package com.uniquevoices.uvapps;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ChatMainActivity extends AppCompatActivity implements MyVoiceFragment.OnFragmentInteractionListener,
        MyChatsFragment.OnFragmentInteractionListener, MyCryptoChatsFragment.OnFragmentInteractionListener, MyGroupFragment.OnFragmentInteractionListener {

    TextView titleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(myPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        titleTextView = (TextView) findViewById(R.id.chatMain_title_textview);
        titleTextView.setText(PrefManager.getInstance(getApplicationContext()).getUserName());
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
