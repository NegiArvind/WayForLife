package com.wayforlife.Common;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class AddFragment {

    public static void AddDifferentFragment(int frameLayoutId,Fragment fragment, FragmentManager fragmentManager){
        if(fragmentManager!=null) {
            fragmentManager.beginTransaction().replace(frameLayoutId, fragment).commit();
        }
    }
}
