package com.wayforlife.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.wayforlife.Activities.HomeActivity;
import com.wayforlife.Adapters.FeedCustomRecyclerViewArrayAdapter;
import com.wayforlife.Common.CommonData;
import com.wayforlife.GlobalStateApplication;
import com.wayforlife.Models.Post;
import com.wayforlife.Models.User;
import com.wayforlife.R;
import com.wayforlife.ViewHolders.FeedViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/** This fragment is used to show the feeds to user */
public class FeedFragment extends Fragment implements View.OnClickListener {

    private FloatingActionButton pollFloatingActionButton,postFloatingActionButton;
    private RecyclerView feedRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private Context context;
//    private ArrayList<Post> postPollArrayList;// stores both polls and posts
//    private ArrayList<String> postPollKeyArrayList; //store both polls and posts keys
//    private ArrayList<Post> postArrayList; //store posts
//    private ArrayList<Post> pollArrayList; //store polls
//    private ArrayList<String> postKeyArrayList;//stores key of posts
//    private ArrayList<String> pollKeyArrayList;// stores key of polls
//    private ArrayList<Post> tempPostArrayList; //temp arraylist (which is a concatenation of polls arraylist and post arraylist) stores post
//    private ArrayList<String> tempKeyPostArrayList; //temp arraylist ( which is a concatenation of pollskeyarraylist and postKeyArraylist stores keys of posts
//    private ArrayList<Post> tempPollArrayList; //same as above mentioned in tempPostArraylist but it stores poll
//    private ArrayList<String> tempKeyPollArrayList; //same as above mentioned in tempKeyPostArraylist but it  stores keys of polls

    private ProgressBar feedProgressBar;
    private FeedCustomRecyclerViewArrayAdapter feedCustomRecyclerViewArrayAdapter;
    private HomeActivity homeActivity;
    private boolean isPostFilterApplied=false;
    private boolean isPollFilterApplied=false;
    private FirebaseRecyclerAdapter<Post,FeedViewHolder> feedFirebaseRecyclerAdapter;
    private HashMap<String,String> likesFeedHashMap;
    private AppCompatActivity appCompatActivity;
    private MenuItem filterMenuItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.feed_fragment_layout,container,false);
        context=getContext();
        homeActivity= (HomeActivity) getActivity();

        pollFloatingActionButton=view.findViewById(R.id.pollFloatingActionButton);
        postFloatingActionButton=view.findViewById(R.id.postFloatingActionButton);
        feedProgressBar=view.findViewById(R.id.feedProgressBar);


        feedRecyclerView=view.findViewById(R.id.feedRecyclerView);
        linearLayoutManager=new LinearLayoutManager(context);
        feedRecyclerView.setHasFixedSize(true);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        feedRecyclerView.setLayoutManager(linearLayoutManager);

        pollFloatingActionButton.setOnClickListener(this);
        postFloatingActionButton.setOnClickListener(this);

        //Initializing all the arraylist
//        postPollArrayList=new ArrayList<>();
//        postPollKeyArrayList=new ArrayList<>();
//        postArrayList=new ArrayList<>();
//        pollArrayList=new ArrayList<>();
//        postKeyArrayList=new ArrayList<>();
//        pollKeyArrayList=new ArrayList<>();


        //Setting the adapter to the recyclerview
