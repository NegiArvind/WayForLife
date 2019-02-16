package com.wayforlife.Fragments;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.wayforlife.Activities.HomeActivity;
import com.wayforlife.Adapters.NotificationCustomArrayAdapter;
import com.wayforlife.Common.CommonData;
import com.wayforlife.GlobalStateApplication;
import com.wayforlife.Models.MyNotification;
import com.wayforlife.Models.User;
import com.wayforlife.R;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {

    private FloatingActionButton notificationAddFloatingActionButton;
    private RecyclerView notificationRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private Context context;
    private NotificationCustomArrayAdapter notificationCustomArrayAdapter;
    private ArrayList<MyNotification> myNotificationArrayList;
    private ProgressBar notificationProgressBar;
    private HomeActivity homeActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.notification_fragment_layout,container,false);
        context=getContext();
        homeActivity= (HomeActivity) getActivity();
        notificationAddFloatingActionButton=view.findViewById(R.id.notificationAddFloatingActionButton);

        if(CommonData.isAdmin) {
            notificationAddFloatingActionButton.setVisibility(View.VISIBLE);
            notificationAddFloatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogFragment(AddNotificationDialogFragment.newInstance(), getString(R.string.addNotificationDialogFragment));
                    }
            });
        }
        notificationRecyclerView=view.findViewById(R.id.notificationRecyclerView);
        notificationProgressBar=view.findViewById(R.id.notificationProgressBar);

        linearLayoutManager=new LinearLayoutManager(context);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        notificationRecyclerView.setHasFixedSize(true);
        notificationRecyclerView.setLayoutManager(linearLayoutManager);

        myNotificationArrayList=new ArrayList<>();
//
//        String cityState=GlobalStateApplication.usersHashMap.get(CommonData.firebaseCurrentUserUid).getCityName()+"_"+
//                GlobalStateApplication.usersHashMap.get(CommonData.firebaseCurrentUserUid).getStateName();
//        cityState=cityState.replace(' ','_');
//
        String cityState= null;
        if (User.getCurrentUser() != null) {
            cityState = User.getCurrentUser().getCityName()+"_"+
                    User.getCurrentUser().getStateName();
            cityState=cityState.replace(' ','_');
        }


        notificationCustomArrayAdapter=new NotificationCustomArrayAdapter(context,myNotificationArrayList);
        notificationRecyclerView.setAdapter(notificationCustomArrayAdapter);

        checkCityStateNodeExistIfYesThenFetchData(cityState);


        return view;
    }

    private void checkCityStateNodeExistIfYesThenFetchData(final String cityState) {
        GlobalStateApplication.notificationsDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(cityState)){
                    notificationProgressBar.setVisibility(View.GONE);
                    Toast toast = Toast.makeText(context, "There is no any notification", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                fetchAllTheNotificationCorrespondingToUser(cityState);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fetchAllTheNotificationCorrespondingToUser(String cityStateNode) {
        GlobalStateApplication.notificationsDatabaseReference.child(cityStateNode).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MyNotification myNotification=dataSnapshot.getValue(MyNotification.class);
                if(myNotification!=null) {
                    if(notificationProgressBar!=null) {
                        notificationProgressBar.setVisibility(View.GONE);
                    }
                    myNotificationArrayList.add(myNotification);
                    if(notificationCustomArrayAdapter!=null){
                        notificationCustomArrayAdapter.notifyDataSetChanged();
                    }
                }
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

    private void showDialogFragment(DialogFragment dialogFragment, String tag) {
        FragmentTransaction fragmentTransaction;
        if (getFragmentManager() != null) {
            fragmentTransaction = getFragmentManager().beginTransaction();
            dialogFragment.show(fragmentTransaction,tag);
//            fragmentTransaction.add(android.R.id.content,dialogFragment,tag).commit();
        }
    }

    public static NotificationFragment newInstance() {

        Bundle args = new Bundle();
        NotificationFragment fragment = new NotificationFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onResume() {
        super.onResume();
        homeActivity.setActionBarTitle("Notifications");
    }
}
