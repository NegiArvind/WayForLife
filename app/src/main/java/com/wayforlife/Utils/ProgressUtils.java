package com.wayforlife.Utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.ProgressBar;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.wayforlife.R;

import java.util.Objects;

public class ProgressUtils {

    private static KProgressHUD kProgressHUD;
    private static Dialog progressDialog;

    public static void showKProgressDialog(Context context,String message){
        kProgressHUD=KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setDetailsLabel(message)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
    }

    public static void cancelKprogressDialog(){
        if(kProgressHUD!=null && kProgressHUD.isShowing())
            kProgressHUD.dismiss();
    }

    public static void showProressBarDialog(Context context) {
        if (!(progressDialog != null && progressDialog.isShowing())) {
            progressDialog = new Dialog(context); // second argument is used toset the color of progress bar
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setContentView(R.layout.progress_bar);
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    }

    public static void cancelProgressBarDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.cancel();
    }

}
