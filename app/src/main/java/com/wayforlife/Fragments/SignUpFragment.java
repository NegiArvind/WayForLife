package com.wayforlife.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.wayforlife.Activities.LoginActivity;
import com.wayforlife.GlobalStateApplication;
import com.wayforlife.Models.User;
import com.wayforlife.R;
import com.wayforlife.Utils.AuthUtil;

import java.util.ArrayList;

/** This fragment is used for signUp the user */
public class SignUpFragment extends Fragment {

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText numberEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Spinner citySpinner;
    private Spinner stateSpinner;
    private ImageView visibilityConfirmPasswordImageView;
    private ImageView visibilityPasswordImageView;
    private Button sendOtpButton;
    private ArrayList<User> users;
    private String firstName,email,number,password,confirmPassword,lastName,state,city;
    private LoginActivity loginActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Inflating layout file for this fragment
        View view=inflater.inflate(R.layout.sign_up_fragment_layout,container,false);

        loginActivity=(LoginActivity)getActivity();
        users=new ArrayList<>();

        fetchAllUsers(); //it will fetch all the users present in the database

        //Initializing all the widget
        firstNameEditText=view.findViewById(R.id.firstNameEditText);
        lastNameEditText=view.findViewById(R.id.lastNameEditText);
        emailEditText=view.findViewById(R.id.emailEditText);
        numberEditText=view.findViewById(R.id.numberEditText);
        passwordEditText=view.findViewById(R.id.passwordEditText);
        confirmPasswordEditText=view.findViewById(R.id.confirmPasswordEditText);
        citySpinner=view.findViewById(R.id.citySpinner);
        stateSpinner=view.findViewById(R.id.stateSpinner);
        visibilityConfirmPasswordImageView=view.findViewById(R.id.visibilityConfirmPasswordImageView);
        visibilityPasswordImageView=view.findViewById(R.id.visibilityPasswordImageView);
        sendOtpButton=view.findViewById(R.id.sendOtpButton);

        sendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyAndSendOtp();
            }
        });


        return view;
    }

    private void fetchAllUsers() {
        users = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);
                users.add(user);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /** This below function will first verify the user details and then send otp to user entered number*/

    private void verifyAndSendOtp() {
        if(isAllDetailsCorrect()){
            if(!isUserAlreadyExist()){

                User user=new User();
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setEmail(email);
                user.setPhoneNumber(number);
                user.setCityName(city);
                user.setStateName(state);
                user.setPassword(password);

                //if user is new then we will go for verification of number.
                loginActivity.addNewFragment(VerificationFragment.newInstance(user));
            }else{
                Toast.makeText(loginActivity,"You are already registered. Please login",Toast.LENGTH_LONG).show();
                loginActivity.addNewFragment(LoginFragment.newInstance());
            }
        }
    }

    private boolean isUserAlreadyExist() {

        for(User user:GlobalStateApplication.usersHashMap.values()){

            /** if entered email or mobile number matched with the email or number present in users database
            then it means users has already sign up.*/

            if(user.getEmail().equals(email)||user.getPhoneNumber().equals(number)){
                return true;
            }
        }
        return false;
    }

    private boolean isAllDetailsCorrect() {
        firstName=firstNameEditText.getText().toString().trim();
        email=emailEditText.getText().toString();
        number=numberEditText.getText().toString().trim();
        password=passwordEditText.getText().toString();
        confirmPassword=confirmPasswordEditText.getText().toString();
        if(firstName.equals("")){
            firstNameEditText.setError("Please Enter first name");
            firstNameEditText.requestFocus();
            return false;
        }

        if(!AuthUtil.isValidEmail(email)){
            emailEditText.setError("Please Enter valid email");
            emailEditText.requestFocus();
            return false;
        }

        if(!AuthUtil.isVailidPhone(number)){
            numberEditText.setError("Please Enter valid number");
            numberEditText.requestFocus();
            return false;
        }

        if(password.length()<6){
            passwordEditText.setError("Password must contain at least 6 characters");
            passwordEditText.requestFocus();
            return false;
        }

        if(!confirmPassword.equals(password)){
            confirmPasswordEditText.setError("Password Mismatched");
            confirmPasswordEditText.requestFocus();
            return false;
        }

        return true;

    }

    public static SignUpFragment newInstance() {

        Bundle args = new Bundle();
        SignUpFragment fragment = new SignUpFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
