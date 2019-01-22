package com.wayforlife.Utils;

import android.content.Context;

import com.kaopiz.kprogresshud.KProgressHUD;

public class ProgressUtils {

    private static KProgressHUD kProgressHUD;

    public static void showKProgressDialog(Context context){
        kProgressHUD=KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setDetailsLabel("Getting you in")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
    }

    public static void cancelKprogressDialog(){
        if(kProgressHUD!=null && kProgressHUD.isShowing())
            kProgressHUD.dismiss();
    }

}
