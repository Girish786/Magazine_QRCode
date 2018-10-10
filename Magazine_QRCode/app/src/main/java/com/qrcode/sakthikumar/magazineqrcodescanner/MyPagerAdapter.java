package com.qrcode.sakthikumar.magazineqrcodescanner;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;


public class MyPagerAdapter extends FragmentStatePagerAdapter {
    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MyVoiceFragment();
            case 1:
                return new MyChatsFragment();
            case 2:
                return new MyCryptoChatsFragment();
            case 3:
                return new MyGroupFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "My Voices";
            case 1:
                return "My Chats";
            case 2:
                return "My Crypto Chats";
            case 3:
                return "My Groups";
            default:
                return null;
        }
    }
}