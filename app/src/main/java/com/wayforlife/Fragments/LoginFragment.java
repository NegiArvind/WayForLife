package com.wayforlife.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.wayforlife.Activities.LoginActivity;
import com.wayforlife.GlobalStateApplication;
import com.wayforlife.Models.User;
import com.wayforlife.R;
import com.wayforlife.Utils.AuthUtil;
import com.wayforlife.Utils.ProgressUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** This fragment is used for user login. Whenever user presses login button then this fragment will be opened*/

public class LoginFragment extends Fragment implements View.OnClickListener {

    private EditText emailLoginEditText;
    private EditText passwordLoginEditText;
    private List<User> users;
    private boolean isPhoneNumber;
    private boolean isEmail;
    private String username="Sample@test.com";
    private FirebaseAuth firebaseAuth;
    private LoginActivity loginActivity;
    private Button loginButton;
    private TextView forgetPasswordTextView;
    private EditText forgetEmailEditText;
    private Button sendResetPasswordLinkButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.login_fragment_layout,container,false);

        loginActivity= (LoginActivity) getActivity();
        firebaseAuth=FirebaseAuth.getInstance();

        //Finding all the widget with their corresponding id
        emailLoginEditText=view.findViewById(R.id.loginEmailEditText);
        passwordLoginEditText=view.findViewById(R.id.loginPasswordEditText);
        loginButton=view.findViewById(R.id.loginButton);
        forgetPasswordTextView=view.findViewById(R.id.forgetPasswordTextView);

        loginButton.setOnClickListener(this);
        forgetPasswordTextView.setOnClickListener(this);

        return view;
    }

    //This below method returns the object of this fragment
    public static LoginFragment newInstance() {
        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.loginButton){
            //if entered details are valid then we will sign in the user.
            if(isDetailsValid()){
                ProgressUtils.showKProgressDialog(loginActivity);
                signIn();
            }
        }else if(v.getId()==R.id.forgetPasswordTextView){
            recoverPassword();
        }
    }


    /** This below function will validate the user entered detail for login.*/
    public boolean isDetailsValid(){

        isPhoneNumber= AuthUtil.isVailidPhone(emailLoginEditText.getText());
        isEmail=AuthUtil.isValidEmail(emailLoginEditText.getText());

        if(isEmail||isPhoneNumber){
            username = emailLoginEditText.getText().toString();
        }else{
            emailLoginEditText.setError("Enter a valid Email/Password");
            emailLoginEditText.requestFocus();
            return false;
        }

        if(passwordLoginEditText.getText().toString().trim().length()<6)
        {
            passwordLoginEditText.setError("Password should have at least 6 characters");
            passwordLoginEditText.requestFocus();
            return false;
        }

        return true;

    }


    /** In below method i am signing the user with email and password. if user entered phone number then using this phone
     * number i am going to get the email address corresponding to it.This is because, i am login the user to firebase
     * with the help of email and password */
    private void signIn()
    {
        if(isPhoneNumber) {
            username = getEmailForPhoneNumber();
        }
        firebaseAuth.signInWithEmailAndPassword(username,passwordLoginEditText.getText().toString().trim())
                .addOnCompleteListener(loginActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
//                            loginActivity.initCurrentUser();
                            Toast.makeText(loginActivity, "Signed in successfully!", Toast.LENGTH_SHORT).show();
                            ProgressUtils.cancelKprogressDialog();

                        }
                        else
                        {
                            Toast.makeText(loginActivity, "Sign in failed!:"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            ProgressUtils.cancelKprogressDialog();
                        }
                    }
                });
    }

    /**This below function is used to get the email corresponding to entered phone number. I am doing this because
     * i am login the user with email and password */

    String getEmailForPhoneNumber()
    {
        Log.i("My UID",FirebaseAuth.getInstance().getUid());

        for(User user:GlobalStateApplication.usersHashMap.values()){
            Log.i("Users name",user.getFirstName());
        }
        User user=GlobalStateApplication.usersHashMap.get(FirebaseAuth.getInstance().getUid());
        if(user!=null)
            return user.getEmail();

        return "Sample@test.com";
    }



    /**This below method will open a dialog box and will send a password reset link to entered email address.*/
    private void recoverPassword()
    {
        View view=LayoutInflater.from(getActivity()).inflate(R.layout.forget_password_layout,null,false);
        sendResetPasswordLinkButton=view.findViewById(R.id.forgetPasswordButton);
        forgetEmailEditText=view.findViewById(R.id.forgetEmailEditText);
        sendResetPasswordLinkButton.setBackgroundColor(Objects.requireNonNull(getActivity()).getResources().getColor(R.color.colorPrimary));
        final AlertDialog builder=new AlertDialog.Builder(getContext())
                .setTitle("Reset Password")
                .setCancelable(false)
                .setView(view)
                .setNegativeButton("Go Back",null)
                .show();

        sendResetPasswordLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(forgetEmailEditText.getText().toString().trim().equals("")){
                    forgetEmailEditText.setError("Please enter an email first");
                    forgetEmailEditText.requestFocus();
                }
                else {
                    firebaseAuth.sendPasswordResetEmail(forgetEmailEditText.getText().toString()).
                            addOnCompleteListener(loginActivity, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(loginActivity, "A Password reset email has been sent to your email " +
                                        forgetEmailEditText.getText().toString(), Toast.LENGTH_SHORT).show();
                                builder.dismiss();
                            } else {
                                Toast.makeText(loginActivity, "Something went wrong.." + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
