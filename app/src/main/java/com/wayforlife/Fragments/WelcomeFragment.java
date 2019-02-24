package com.wayforlife.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wayforlife.Activities.LoginActivity;
import com.wayforlife.R;

public class WelcomeFragment extends Fragment {

    private TextView descriptionTextView;
    private Button letsGetStartedButton;
    private SliderLayout bannerSliderLayout;
    private FirebaseDatabase firebaseDatabase;
    private LoginActivity loginActivity;
    private ProgressBar welcomeProgressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.welcome_fragment,container,false);

        firebaseDatabase=FirebaseDatabase.getInstance();
        loginActivity= (LoginActivity) getActivity();

        descriptionTextView=view.findViewById(R.id.descriptionTextView);
        letsGetStartedButton=view.findViewById(R.id.letsGetStartedButton);
        bannerSliderLayout=view.findViewById(R.id.bannerSliderLayout);
        welcomeProgressBar=view.findViewById(R.id.welcomeProgressBar);

        fetchWelcomeImages();
        getWelcomeQuotes();

        letsGetStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginActivity.addNewFragment(LoginFragment.newInstance(),getString(R.string.loginFragmentTag));
            }
        });
        return view;
    }

    private void fetchWelcomeImages() {
        firebaseDatabase.getReference("welcomeBanner").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(welcomeProgressBar!=null){
                    welcomeProgressBar.setVisibility(View.GONE);
                }
                setBannerOnLayout(dataSnapshot.getValue(String.class));
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

    private void setBannerOnLayout(String imageUrl) {
        TextSliderView textSliderView=new TextSliderView(getContext());
        textSliderView.image(imageUrl)
                .setScaleType(BaseSliderView.ScaleType.Fit);
        bannerSliderLayout.addSlider(textSliderView);
    }

    private void getWelcomeQuotes() {
        firebaseDatabase.getReference("welcomeQuote").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                descriptionTextView.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static WelcomeFragment newInstance() {

        Bundle args = new Bundle();
        WelcomeFragment fragment = new WelcomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStop() {
        bannerSliderLayout.stopAutoCycle();
        super.onStop();
    }
}
