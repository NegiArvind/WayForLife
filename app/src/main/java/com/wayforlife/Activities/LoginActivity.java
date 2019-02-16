package com.wayforlife.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wayforlife.Common.CommonData;
import com.wayforlife.Fragments.LoginAndSignUpFragment;
import com.wayforlife.Fragments.LoginFragment;
import com.wayforlife.Fragments.SignUpFragment;
import com.wayforlife.Fragments.WelcomeFragment;
import com.wayforlife.GlobalStateApplication;
import com.wayforlife.Models.User;
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
        if(FirebaseAuth.getInstance().getCurrentUser()==null) {
            addNewFragment(LoginAndSignUpFragment.newInstance(),getString(R.string.loginAndSignUpFragmentTag));
        }else{
            saveUserDetail();
        }
    }

    private void saveUserDetail() {
        //Below method will call only at once
        GlobalStateApplication.usersDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                if(user!=null) {
                    CommonData.isAdmin = user.isAdmin;
                    User.setCurrentUser(user);
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //if there will be any change in user node then this below listener will set that updated value to currentUser.
        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                User user=dataSnapshot.getValue(User.class);
//                Log.i("user",user.getFirstName());
//                Log.i("user",user.getEmail());
                User.setCurrentUser(dataSnapshot.getValue(User.class));
//                startActivity(new Intent(LoginActivity.this,HomeActivity.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addNewFragment(Fragment fragment,String tag){
        FragmentManager fragmentManager=getSupportFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_left,R.anim.slide_in_right).
                replace(R.id.loginFrameLayout,fragment,tag).commit();
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().findFragmentByTag(getString(R.string.welcomeFragmentTag)) instanceof WelcomeFragment
                ||getSupportFragmentManager().findFragmentByTag(getString(R.string.loginFragmentTag)) instanceof LoginFragment
                ||getSupportFragmentManager().findFragmentByTag(getString(R.string.signUpFragmentTag)) instanceof SignUpFragment){
            addNewFragment(LoginAndSignUpFragment.newInstance(),getString(R.string.loginAndSignUpFragmentTag));
        }else{
            showExitAlertDialog();
        }
    }

    private void showExitAlertDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        finishAffinity();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No",null)
                .show();
    }
}
