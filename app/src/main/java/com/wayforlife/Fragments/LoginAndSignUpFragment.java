package com.wayforlife.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.wayforlife.Activities.LoginActivity;
import com.wayforlife.Models.User;
import com.wayforlife.R;

/**This fragment only shows the login and sign up button */

public class LoginAndSignUpFragment extends Fragment implements View.OnClickListener {

    private Button loginButton;
    private Button signUpButton;
    private Context context;
    private LoginActivity loginActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.login_and_signup_fragment_layout,container,false);
        context=getContext();
        loginActivity= (LoginActivity) getActivity();
        loginButton=view.findViewById(R.id.loginButton);
        signUpButton=view.findViewById(R.id.signUpButton);
        loginButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
        return view;
    }

    public static LoginAndSignUpFragment newInstance() {
        Bundle args = new Bundle();
        LoginAndSignUpFragment fragment = new LoginAndSignUpFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.loginButton){
            loginActivity.addNewFragment(LoginFragment.newInstance(),getString(R.string.loginFragmentTag));
        }
        else if(v.getId()==R.id.signUpButton){
            loginActivity.addNewFragment(SignUpFragment.newInstance(),getString(R.string.signUpFragmentTag));
//            loginActivity.addNewFragment(WelcomeFragment.newInstance(),getString(R.string.welcomeFragmentTag));
        }
    }
}
