package com.wayforlife.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.wayforlife.Activities.HomeActivity;
import com.wayforlife.Activities.LoginActivity;
import com.wayforlife.Models.SerializeUser;
import com.wayforlife.Models.User;
import com.wayforlife.R;
import com.wayforlife.Utils.ProgressUtils;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VerificationFragment extends DialogFragment implements View.OnClickListener {

    private EditText otpEditText;
    private TextView verificationTextView;
    private ProgressBar progressBar;
    private Button resendOtpButton;
    private Button signUpButton;
    private User user;
    private PhoneAuthCredential phoneAuthCredential;
    private String verificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private Boolean isPhoneNumberVerified=false;
    private Context context;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userDatabaseReference;
    private LoginActivity loginActivity;
    private HomeActivity homeActivity;
    private SerializeUser serializeUser;
    private boolean isEditProfile=false;
    private String oldSubscribeTopic;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,R.style.AppTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.verification_fragment_layout,container,false);

        context=getContext();
        firebaseAuth=FirebaseAuth.getInstance();
        userDatabaseReference=FirebaseDatabase.getInstance().getReference("Users");
        otpEditText=view.findViewById(R.id.enterOtpEditText);
        verificationTextView=view.findViewById(R.id.textView);
        progressBar=view.findViewById(R.id.progressBar);
        signUpButton=view.findViewById(R.id.signUpButton);
        resendOtpButton=view.findViewById(R.id.resendOtpButton);

        if (getArguments() != null) {
            serializeUser = (SerializeUser) getArguments().getSerializable("user");
            isEditProfile=getArguments().getBoolean("isEditProfile");
            if(isEditProfile){
                oldSubscribeTopic=getArguments().getString("oldSubscribeTopic");
                homeActivity=(HomeActivity)getActivity();
                signUpButton.setText("Update");
            }else{
                loginActivity= (LoginActivity) getActivity();
                signUpButton.setText("Sign Up");
            }

        }


        signUpButton.setOnClickListener(this);
        resendOtpButton.setOnClickListener(this);

        mCallBacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                //Getting the code sent by SMS
                String code = phoneAuthCredential.getSmsCode();

                //sometime the code is not detected automatically
                //in this case the code will be null
                //so user has to manually enter the code
                if (code != null) {

                    otpEditText.setText(code);
                    otpEditText.setFocusable(false);
                    otpEditText.setFocusableInTouchMode(false);

                    //generating credential

                    Log.i("Code completed",code);
                    //verifying the code
                    verifyPhoneNumberWithCode(code);
                    isPhoneNumberVerified=true;
                    progressBar.setVisibility(View.INVISIBLE);

                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                Log.i("verification code",s);
                verificationId=s;
                resendingToken=forceResendingToken;
            }
        };
        if(serializeUser!=null) {
            user = serializeUser.getUser();
            startPhoneNumberVerification(user.getPhoneNumber());
        }

        return view;
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        Log.i("user phonenumber",phoneNumber);
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91"+phoneNumber
            ,60
            ,TimeUnit.SECONDS
            ,TaskExecutors.MAIN_THREAD
            ,mCallBacks);
    }

    public static VerificationFragment newInstance(User user,boolean isEditProfile,String oldSubscribeTopic) {
        Bundle args = new Bundle();
        SerializeUser serializeUser=new SerializeUser(user);
        args.putSerializable("user",serializeUser);
        args.putBoolean("isEditProfile",isEditProfile);
        args.putString("oldSubscribeTopic",oldSubscribeTopic);
        VerificationFragment fragment = new VerificationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.signUpButton){
            if(!isPhoneNumberVerified){
                if(otpEditText.getText().toString().trim().length()!=0) {
                    verifyPhoneNumberWithCode(otpEditText.getText().toString().trim());
                }
                else{
                    Toast.makeText(context,"Please enter otp first",Toast.LENGTH_SHORT).show();
                }
            }
        }else if(v.getId()==R.id.resendOtpButton){
            resendAlertDialog();
        }
    }

    private void resendAlertDialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
        alertDialog.setTitle("Resend OTP")
                .setMessage("Do you want to resend the OTP ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isPhoneNumberVerified=false;
                        resendVerificationCode(user.getPhoneNumber(),resendingToken);
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }


    private void registerUserWithCredential() {
        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(),user.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Objects.requireNonNull(firebaseAuth.getCurrentUser()).linkWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Log.i("Phone number ","linked");
                                User.setCurrentUser(user);
                                makeUserEntryIntoFirebaseDatabase();

                            }else{
                                firebaseAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            ProgressUtils.cancelKprogressDialog();
                                            Toast.makeText(context,"Please Enter a valid OTP",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                Toast.makeText(context, "Sign Up Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    ProgressUtils.cancelKprogressDialog();
                    Toast.makeText(context,Objects.requireNonNull(task.getException()).toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void makeUserEntryIntoFirebaseDatabase() {
        userDatabaseReference.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    ProgressUtils.cancelKprogressDialog();
                    dismiss();
                    if(isEditProfile){
                        if(oldSubscribeTopic!=null) {
                            final String newTopic = user.getCityName() + '_' + user.getStateName();
                            Log.i("newTopic",newTopic);
                            Log.i("oldSubscribeTopic",oldSubscribeTopic);
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(oldSubscribeTopic.replace(' ', '_')).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        FirebaseMessaging.getInstance().subscribeToTopic(newTopic.replace(' ', '_'));
                                        Log.i("new Topic update","in verification fragment");
                                    }
                                }
                            });
                        }
                        Toast.makeText(context, "Profile successfully edited", Toast.LENGTH_SHORT).show();
                        homeActivity.addNewFragment(EditProfileFragment.newInstance(),getResources().getString(R.string.editProfileFragmentTag));
                    }else {
                        Toast.makeText(context, "Sign Up successful.", Toast.LENGTH_SHORT).show();
                        loginActivity.addNewFragment(WelcomeFragment.newInstance(),getString(R.string.welcomeFragmentTag));
                    }
                }
            }
        });
    }

    private void verifyPhoneNumberWithCode(String otp){
        if(verificationId!=null) {
            progressBar.setVisibility(View.INVISIBLE);
            phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, otp);
//          verificationTextView.setText(getString(R.string.number_verified_message));
            ProgressUtils.showKProgressDialog(context,"Getting you in");
            if(isEditProfile){
                updateUserWithNewCredential();
            }else {
                registerUserWithCredential();
            }
        }else{
            Toast.makeText(context,"Please enter valid OTP",Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserWithNewCredential() {
        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).updatePhoneNumber(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.i("Phone number ","linked");
                    User.setCurrentUser(user);
                    makeUserEntryIntoFirebaseDatabase();
                }else{
                    ProgressUtils.cancelKprogressDialog();
                    Toast.makeText(context,"Please Enter a valid OTP",Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "Update Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,
                mCallBacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);
        }
    }

}
