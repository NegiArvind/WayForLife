package com.wayforlife.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;
import com.wayforlife.Common.CommonData;
import com.wayforlife.Fragments.EditProfileFragment;
import com.wayforlife.Fragments.EventFragment;
import com.wayforlife.Fragments.FeedFragment;
import com.wayforlife.Fragments.HomeMapFragment;
import com.wayforlife.Fragments.NotificationFragment;
import com.wayforlife.Fragments.XyzProblemFragment;
import com.wayforlife.GlobalStateApplication;
import com.wayforlife.Models.User;
import com.wayforlife.R;
import com.wayforlife.Utils.ProgressUtils;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,BottomNavigationView.OnNavigationItemSelectedListener {

    private ImageView userNavigationImageView;
    private TextView emailNavigationTextView;
    private TextView nameNavigationTextView;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private ImageView facebookLogoImageView,twitterLogoImageView,instagramLogoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setTitleTextColor(getResources().getColor(R.color.md_white_1000));
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        CommonData.firebaseCurrentUserUid=Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        bottomNavigationView=findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
//        Toast.makeText(getApplicationContext(),"Inside of home activty",Toast.LENGTH_SHORT).show();
        Log.i("inside of home ", "activity");

//        for(String string:GlobalStateApplication.usersHashMap.keySet()){
//            Log.i("user id",string);
//        }
        bottomNavigationView.setSelectedItemId(R.id.home_bottom_navigation);
        addNewFragment(HomeMapFragment.newInstance(),"homeMapFragment");
        initializeAndSetHeaderView();
    }

    public void initializeAndSetHeaderView() {

        View view=navigationView.getHeaderView(0);
        userNavigationImageView=view.findViewById(R.id.userImageView);
        emailNavigationTextView=view.findViewById(R.id.userEmailTextView);
        nameNavigationTextView=view.findViewById(R.id.userNameTextView);

        if(User.getCurrentUser()!=null) {
            if(User.getCurrentUser().getImageUrl()!=null) {
                Picasso.with(HomeActivity.this).load(User.getCurrentUser().getImageUrl()).into(userNavigationImageView);
            }else{
                userNavigationImageView.setImageResource(R.drawable.circular_person_image_background);
            }
            emailNavigationTextView.setText(User.getCurrentUser().getEmail());
            nameNavigationTextView.setText(User.getCurrentUser().getFirstName());
        }
    }

    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(getSupportFragmentManager().findFragmentByTag(getString(R.string.xyzProblemFragmentTag)) instanceof XyzProblemFragment
                ||getSupportFragmentManager().findFragmentByTag(getString(R.string.editProfileFragmentTag)) instanceof EditProfileFragment){
            addNewFragment(HomeMapFragment.newInstance(),getString(R.string.homeMapFragmentTag));
            bottomNavigationView.setSelectedItemId(R.id.home_bottom_navigation);
        }else{
            showExitAlertDialog();
        }
    }

    private void showExitAlertDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setIcon(R.drawable.way_for_life_logo)
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        finishAffinity();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No",null)
                .show();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
////        getMenuInflater().inflate(R.menu.home, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()){

            case R.id.donate_navigation:
                moveToWebPage(CommonData.donateUrl);
                break;
            case R.id.request_blood_navigation:
                moveToWebPage(CommonData.requestBloodUrl);
                break;
            case R.id.about_us_navigation:
                moveToWebPage(CommonData.aboutUsUrl);
                break;
            case R.id.follow_us_navigation:
                showFollowUsDialog();
                break;
            case R.id.edit_profile_navigation:
                addNewFragment(EditProfileFragment.newInstance(),getString(R.string.editProfileFragmentTag));
                break;
            case R.id.log_out_navigation_:
                showLogOutAlertDialog();
                break;
            case R.id.home_bottom_navigation:
//                Toast.makeText(HomeActivity.this,"home button pressed",Toast.LENGTH_SHORT).show();
                addNewFragment(HomeMapFragment.newInstance(),getString(R.string.homeMapFragmentTag));
                break;
            case R.id.feed_bottom_navigation:
//                Toast.makeText(HomeActivity.this,"feed button pressed",Toast.LENGTH_SHORT).show();
                addNewFragment(FeedFragment.newInstance(),getString(R.string.feedFragmentTag));
                break;
            case R.id.notification_bottom_navigation:
                addNewFragment(NotificationFragment.newInstance(),getString(R.string.notificationFragment));
                break;
            case R.id.events_bottom_navigation:
                addNewFragment(EventFragment.newInstance(),getString(R.string.eventFragmentTag));
                break;
            case R.id.feedback_navigation:
                sendEmailIntent("Feedback for your organisation app");
                break;
            case R.id.share_navigation:
                shareAppWithFriends();
                break;
            case R.id.rate_us_navigation:
                rateMeFunction();
                break;

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void moveToWebPage(String url) {
        Intent intent=new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);

    }

    private void shareAppWithFriends() {
        String appUrl="https://play.google.com/store/apps/details?id="+getPackageName();
        String shareBody = "Non Governmental Organisation app : "+appUrl;
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Way For Life");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent,"Share with"));
    }

    private void rateMeFunction() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    private void sendEmailIntent(String subject) {
        Intent emailIntent=new Intent(Intent.ACTION_SENDTO,Uri.fromParts("mailto","wayforlife@gmail.com",null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,subject); //subject of mail
        emailIntent.putExtra(Intent.EXTRA_TEXT,""); //Body of mail
        startActivity(Intent.createChooser(emailIntent,"Send mail"));
    }



    private void showFollowUsDialog() {
        final AlertDialog alertDialog= new AlertDialog.Builder(HomeActivity.this).create();
        alertDialog.setTitle("Follow Us");
        LayoutInflater layoutInflater=LayoutInflater.from(this);
        View view=layoutInflater.inflate(R.layout.follow_us_alert_dialog_layout,null,false);
        facebookLogoImageView=view.findViewById(R.id.facebookLogoImageView);
        twitterLogoImageView=view.findViewById(R.id.twitterLogoImageView);
        instagramLogoImageView=view.findViewById(R.id.instagramLogoImageView);
        facebookLogoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToWebPage(CommonData.facebookProfileUrl);
                alertDialog.dismiss();
            }
        });
        twitterLogoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToWebPage(CommonData.twitterProfileUrl);
                alertDialog.dismiss();
            }
        });
        instagramLogoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToWebPage(CommonData.instagramProfileUrl);
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(view);
        alertDialog.show();
    }

    private void showLogOutAlertDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setIcon(R.drawable.way_for_life_logo)
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Unsubscribing user from firebase meesssaging so that user will not get any notification if he/she logged out from the app.
                        String cityState=User.currentUser.getCityName()+"_"+User.currentUser.getStateName();
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(cityState.replace(' ','_'));

                        FirebaseAuth.getInstance().signOut();
                        Intent intent=new Intent(HomeActivity.this,LoginActivity.class);
                        startActivity(new Intent(intent));
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No",null)
                .show();
    }

    public void addNewFragment(Fragment fragment,String tag){
        FragmentManager fragmentManager=getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.homeFrameLayout,fragment,tag).commit();
    }

}
