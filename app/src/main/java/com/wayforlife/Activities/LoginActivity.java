package com.wayforlife.Activities;

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
import com.wayforlife.Fragments.LoginAndSignUpFragment;
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
            addNewFragment(LoginAndSignUpFragment.newInstance());
        }else{
            saveUserDetail();
        }
    }

    private void saveUserDetail() {
        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                User user=dataSnapshot.getValue(User.class);
//                Log.i("user",user.getFirstName());
//                Log.i("user",user.getEmail());
                User.setCurrentUser(dataSnapshot.getValue(User.class));
                startActivity(new Intent(LoginActivity.this,HomeActivity.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addNewFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.loginFrameLayout,fragment).commit();
    }
}
