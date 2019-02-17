package com.wayforlife.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.wayforlife.Models.User;
import com.wayforlife.R;

import java.util.Objects;

public class ProfilePhotoDialogFragment extends DialogFragment {

    private ImageView profilePhotoImageView;
    private Toolbar profilePhotoToolbar;
    private Context context;
    private ProgressBar profilePhotoProgressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,R.style.AppTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.profile_photo_dialog_fragment_layout,container,false);
        profilePhotoImageView=view.findViewById(R.id.profilePhotoImageView);
        profilePhotoToolbar=view.findViewById(R.id.profilePhotoToolbar);
        profilePhotoProgressBar=view.findViewById(R.id.profilePhotoProgressBar);
        profilePhotoImageView.getLayoutParams().height= ViewGroup.LayoutParams.MATCH_PARENT;

        context=getContext();
        profilePhotoToolbar.setTitleTextColor(getResources().getColor(R.color.md_white_1000));
        profilePhotoToolbar.setTitle("Profile photo");
        profilePhotoToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if(User.currentUser.getImageUrl()!=null){
            Picasso.with(context).load(User.currentUser.getImageUrl()).into(profilePhotoImageView, new Callback() {
                @Override
                public void onSuccess() {
                    if(profilePhotoProgressBar!=null) {
                        profilePhotoProgressBar.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onError() {

                }
            });
        }else{
            if(profilePhotoProgressBar!=null) {
                profilePhotoProgressBar.setVisibility(View.GONE);
            }
            profilePhotoImageView.setImageResource(R.drawable.person_image);
        }
        return view;
    }

    public static ProfilePhotoDialogFragment newInstance() {

        Bundle args = new Bundle();
        ProfilePhotoDialogFragment fragment = new ProfilePhotoDialogFragment();
        fragment.setArguments(args);
        return fragment;
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
