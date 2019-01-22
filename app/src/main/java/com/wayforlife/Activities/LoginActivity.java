package com.wayforlife.Activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.wayforlife.Fragments.LoginAndSignUpFragment;
import com.wayforlife.R;
import android.util.Log;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Inside ","activity class");
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_login);
        addNewFragment(LoginAndSignUpFragment.newInstance());
    }

    public void addNewFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.loginFrameLayout,fragment).commit();
    }
}
