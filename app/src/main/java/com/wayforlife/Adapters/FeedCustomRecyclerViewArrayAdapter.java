package com.wayforlife.Adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.wayforlife.Common.CommonData;
import com.wayforlife.Fragments.DetailsWithCommentDialogFragment;
import com.wayforlife.GlobalStateApplication;
import com.wayforlife.Models.Post;
import com.wayforlife.Models.User;
import com.wayforlife.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class FeedCustomRecyclerViewArrayAdapter extends RecyclerView.Adapter<FeedCustomRecyclerViewArrayAdapter.FeedViewHolder>{

    private ArrayList<Post> postPollArrayList; //contains all the posts and polls
    private ArrayList<String> postPollKeyArrayList; //contains all the posts and polls keys
    private Context context;
    private Activity activity;
    private AppCompatActivity appCompatActivity;
    private HashMap<String,String> likesFeedHashMap;
    public FeedCustomRecyclerViewArrayAdapter() {
    }

    public FeedCustomRecyclerViewArrayAdapter(Context context, ArrayList<Post> postPollArrayList, ArrayList<String> postPollKeyArrayList) {
        this.context=context;
        this.postPollArrayList=postPollArrayList;
        this.postPollKeyArrayList=postPollKeyArrayList;
//        setHasStableIds(true);
        Log.i("Arraylist size",String.valueOf(postPollArrayList.size()));
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.feed_row_layout,viewGroup,false);
        FeedViewHolder feedViewHolder=new FeedViewHolder(view);
        feedViewHolder.setIsRecyclable(false);
        return feedViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final FeedViewHolder feedViewHolder, final int i) {

        //Binding all the data of post on view holder
        Post post=postPollArrayList.get(i);
        //if post.getPost() is true, it means it is a post then show post raw layout otherwise show poll raw layout
        setAllDataOnViewHolder(feedViewHolder,post,i);
        feedViewHolder.feedLikeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likesFeedHashMap=GlobalStateApplication.usersHashMap.get(CommonData.firebaseCurrentUserUid).getLikesFeedHashMap();
                /**likesFeedHashMap contains all the keys of post which users had liked. if user has already liked the post and he again
                presses like button then i will not do anything. But if he has not liked then this blue like image will be set
                and will update the post.
                Initially there will no liked post by user.So at that time likesFeedHashMap will be null.It is the first time so i am directly
                setting the blue like image without checking(that this post key is present in users likesFeedHashMap.*/
                if(likesFeedHashMap!=null) {
                    if (!likesFeedHashMap.containsKey(postPollKeyArrayList.get(feedViewHolder.getAdapterPosition()))) {
                        setNewData(feedViewHolder);
                    }
                }else{
                    setNewData(feedViewHolder);
                }
            }
        });

        feedViewHolder.feedCommentImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appCompatActivity= (AppCompatActivity)context;
                showDialogFragment(DetailsWithCommentDialogFragment.newInstance(),context.getString(R.string.detailsWithCommentDialogFragment),
                        postPollKeyArrayList.get(feedViewHolder.getAdapterPosition()));
            }
        });
    }

    //This below method will set the new data with like_blue_image and also increments the like value.After incrementing update the post.
    private void setNewData(final FeedViewHolder feedViewHolder) {
        feedViewHolder.feedLikeImageView.setImageResource(R.drawable.like_blue_image);
        final DatabaseReference postReference = GlobalStateApplication.feedsDatabaseReference.child(postPollKeyArrayList.get(feedViewHolder.getAdapterPosition()));
        postReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post1 = dataSnapshot.getValue(Post.class);
                if (post1 != null) {
                    int totalLikes = post1.getNoOfLikes() + 1;
                    post1.setNoOfLikes(totalLikes);
                    feedViewHolder.feedNoOfLikes.setText(String.valueOf(totalLikes));
                    saveThisLikedPostInUserNode(postReference,post1,dataSnapshot.getKey());
//                    postReference.setValue(post1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //This below method will saved the liked post in user node.
    private void saveThisLikedPostInUserNode(final DatabaseReference postReference, final Post post, final String key) {
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
                    hashMap.put(key, key);
                    user.setLikesFeedHashMap(hashMap);
                    currentUserReference.setValue(user);
                    postReference.setValue(post);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setAllDataOnViewHolder(final FeedViewHolder feedViewHolder, Post post,int position) {

        /**Initially when data will be shown to user, at that time i have to set the like_image to blue_like_image which he has liked
         * already. For that i am checking if the post key is present in usersLikedPostHashMap or not. if it is not present there it means
         * user haven't liked it yet now. */
        User feedUser=GlobalStateApplication.usersHashMap.get(post.getUserId());
        if(feedUser!=null) {
            if (feedUser.getImageUrl() != null) {
                Picasso.with(context).load(Objects.requireNonNull(feedUser.getImageUrl())).into(feedViewHolder.feedUserImageView);
            } else {
                feedViewHolder.feedUserImageView.setImageResource(R.drawable.person_image);
            }
            feedViewHolder.feedUserNameTextView.setText(feedUser.getFirstName());
            feedViewHolder.feedTimeDateTextView.setText(post.getTimeDate());
            feedViewHolder.feedTitleTextView.setText(post.getTitle());
            likesFeedHashMap = GlobalStateApplication.usersHashMap.get(CommonData.firebaseCurrentUserUid).getLikesFeedHashMap();
            if (likesFeedHashMap != null) {
                String tempKey = likesFeedHashMap.get(postPollKeyArrayList.get(position));
                if (tempKey != null) {
                    feedViewHolder.feedLikeImageView.setImageResource(R.drawable.like_blue_image);
                } else {
                    feedViewHolder.feedLikeImageView.setImageResource(R.drawable.like_image);
                }
            }
            feedViewHolder.feedNoOfLikes.setText(String.valueOf(post.getNoOfLikes()));

            if (post.isPost()) {
                feedViewHolder.optionLinearLayout.setVisibility(View.GONE);
                feedViewHolder.feedDescriptionReadMoreTextView.setText(post.getContent());
            } else {
                //if the post is poll then i need to find the substrings for option1 and option2 from the content.
                String content = post.getContent();
                int firstIndexOfFirstPattern = content.indexOf(CommonData.firstPatternWord);
                int firstIndexOfSecondPattern = content.indexOf(CommonData.secondPatternWord);
                feedViewHolder.optionOneTextView.setText(content.substring(firstIndexOfFirstPattern + 4, firstIndexOfSecondPattern));
                feedViewHolder.optionTwoTextView.setText(content.substring(firstIndexOfSecondPattern + 4));
                feedViewHolder.feedDescriptionReadMoreTextView.setText(content.substring(0, firstIndexOfFirstPattern));
            }
        }
    }

    @Override
    public int getItemCount() {
        return postPollArrayList.size();
    }

//    @Override
//    public long getItemId(int position) {
//        return super.getItemId(position);
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return super.getItemViewType(position);
//    }

    private void showDialogFragment(DialogFragment dialogFragment, String tag, String postKeyId) {
        FragmentTransaction fragmentTransaction;
        if (appCompatActivity.getSupportFragmentManager() != null) {
            fragmentTransaction = appCompatActivity.getSupportFragmentManager().beginTransaction();
            Bundle bundle=new Bundle();
            bundle.putString("postKeyId",postKeyId);
            dialogFragment.setArguments(bundle);
            dialogFragment.show(fragmentTransaction,tag);
//            fragmentTransaction.add(android.R.id.content,dialogFragment,tag).commit();
        }
    }


    class FeedViewHolder extends RecyclerView.ViewHolder{

        private ImageView feedUserImageView,feedLikeImageView,feedCommentImageView;
        private TextView feedTitleTextView;
        private TextView feedDescriptionReadMoreTextView;
        private LinearLayout optionLinearLayout;
        private TextView optionOneTextView,optionTwoTextView,feedNoOfLikes,feedUserNameTextView,feedTimeDateTextView;

        FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            feedUserImageView=itemView.findViewById(R.id.feedUserImageView);
            feedLikeImageView=itemView.findViewById(R.id.feedLikeImageView);
            feedCommentImageView=itemView.findViewById(R.id.feedCommentImageView);
            feedTitleTextView=itemView.findViewById(R.id.feedTitleTextView);
            feedDescriptionReadMoreTextView=itemView.findViewById(R.id.feedDescriptionReadMoreTextView);
            optionLinearLayout=itemView.findViewById(R.id.optionLinearLayout);
            optionOneTextView=itemView.findViewById(R.id.optionOneTextView);
            optionTwoTextView=itemView.findViewById(R.id.optionTwoTextView);
            feedNoOfLikes=itemView.findViewById(R.id.feedNoOfLikes);
            feedUserNameTextView=itemView.findViewById(R.id.feedUserNameTextView);
            feedTimeDateTextView=itemView.findViewById(R.id.feedTimeDateTextView);
        }
    }
}
