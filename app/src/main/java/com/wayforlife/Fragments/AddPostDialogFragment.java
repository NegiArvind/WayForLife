package com.wayforlife.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.wayforlife.Common.CommonData;
import com.wayforlife.GlobalStateApplication;
import com.wayforlife.Models.Post;
import com.wayforlife.R;
import com.wayforlife.Utils.ProgressUtils;

import java.util.Objects;

/** This is a full screen dialog fragment. Here user can add a new post by filling the details*/
public class AddPostDialogFragment extends DialogFragment {

    private Toolbar addPostToolbar;
    private EditText titlePostEditText,descriptionPostEditText;
    private TextView postLetterCountingTextView;
    private Button postButton;
    private Context context;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,R.style.AppTheme);//setting the style for this dialog fragment
    }

//    @NonNull
//    @Override
//    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        Dialog dialog=super.onCreateDialog(savedInstanceState);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        return dialog;
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.add_post_dialog_fragment_layout,container,false);
        context=getContext();
        addPostToolbar=view.findViewById(R.id.addPostToolbar);
        addPostToolbar.setTitle("Add your post"); //Setting the title
        addPostToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        titlePostEditText=view.findViewById(R.id.titlePostEditText);
        descriptionPostEditText=view.findViewById(R.id.descriptionPostEditText);
        postLetterCountingTextView=view.findViewById(R.id.postLetterCountingTextView);
        postButton=view.findViewById(R.id.postButton);

        //This below method is used to count number of character user has written in the editText.
        descriptionPostEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                postLetterCountingTextView.setText(String.valueOf(s.length())+"/250"); //This sets a message which tells user number of word he has typed.
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allDetailsAreValid()) {
                    ProgressUtils.showKProgressDialog(context,"posting...");
                    addPostIntoFirebaseDatabase();
                }
            }
        });
        return view;
    }

    private boolean allDetailsAreValid() {
        if(titlePostEditText.getText().toString().trim().length()==0){
            titlePostEditText.setError("Please enter the title");
            titlePostEditText.requestFocus();
            return false;
        }
        if(descriptionPostEditText.getText().toString().trim().length()==0){
            descriptionPostEditText.setError("Please enter the description");
            descriptionPostEditText.requestFocus();
            return false;
        }
        return true;
    }

    //This below method will add a new post into firebase database
    private void addPostIntoFirebaseDatabase() {
        Post post=new Post();
        post.setTitle(titlePostEditText.getText().toString());
        post.setContent(descriptionPostEditText.getText().toString());
        post.setNoOfLikes(0);
        post.setPost(true);
        post.setTimeDate(CommonData.getCurrentTime()+"   "+CommonData.getTodayDate());
        post.setUserId(CommonData.firebaseCurrentUserUid);
        GlobalStateApplication.feedsDatabaseReference.push().setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    if(task.isSuccessful()){
                        ProgressUtils.cancelKprogressDialog();
                        Toast.makeText(context,"Successfully Posted",Toast.LENGTH_SHORT).show();
                        dismiss();
                    }else{
                        ProgressUtils.cancelKprogressDialog();
                        Toast.makeText(context,"Posted not successfully "+ task.getException(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public static AddPostDialogFragment newInstance() {
        Bundle args = new Bundle();
        AddPostDialogFragment fragment = new AddPostDialogFragment();
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
