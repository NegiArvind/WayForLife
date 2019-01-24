package com.wayforlife.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

import com.squareup.picasso.Picasso;
import com.wayforlife.Fragments.HomeMapFragment;
import com.wayforlife.Models.User;
import com.wayforlife.R;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView userNavigationImageView;
    private TextView emailNavigationTextView;
    private TextView nameNavigationTextView;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initializeAndSetHeaderView();
        Toast.makeText(getApplicationContext(),"Inside of home activty",Toast.LENGTH_SHORT).show();

        addNewFragment(HomeMapFragment.newInstance());

    }

    private void initializeAndSetHeaderView() {

        View view=navigationView.getHeaderView(0);
        userNavigationImageView=view.findViewById(R.id.userImageView);
        emailNavigationTextView=view.findViewById(R.id.userEmailTextView);
        nameNavigationTextView=view.findViewById(R.id.userNameTextView);

        if(User.getCurrentUser()!=null) {
            Picasso.with(HomeActivity.this).load(User.getCurrentUser().getImageUrl()).into(userNavigationImageView);
            emailNavigationTextView.setText(User.getCurrentUser().getEmail());
            nameNavigationTextView.setText(User.getCurrentUser().getFirstName());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()){

            case R.id.donate_navigation:
                break;
            case R.id.request_blood_navigation:
                break;
            case R.id.about_us_navigation:
                break;
            case R.id.follow_us_navigation:
                break;
            case R.id.edit_profile_navigation:
                break;
            case R.id.log_out_navigation_:
                break;
            case R.id.home_bottom_navigation:
                addNewFragment(HomeMapFragment.newInstance());
                break;
            case R.id.feed_bottom_navigation:
                break;
            case R.id.notification_bottom_navigation:
                break;
            case R.id.events_bottom_navigation:
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void addNewFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.homeFrameLayout,fragment).commit();
    }

}
