package com.example.user.chatclient;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

/**
 * Created by adir schlezinger on 27/05/2015.
 */
public class DevicePagerAdaptor extends FragmentPagerAdapter{

    String[] devices;
    String[] deviceDescription;

    public DevicePagerAdaptor(FragmentManager fm , Context context) {
        super(fm);
        Resources resources = context.getResources();
        devices = resources.getStringArray(R.array.devices);
        deviceDescription = resources.getStringArray(R.array.device_description);
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();

        bundle.putInt(DeviceFragment.BackgroundIDKey ,getDeviceImageID(position) );
        bundle.putInt(DeviceFragment.NavKey ,getnav(position) );
        bundle.putInt(DeviceFragment.BtnVisability,btnVisability(position));
        bundle.putString(DeviceFragment.DescriptionKey , deviceDescription[position]);
        DeviceFragment deviceFragment = new DeviceFragment();
        deviceFragment.setArguments(bundle);
        return deviceFragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return devices[position];
    }

    @Override
    public int getCount() {
        return devices.length;
    }

    public int getDeviceImageID(int position)
    {
        int id=0;
        switch (position)
        {
            case 0:
                id = R.drawable.startbackground5;
                break;
            case 1:
                id = R.drawable.startbackground2;
                break;
            case 2:
                id = R.drawable.startbackground3;
                break;
            case 3:
                id = R.drawable.startbackground4;
                break;
            case 4:
                id = R.drawable.startbackground1;
                break;

        }
        return id;
    }
    public int getnav(int position)
    {
        int id=0;
        switch (position)
        {
            case 0:
                id = R.drawable.startnav1;
                break;
            case 1:
                id = R.drawable.startnav2;
                break;
            case 2:
                id = R.drawable.startnav3;
                break;
            case 3:
                id = R.drawable.startnav4;
                break;
            case 4:
                id = R.drawable.startnav5;
                break;

        }
        return id;
    }
    public int btnVisability(int position)
    {
        int id=0;
        switch (position)
        {
            case 0:
                id = View.GONE;
                break;
            case 1:
                id = View.GONE;
                break;
            case 2:
                id = View.GONE;
                break;
            case 3:
                id = View.GONE;
                break;
            case 4:
                id = View.VISIBLE;
                break;

        }
        return id;
    }
}
