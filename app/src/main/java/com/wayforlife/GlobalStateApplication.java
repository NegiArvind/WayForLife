package com.wayforlife;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wayforlife.Models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GlobalStateApplication extends Application {

//    public static Map<String,User> usersHashMap=new HashMap<>();
    public static DatabaseReference usersDatabaseReference;
    public static DatabaseReference problemDatabaseReference;
    public static DatabaseReference feedsDatabaseReference;
    public static DatabaseReference eventsDatabaseReference;
    public static DatabaseReference notificationsDatabaseReference;

    private FirebaseDatabase firebaseDatabase;
    public static Boolean isFirstTimeMapIsShowing;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Inside ","application class");
        firebaseDatabase=FirebaseDatabase.getInstance();
        usersDatabaseReference=firebaseDatabase.getReference("Users");
        problemDatabaseReference=firebaseDatabase.getReference("Problems");
        feedsDatabaseReference=firebaseDatabase.getReference("Feeds");
        eventsDatabaseReference=firebaseDatabase.getReference("Events");
        notificationsDatabaseReference=firebaseDatabase.getReference("Notifications");
        isFirstTimeMapIsShowing=true;
//        getAllUsersDetails();
    }

//    private void getAllUsersDetails() {
////        usersArrayList=new ArrayList<>();
//        usersDatabaseReference.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                if(dataSnapshot.getKey()!=null) {
//                    usersHashMap.put(dataSnapshot.getKey(), Objects.requireNonNull(dataSnapshot.getValue(User.class)));
//                }
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                if(dataSnapshot.getKey()!=null){
//                    HashMap<String,String> hashMap=dataSnapshot.getValue(User.class).getLikesFeedHashMap();
//                    usersHashMap.put(dataSnapshot.getKey(),Objects.requireNonNull(dataSnapshot.getValue(User.class)));
//                    Log.i("Changed in application",dataSnapshot.getValue(User.class).getFirstName());
////                    for(String key:hashMap.keySet()){
////                        Log.i("new keys added",hashMap.get(key));
////                    }
//                }
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
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
//    }
}
