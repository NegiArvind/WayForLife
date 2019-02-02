package com.wayforlife.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wayforlife.Adapters.FeedCustomRecyclerViewArrayAdapter;
import com.wayforlife.GlobalStateApplication;
import com.wayforlife.Models.Post;
import com.wayforlife.R;

import java.util.ArrayList;

/** This fragment is used to show the feeds to user */
public class FeedFragment extends Fragment implements View.OnClickListener {

    private FloatingActionButton pollFloatingActionButton,postFloatingActionButton;
    private RecyclerView feedRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private Context context;
    private ArrayList<Post> postPollArrayList;// stores both polls and posts
    private ArrayList<String> postPollKeyArrayList; //store both polls and posts keys
    private ArrayList<Post> postArrayList; //store posts
    private ArrayList<Post> pollArrayList; //store polls
    private ArrayList<String> postKeyArrayList;//stores key of posts
    private ArrayList<String> pollKeyArrayList;// stores key of polls
    private ArrayList<Post> tempPostArrayList; //temp arraylist (which is a concatenation of polls arraylist and post arraylist) stores post
    private ArrayList<String> tempKeyPostArrayList; //temp arraylist ( which is a concatenation of pollskeyarraylist and postKeyArraylist stores keys of posts
    private ArrayList<Post> tempPollArrayList; //same as above mentioned in tempPostArraylist but it stores poll
    private ArrayList<String> tempKeyPollArrayList; //same as above mentioned in tempKeyPostArraylist but it  stores keys of polls

    private ProgressBar feedProgressBar;
    private FeedCustomRecyclerViewArrayAdapter feedCustomRecyclerViewArrayAdapter;
    private Activity activity;
    private boolean isPostFilterApplied=false;
    private boolean isPollFilterApplied=false;

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
        activity=getActivity();

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
        postPollArrayList=new ArrayList<>();
        postPollKeyArrayList=new ArrayList<>();
        postArrayList=new ArrayList<>();
        pollArrayList=new ArrayList<>();
        postKeyArrayList=new ArrayList<>();
        pollKeyArrayList=new ArrayList<>();


        //Setting the adapter to the recyclerview
        feedCustomRecyclerViewArrayAdapter = new FeedCustomRecyclerViewArrayAdapter(context,postPollArrayList, postPollKeyArrayList);
        feedRecyclerView.setAdapter(feedCustomRecyclerViewArrayAdapter);

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
                getAllTheFeeds();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAllTheFeeds() {
        GlobalStateApplication.feedsDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                /** Below i am storing the post into different different arraylist because when i will apply filter then i will use these
                 * arraylist */
                Post post=dataSnapshot.getValue(Post.class);
                if(post.isPost()){
                    postArrayList.add(post); //storing the post
                    postKeyArrayList.add(dataSnapshot.getKey()); //storing the key of post
                }else{
                    pollArrayList.add(post);
                    pollKeyArrayList.add(dataSnapshot.getKey());
                }
                postPollArrayList.add(post);
                postPollKeyArrayList.add(dataSnapshot.getKey());
                if(feedCustomRecyclerViewArrayAdapter!=null){
                    feedProgressBar.setVisibility(View.GONE); //one item is being fetched so i will remove progress bar
                    feedCustomRecyclerViewArrayAdapter.notifyDataSetChanged(); //notifying the adapter
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                /**if someone like the post then it will change the value in node. If value will changed then this method
                 will be called. I want to show those changes to user at the same time synchronously. For that first i am finding the position
                 of that post in arraylist and then setting the new updated post in arraylist and then notifying the adapter. */
                int i;
                //if user applied post filter then i will make changes in tempPollArrayList.
                if(isPostFilterApplied){
                    i=tempKeyPollArrayList.indexOf(dataSnapshot.getKey());
                    if(i>=0) {
                        tempPollArrayList.set(i, dataSnapshot.getValue(Post.class));
                    }
                }else if(isPollFilterApplied){
                    i=tempKeyPostArrayList.indexOf(dataSnapshot.getKey());
                    if(i>=0) {
                        tempPostArrayList.set(i, dataSnapshot.getValue(Post.class));
                    }
                }else{
                    i=postPollKeyArrayList.indexOf(dataSnapshot.getKey());
                    if(i>=0) {
                        postPollArrayList.set(i, dataSnapshot.getValue(Post.class));
                    }
                }
                feedCustomRecyclerViewArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int i;
                //if admin remove the post.
                if(isPostFilterApplied){
                    i=tempKeyPollArrayList.indexOf(dataSnapshot.getKey());
                    if(i>=0) {
                        tempPollArrayList.remove(i);
                    }
                }else if(isPollFilterApplied){
                    i=tempKeyPostArrayList.indexOf(dataSnapshot.getKey());
                    if(i>=0) {
                        tempPostArrayList.remove(i);
                    }
                }else{
                    i=postPollKeyArrayList.indexOf(dataSnapshot.getKey());
                    if(i>=0) {
                        postPollArrayList.remove(i);
                    }
                }
                feedCustomRecyclerViewArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //This below will be called once above listener completes.
//        GlobalStateApplication.feedsDatabaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(postPollArrayList.size()==0){
//                    Toast.makeText(context,"There are no feeds",Toast.LENGTH_SHORT).show();
//                }
////                    GlobalStateApplication.feedsDatabaseReference.removeEventListener(feedValueEventListener);
////                    flag=false;
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
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
        item.setChecked(true);
        switch (item.getItemId()){
            case R.id.pollFilterMenu:
                isPostFilterApplied=false;
                isPollFilterApplied=true;
                tempPostArrayList= new ArrayList<>(postArrayList); //Copying postArrayList into tempPostArrayList
                tempKeyPostArrayList = new ArrayList<>(postKeyArrayList); //Copying postKeyArrayList into tempKeyPostArrayList

                /**tempPostArrayList is the concatenation of postArrayList and pollArrayList. Why i am doing this? Because i have applied reverse
                layout on feedRecyclerView so to show post first i need to put all the post at the last of tempPostArrayList*/
                tempPostArrayList.addAll(pollArrayList);
                tempKeyPostArrayList.addAll(pollKeyArrayList);
                setTheAdapterOnRecyclerView(tempPostArrayList,tempKeyPostArrayList);
                break;
            case R.id.postFilterMenu:
                isPollFilterApplied=false;
                isPostFilterApplied=true;
                tempPollArrayList = new ArrayList<>(pollArrayList);
                tempKeyPollArrayList = new ArrayList<>(pollKeyArrayList);

                //Same as above mentioned
                tempPollArrayList.addAll(postArrayList);
                tempKeyPollArrayList.addAll(postKeyArrayList);
                setTheAdapterOnRecyclerView(tempPollArrayList,tempKeyPollArrayList);
                break;
            case R.id.defaultFilterMenu:
                //This will set the default filter.
                setTheAdapterOnRecyclerView(postPollArrayList,postPollKeyArrayList);
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
}
