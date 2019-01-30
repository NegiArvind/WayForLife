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

/** This is a full screen dialog fragment. Here user can add a new poll by filling the details*/
public class AddPollDialogFragment extends DialogFragment {

    private Toolbar addPollToolbar;
    private EditText titlePollEditText,contentPollEditText,optionOnePollEditText,optionTwoPollEditText;
    private Button submitPollButton;
    private Context context;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,R.style.AppTheme);
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

        View view=inflater.inflate(R.layout.add_poll_dialog_fragment_layout,container,false);
        context=getContext();
        addPollToolbar=view.findViewById(R.id.addPollToolbar);
        addPollToolbar.setTitle("Add your poll");
        addPollToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        titlePollEditText=view.findViewById(R.id.titlePollEditText);
        contentPollEditText=view.findViewById(R.id.contentPollEditText);
        optionOnePollEditText=view.findViewById(R.id.optionOnePollEditText);
        optionTwoPollEditText=view.findViewById(R.id.optionTwoPollEditText);

        submitPollButton=view.findViewById(R.id.pollSubmitButton);

//        contentPollEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                postLetterCountingTextView.setText(String.valueOf(s.length())+"/150");
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

        submitPollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allDetailsAreValid()) {
                    ProgressUtils.showKProgressDialog(context,"Submitting...");
                    addPollIntoFirebaseDatabase();
                }
            }
        });
        return view;
    }

    private boolean allDetailsAreValid() {
        if(titlePollEditText.getText().toString().trim().length()==0){
            titlePollEditText.setError("Please enter the title");
            titlePollEditText.requestFocus();
            return false;
        }
        if(contentPollEditText.getText().toString().trim().length()==0){
            contentPollEditText.setError("Please enter the content");
            contentPollEditText.requestFocus();
            return false;
        }
        if(optionOnePollEditText.getText().toString().trim().length()==0){
            optionOnePollEditText.setError("Please enter your first choice");
            optionOnePollEditText.requestFocus();
            return false;
        }
        if(optionTwoPollEditText.getText().toString().trim().length()==0){
            optionTwoPollEditText.setError("Please enter your second choice");
            optionTwoPollEditText.requestFocus();
            return false;
        }
        return true;
    }

    //Adding new poll into firebase database.
    private void addPollIntoFirebaseDatabase() {

        //poll and post are same.
        Post post=new Post();
        post.setTitle(titlePollEditText.getText().toString());
        /**I haven't created two different models for post and poll. I have used same post model in poll also. I am adding option1 and option2
         * with content and distinguish them by putting some pattern in between them. When i will fetch this poll i will get the option1 and
         * option2 substring from the content*/
        post.setContent(contentPollEditText.getText().toString()+CommonData.firstPatternWord
                +optionOnePollEditText.getText().toString()+CommonData.secondPatternWord +optionTwoPollEditText.getText().toString());
        post.setNoOfLikes(0);
        post.setTimeDate(CommonData.getCurrentTime()+"   "+CommonData.getTodayDate());
        post.setUserId(CommonData.firebaseCurrentUserUid);
        post.setPost(false); //This is not post so that's why i put it false here
        GlobalStateApplication.feedsDatabaseReference.push().setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    if(task.isSuccessful()){
                        ProgressUtils.cancelKprogressDialog();
                        Toast.makeText(context,"Submitted Successfully",Toast.LENGTH_SHORT).show();
                        dismiss();
                    }else{
                        ProgressUtils.cancelKprogressDialog();
                        Toast.makeText(context,"Unsuccessful Submission "+ task.getException(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public static AddPollDialogFragment newInstance() {

        Bundle args = new Bundle();
        AddPollDialogFragment fragment = new AddPollDialogFragment();
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