//        feedCustomRecyclerViewArrayAdapter = new FeedCustomRecyclerViewArrayAdapter(context,postPollArrayList, postPollKeyArrayList);
//        feedRecyclerView.setAdapter(feedCustomRecyclerViewArrayAdapter);

        checkIfFeedExistIfExistThenFetchAllFeeds();

        return view;
    }

    /**This below method will fetch all the feeds and store them into the arraylist*/
    private void checkIfFeedExistIfExistThenFetchAllFeeds() {
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild("Feeds")) {
                    feedProgressBar.setVisibility(View.GONE);
                    Toast toast = Toast.makeText(context, "There is no any feed", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                //Applying listener on feed node at that time also when there is no any feed. Because if someone add new feed then that new feed
                //will be visible to user only if listener is applied on that node.
                setFirebaseRecyclerUiAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
//
//    private void getAllTheFeeds() {
//        GlobalStateApplication.feedsDatabaseReference.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                /** Below i am storing the post into different different arraylist because when i will apply filter then i will use these
//                 * arraylist */
//                Post post=dataSnapshot.getValue(Post.class);
//                if(post.isPost()){
//                    postArrayList.add(post); //storing the post
//                    postKeyArrayList.add(dataSnapshot.getKey()); //storing the key of post
//                }else{
//                    pollArrayList.add(post);
//                    pollKeyArrayList.add(dataSnapshot.getKey());
//                }
//                postPollArrayList.add(post);
//                postPollKeyArrayList.add(dataSnapshot.getKey());
//                if(feedCustomRecyclerViewArrayAdapter!=null){
//                    feedProgressBar.setVisibility(View.GONE); //one item is being fetched so i will remove progress bar
//                    feedCustomRecyclerViewArrayAdapter.notifyDataSetChanged(); //notifying the adapter
//                }
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                /**if someone like the post then it will change the value in node. If value will changed then this method
//                 will be called. I want to show those changes to user at the same time synchronously. For that first i am finding the position
//                 of that post in arraylist and then setting the new updated post in arraylist and then notifying the adapter. */
//                int i;
//                //if user applied post filter then i will make changes in tempPollArrayList.
//                if(isPostFilterApplied){
//                    i=tempKeyPollArrayList.indexOf(dataSnapshot.getKey());
//                    if(i>=0) {
//                        tempPollArrayList.set(i, dataSnapshot.getValue(Post.class));
//                    }
//                }else if(isPollFilterApplied){
//                    i=tempKeyPostArrayList.indexOf(dataSnapshot.getKey());
//                    if(i>=0) {
//                        tempPostArrayList.set(i, dataSnapshot.getValue(Post.class));
//                    }
//                }else{
//                    i=postPollKeyArrayList.indexOf(dataSnapshot.getKey());
//                    if(i>=0) {
//                        postPollArrayList.set(i, dataSnapshot.getValue(Post.class));
//                    }
//                }
//                feedCustomRecyclerViewArrayAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//                int i;
//                //if admin remove the post.
//                if(isPostFilterApplied){
//                    i=tempKeyPollArrayList.indexOf(dataSnapshot.getKey());
//                    if(i>=0) {
//                        tempPollArrayList.remove(i);
//                    }
//                }else if(isPollFilterApplied){
//                    i=tempKeyPostArrayList.indexOf(dataSnapshot.getKey());
//                    if(i>=0) {
//                        tempPostArrayList.remove(i);
//                    }
//                }else{
//                    i=postPollKeyArrayList.indexOf(dataSnapshot.getKey());
//                    if(i>=0) {
//                        postPollArrayList.remove(i);
//                    }
//                }
//                feedCustomRecyclerViewArrayAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//        //This below will be called once above listener completes.
////        GlobalStateApplication.feedsDatabaseReference.addValueEventListener(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                if(postPollArrayList.size()==0){
////                    Toast.makeText(context,"There are no feeds",Toast.LENGTH_SHORT).show();
////                }
//////                    GlobalStateApplication.feedsDatabaseReference.removeEventListener(feedValueEventListener);
//////                    flag=false;
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError databaseError) {
////
////            }
////        });
//    }

    private void setPostOrPollFilterRecyclerUiAdapter(boolean isPost){
        feedFirebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Post, FeedViewHolder>(Post.class,R.layout.feed_row_layout,FeedViewHolder.class,
                GlobalStateApplication.feedsDatabaseReference.orderByChild("isPost").equalTo(isPost)) {
            @Override
            protected void populateViewHolder(FeedViewHolder feedViewHolder, Post post, int position) {
                feedProgressBar.setVisibility(View.GONE);
                bindTheDataWithViewHolder(feedViewHolder,post,position);
            }
        };
        feedRecyclerView.setAdapter(feedFirebaseRecyclerAdapter);
    }

    private void setFirebaseRecyclerUiAdapter(){
        feedFirebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Post, FeedViewHolder>(Post.class,R.layout.feed_row_layout,FeedViewHolder.class,
                GlobalStateApplication.feedsDatabaseReference) {
            @Override
            protected void populateViewHolder(FeedViewHolder feedViewHolder, Post post, int position) {
                feedProgressBar.setVisibility(View.GONE);
                bindTheDataWithViewHolder(feedViewHolder,post,position);
            }
        };
        feedRecyclerView.setAdapter(feedFirebaseRecyclerAdapter);
    }

    private void bindTheDataWithViewHolder(final FeedViewHolder feedViewHolder, final Post post, int position) {
        GlobalStateApplication.usersDatabaseReference.child(post.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User feedUser=dataSnapshot.getValue(User.class);
                if(feedUser!=null){
                    setAllDataOnViewHolder(feedViewHolder,post,feedViewHolder.getAdapterPosition(),feedUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        feedViewHolder.feedLikeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(User.getCurrentUser()!=null) {
                    likesFeedHashMap = User.getCurrentUser().getLikesFeedHashMap();
                    /**likesFeedHashMap contains all the keys of post which users had liked. if user has already liked the post and he again
                     presses like button then i will not do anything. But if he has not liked then this blue like image will be set
                     and will update the post.
                     Initially there will no liked post by user.So at that time likesFeedHashMap will be null.It is the first time so i am directly
                     setting the blue like image without checking(that this post key is present in users likesFeedHashMap.*/
                    if (likesFeedHashMap != null && feedViewHolder.getAdapterPosition()>=0) {
                        if (!likesFeedHashMap.containsKey(feedFirebaseRecyclerAdapter.getRef(feedViewHolder.getAdapterPosition()).getKey())){
                            setNewData(feedViewHolder);
                        }
                    } else {
                        setNewData(feedViewHolder);
                    }
                }
            }
        });

        feedViewHolder.feedCommentImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appCompatActivity= (AppCompatActivity)context;
                showDialogFragment(DetailsWithCommentDialogFragment.newInstance(),context.getString(R.string.detailsWithCommentDialogFragment),
                        feedFirebaseRecyclerAdapter.getRef(feedViewHolder.getAdapterPosition()).getKey());
            }
        });
    }

    //This below method will set the new data with like_blue_image and also increments the like value.After incrementing update the post.
    private void setNewData(final FeedViewHolder feedViewHolder) {
        feedViewHolder.feedLikeImageView.setImageResource(R.drawable.like_blue_image);
        final DatabaseReference postReference = GlobalStateApplication.feedsDatabaseReference.child(feedFirebaseRecyclerAdapter.getRef(feedViewHolder.getAdapterPosition()).getKey());
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
                    feedFirebaseRecyclerAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setAllDataOnViewHolder(FeedViewHolder feedViewHolder, Post post, int position, User feedUser) {

        /**Initially when data will be shown to user, at that time i have to set the like_image to blue_like_image which he has liked
         * already. For that i am checking if the post key is present in usersLikedPostHashMap or not. if it is not present there it means
         * user haven't liked it yet now. */
        if (feedUser.getImageUrl() != null) {
            Picasso.with(context).load(Objects.requireNonNull(feedUser.getImageUrl())).into(feedViewHolder.feedUserImageView);
        } else {
            feedViewHolder.feedUserImageView.setImageResource(R.drawable.person_image);
        }
        feedViewHolder.feedUserNameTextView.setText(feedUser.getFirstName());
        feedViewHolder.feedTimeDateTextView.setText(post.getTimeDate());
        feedViewHolder.feedTitleTextView.setText(post.getTitle());

        if (User.getCurrentUser() != null) {
            likesFeedHashMap = User.getCurrentUser().getLikesFeedHashMap();
        }
        if (likesFeedHashMap != null && feedViewHolder.getAdapterPosition()>=0) {
            Log.i("adapter position",feedViewHolder.getAdapterPosition()+ " "+feedFirebaseRecyclerAdapter.getRef(feedViewHolder.getAdapterPosition()).getKey());
            String tempKey = likesFeedHashMap.get(feedFirebaseRecyclerAdapter.getRef(feedViewHolder.getAdapterPosition()).getKey());
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
            feedViewHolder.optionLinearLayout.setVisibility(View.VISIBLE);
            int firstIndexOfFirstPattern = content.indexOf(CommonData.firstPatternWord);
            int firstIndexOfSecondPattern = content.indexOf(CommonData.secondPatternWord);
            feedViewHolder.optionOneTextView.setText(content.substring(firstIndexOfFirstPattern + 4, firstIndexOfSecondPattern));
            feedViewHolder.optionTwoTextView.setText(content.substring(firstIndexOfSecondPattern + 4));
            feedViewHolder.feedDescriptionReadMoreTextView.setText(content.substring(0, firstIndexOfFirstPattern));
        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.pollFloatingActionButton:
                showDialogFragment(AddPollDialogFragment.newInstance(),getString(R.string.addPollDialogFragment));
                break;

            case R.id.postFloatingActionButton:
                showDialogFragment(AddPostDialogFragment.newInstance(),getString(R.string.addPostDialogFragmentTag));
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.filter_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.pollFilterMenu:
                if(filterMenuItem!=null) {
                    filterMenuItem.setChecked(false);
                }
                item.setChecked(true);
//                isPostFilterApplied=false;
//                isPollFilterApplied=true;
//                tempPostArrayList= new ArrayList<>(postArrayList); //Copying postArrayList into tempPostArrayList
//                tempKeyPostArrayList = new ArrayList<>(postKeyArrayList); //Copying postKeyArrayList into tempKeyPostArrayList
//
//                /**tempPostArrayList is the concatenation of postArrayList and pollArrayList. Why i am doing this? Because i have applied reverse
//                layout on feedRecyclerView so to show post first i need to put all the post at the last of tempPostArrayList*/
//                tempPostArrayList.addAll(pollArrayList);
//                tempKeyPostArrayList.addAll(pollKeyArrayList);
//                setTheAdapterOnRecyclerView(tempPostArrayList,tempKeyPostArrayList);
                setPostOrPollFilterRecyclerUiAdapter(false);
                filterMenuItem=item;
                break;
            case R.id.postFilterMenu:
                if(filterMenuItem!=null) {
                    filterMenuItem.setChecked(false);
                }
                item.setChecked(true);
//                isPollFilterApplied=false;
//                isPostFilterApplied=true;
//                tempPollArrayList = new ArrayList<>(pollArrayList);
//                tempKeyPollArrayList = new ArrayList<>(pollKeyArrayList);
//
//                //Same as above mentioned
//                tempPollArrayList.addAll(postArrayList);
//                tempKeyPollArrayList.addAll(postKeyArrayList);
//                setTheAdapterOnRecyclerView(tempPollArrayList,tempKeyPollArrayList);
                setPostOrPollFilterRecyclerUiAdapter(true);
                filterMenuItem=item;
                break;
            case R.id.defaultFilterMenu:
                if(filterMenuItem!=null) {
                    filterMenuItem.setChecked(false);
                }
                item.setChecked(true);
                //This will set the default filter.
//                setTheAdapterOnRecyclerView(postPollArrayList,postPollKeyArrayList);
                setFirebaseRecyclerUiAdapter();
                filterMenuItem=item;
                break;

        }
        return true;
    }

    private void setTheAdapterOnRecyclerView(ArrayList<Post> arrayList, ArrayList<String> keyArrayList) {
//        feedRecyclerView.removeAllViewsInLayout();
        feedCustomRecyclerViewArrayAdapter = new FeedCustomRecyclerViewArrayAdapter(context,arrayList, keyArrayList);
        feedRecyclerView.setAdapter(feedCustomRecyclerViewArrayAdapter);
    }


    private void showDialogFragment(DialogFragment dialogFragment,String tag) {
        FragmentTransaction fragmentTransaction;
        if (getFragmentManager() != null) {
            fragmentTransaction = getFragmentManager().beginTransaction();
            dialogFragment.show(fragmentTransaction,tag);
//            fragmentTransaction.add(android.R.id.content,dialogFragment,tag).commit();
        }
    }

    public static FeedFragment newInstance() {
        Bundle args = new Bundle();
        FeedFragment fragment = new FeedFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onResume() {
        super.onResume();
        homeActivity.setActionBarTitle("Events");
    }
}
