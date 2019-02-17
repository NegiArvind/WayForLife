package com.wayforlife.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.wayforlife.Adapters.CommentCustomArrayAdapter;
import com.wayforlife.Common.CommonData;
import com.wayforlife.GlobalStateApplication;
import com.wayforlife.Models.Comment;
import com.wayforlife.Models.Post;
import com.wayforlife.Models.User;
import com.wayforlife.R;
import com.wayforlife.Utils.ProgressUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**This is also a full screen dialog fragment. When users clicks comment button in feed section then this dialog will
 * be opened. Here he can view all the comments posted on that post.Also he can make his comment on that post.*/
public class DetailsWithCommentDialogFragment extends DialogFragment implements View.OnClickListener {

    private String postKeyId;
    private Context context;
    private ImageView feedUserImageView,feedLikeImageView,feedCommentImageView,currentUserImageView;
    private TextView feedTitleTextView,feedUserNameTextView,feedTimeDateTextView;
    private TextView feedDescriptionReadMoreTextView;
    private LinearLayout optionLinearLayout;
    private TextView optionOneTextView,optionTwoTextView,feedNoOfLikes;
    private RecyclerView commentRecyclerView;
    private Button sendCommentButton;
    private EditText commentEditText;
    private ArrayList<Comment> commentArrayList;
    private LinearLayoutManager linearLayoutManager;
    private Post post;
    private CommentCustomArrayAdapter commentCustomArrayAdapter;
    private ProgressBar commentProgressBar;
    private Toolbar toolbar;
    private Boolean isLiked=false;
    private HashMap<String,String> likeHashMap;
    private User feedUser;
    private ProgressBar feedUserImageViewProgressBar,currentUserImageViewProgressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,R.style.AppTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.details_with_comment_dialog_fragment_layout,container,false);
        context=getContext();

        toolbar=view.findViewById(R.id.commentToolbar);
        toolbar.setTitle("Comments");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        if(CommonData.isAdmin) {
            toolbar.inflateMenu(R.menu.delete_post_menu);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if (menuItem.getItemId() == R.id.deletePostMenu) {
                        showDeleteAlertDialog();
                    }
                    return true;
                }
            });
        }

        //Finding all the widgets by there id's
        feedUserImageView=view.findViewById(R.id.feedUserImageView);
        feedLikeImageView=view.findViewById(R.id.feedLikeImageView);
        feedCommentImageView=view.findViewById(R.id.feedCommentImageView);
        feedTitleTextView=view.findViewById(R.id.feedTitleTextView);
        feedDescriptionReadMoreTextView=view.findViewById(R.id.feedDescriptionReadMoreTextView);
        optionLinearLayout=view.findViewById(R.id.optionLinearLayout);
        optionOneTextView=view.findViewById(R.id.optionOneTextView);
        optionTwoTextView=view.findViewById(R.id.optionTwoTextView);
        feedNoOfLikes=view.findViewById(R.id.feedNoOfLikes);
        commentRecyclerView=view.findViewById(R.id.commentRecyclerView);
        sendCommentButton=view.findViewById(R.id.sendCommentButton);
        commentEditText=view.findViewById(R.id.commentEditText);
        currentUserImageView=view.findViewById(R.id.currentUserImageView);
        commentProgressBar=view.findViewById(R.id.commentProgressBar);
        feedUserNameTextView=view.findViewById(R.id.feedUserNameTextView);
        feedTimeDateTextView=view.findViewById(R.id.feedTimeDateTextView);
        feedUserImageViewProgressBar=view.findViewById(R.id.feedUserImageViewProgressBar);
        currentUserImageViewProgressBar=view.findViewById(R.id.currentUserImageViewProgressBar);

        linearLayoutManager=new LinearLayoutManager(context);
        commentRecyclerView.setHasFixedSize(false);
        linearLayoutManager.setSmoothScrollbarEnabled(false);
        commentRecyclerView.setLayoutManager(linearLayoutManager);

        
        if(getArguments()!=null){
            //I have passed key of post which user clicks from feed fragment
            postKeyId=getArguments().getString("postKeyId");
//            likeHashMap=GlobalStateApplication.usersHashMap.get(CommonData.firebaseCurrentUserUid).getLikesFeedHashMap();
            if(User.getCurrentUser()!=null) {
                likeHashMap = User.getCurrentUser().getLikesFeedHashMap();
                if (likeHashMap != null) {
                    if (likeHashMap.containsKey(postKeyId)) {
                        isLiked = true;
                    }
                } else {
                    likeHashMap = new HashMap<>();
                }
                settingTheData();
            }
        }

        sendCommentButton.setOnClickListener(this);
        feedLikeImageView.setOnClickListener(this);
        feedCommentImageView.setOnClickListener(this);

        return view;

    }

    private void showDeleteAlertDialog() {
        new AlertDialog.Builder(context)
                .setTitle("Delete Post/Poll")
                .setIcon(R.drawable.way_for_life_logo)
                .setMessage("Are you sure you want to delete it?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ProgressUtils.showKProgressDialog(context,"Deleting..");
                        GlobalStateApplication.feedsDatabaseReference.child(postKeyId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    ProgressUtils.cancelKprogressDialog();
                                    dismiss();
                                }else{
                                    ProgressUtils.cancelKprogressDialog();
                                    Toast.makeText(context, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("No",null)
                .show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.sendCommentButton:
                if(commentEditText.getText().toString().trim().length()!=0){
                    pushTheCommentIntoFirebaseDatabase();
                }else{
                    commentEditText.setError("Please first write something");
                    commentEditText.requestFocus();
                }
                break;
            case R.id.feedLikeImageView:
                if(!isLiked){
                    setColoredLikeImageAndMakeChangeInFirebaseDatabase();
                }
                break;
            case R.id.feedCommentImageView:
                commentEditText.requestFocus();
                break;

        }
    }

    //This below will set the noOfLikes and blue like image.This below method will be called when user presses like button.
    private void setColoredLikeImageAndMakeChangeInFirebaseDatabase() {
        feedLikeImageView.setImageResource(R.drawable.like_blue_image);
        final DatabaseReference postReference = GlobalStateApplication.feedsDatabaseReference.child(postKeyId);
        postReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post1 = dataSnapshot.getValue(Post.class);
                if (post1 != null) {
                    int totalLikes = post1.getNoOfLikes() + 1;
                    post1.setNoOfLikes(totalLikes);
                    feedNoOfLikes.setText(String.valueOf(totalLikes));
                    postReference.setValue(post1);
                    isLiked=true;
                    saveThisLikedPostInUserNode(dataSnapshot.getKey());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //I am saving the liked post in user node.Whatever post user will like will be saved into user node.
    private void saveThisLikedPostInUserNode(final String key) {
        final DatabaseReference currentUserReference=GlobalStateApplication.usersDatabaseReference.child(CommonData.firebaseCurrentUserUid);
        currentUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                HashMap<String, String> hashMap;
                if (user != null) {
                    hashMap = user.getLikesFeedHashMap();
                    if (hashMap == null) {
                        hashMap = new HashMap<>();
                    }
                    hashMap.put(key, key);//putting only keys of that post in users likesFeedHashMap
                    user.setLikesFeedHashMap(hashMap);
                    currentUserReference.setValue(user);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Whenever user post some comment then this comment will be stored into firebase database.All the comments are stored in commentArrayList.
    private void pushTheCommentIntoFirebaseDatabase() {
        Comment comment=new Comment();
        comment.setTimeDate(CommonData.getCurrentTime()+"   "+CommonData.getTodayDate());
        comment.setUserId(CommonData.firebaseCurrentUserUid);
        comment.setCommentedMessage(commentEditText.getText().toString());
        commentArrayList.add(comment);
        commentCustomArrayAdapter.notifyDataSetChanged();
        post.setCommentArrayList(commentArrayList);
        commentEditText.setText("");
        GlobalStateApplication.feedsDatabaseReference.child(postKeyId).setValue(post);
    }

    //This below will fetch the post data using post key from firebase.
    private void settingTheData() {
        GlobalStateApplication.feedsDatabaseReference.child(postKeyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                post=dataSnapshot.getValue(Post.class);
                if(post!=null) {
//                    Toast.makeText(context, post.getTitle(), Toast.LENGTH_SHORT).show();
                    GlobalStateApplication.usersDatabaseReference.child(post.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            feedUser=dataSnapshot.getValue(User.class);
                            if(feedUser!=null){
                                setAllTheDataOfThisPost();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //This below method will set all the data into their appropriate places.
    private void setAllTheDataOfThisPost() {
        commentProgressBar.setVisibility(View.GONE);
        if (feedUser.getImageUrl() != null) {
            Picasso.with(context).load(feedUser.getImageUrl()).into(feedUserImageView, new Callback() {
                @Override
                public void onSuccess() {
                    if(feedUserImageViewProgressBar!=null) {
                        feedUserImageViewProgressBar.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onError() {

                }
            });
        } else {
            feedUserImageView.setImageResource(R.drawable.person_image);
        }

        if (User.getCurrentUser().getImageUrl() != null) {
            Picasso.with(context).load(User.getCurrentUser().getImageUrl()).into(currentUserImageView, new Callback() {
                @Override
                public void onSuccess() {
                    if(currentUserImageViewProgressBar!=null){
                        currentUserImageViewProgressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onError() {

                }
            });
        } else {
            currentUserImageView.setImageResource(R.drawable.person_image);
        }

        feedUserNameTextView.setText(feedUser.getFirstName());
        feedTimeDateTextView.setText(post.getTimeDate());
        feedTitleTextView.setText(post.getTitle());
        feedNoOfLikes.setText(String.valueOf(post.getNoOfLikes()));

        if (isLiked) {
            feedLikeImageView.setImageResource(R.drawable.like_blue_image);
        }
        if (post.isPost()) {
            optionLinearLayout.setVisibility(View.GONE);
            feedDescriptionReadMoreTextView.setText(post.getContent());
        } else {
            String content = post.getContent();
            int firstIndexOfFirstPattern = content.indexOf(CommonData.firstPatternWord);
            int firstIndexOfSecondPattern = content.indexOf(CommonData.secondPatternWord);
            optionOneTextView.setText(content.substring(firstIndexOfFirstPattern + 4, firstIndexOfSecondPattern));
            optionTwoTextView.setText(content.substring(firstIndexOfSecondPattern + 4));
            feedDescriptionReadMoreTextView.setText(content.substring(0, firstIndexOfFirstPattern));
        }
        commentArrayList = post.getCommentArrayList();
        if (commentArrayList == null) {
            commentArrayList = new ArrayList<>();
        }
        setCommentAdapter();
    }

    private void setCommentAdapter() {
        commentCustomArrayAdapter=new CommentCustomArrayAdapter(context,commentArrayList,postKeyId);
        commentRecyclerView.setAdapter(commentCustomArrayAdapter);
    }

    public static DetailsWithCommentDialogFragment newInstance() {

        Bundle args = new Bundle();
        DetailsWithCommentDialogFragment fragment = new DetailsWithCommentDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        //This is necessary for showing the dialog in full screen.
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);
        }
    }
}
