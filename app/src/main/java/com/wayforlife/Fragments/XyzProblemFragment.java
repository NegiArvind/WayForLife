package com.wayforlife.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.wayforlife.Activities.HomeActivity;
import com.wayforlife.Common.CommonData;
import com.wayforlife.GlobalStateApplication;
import com.wayforlife.Models.LocationAddress;
import com.wayforlife.Models.Problem;
import com.wayforlife.Models.SerializeProblem;
import com.wayforlife.Models.User;
import com.wayforlife.R;
import com.wayforlife.Utils.FileUtil;
import com.wayforlife.Utils.ProgressUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class XyzProblemFragment extends Fragment implements View.OnClickListener {

    private ImageView problemImageView,plusImageView;
    private TextView chooseImageTextView;
    private Button reportButton;
    private EditText locationEditText;
    private EditText dateEditText;
    private EditText timeEditText;
    private EditText descriptionEditText;
    private SerializeProblem serializeProblem;
    private Problem problem;
    private Context context;
    private Calendar calendar;
    private String choosenImageUrl;
    private Uri choosenImageUri;
    private HomeActivity homeActivity;
    private ProgressBar chooseImageProgressBar;
    private String problemKeyId;
    private Boolean isMarkerClick=false;
    private String problemUserFirstName="";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.xyz_problem_fragment_layout,container,false);

        context=getContext();


        homeActivity= (HomeActivity) getActivity();
        calendar=Calendar.getInstance();

        //Finding all the widget by their id's
        problemImageView=view.findViewById(R.id.problemImageView);
        plusImageView=view.findViewById(R.id.plusImageView);
        reportButton=view.findViewById(R.id.reportProblemButton);
        locationEditText=view.findViewById(R.id.locationEditText);
        dateEditText=view.findViewById(R.id.dateEditText);
        timeEditText=view.findViewById(R.id.timeEditText);
        descriptionEditText=view.findViewById(R.id.descriptionEditText);
        chooseImageTextView=view.findViewById(R.id.chooseImageTextView);
        chooseImageProgressBar=view.findViewById(R.id.chooseImageProgressBar);

        /** Below i am checking that if map marker is clicked or not. if it is clicked than i am filling the problem details
         * Also if he is an admin then remove button will be visible to him. and by pressing this button he can remove or delete
         * the problem.*/
        if(getArguments()!=null){
            isMarkerClick=getArguments().getBoolean("isMarkerClick");
            if(isMarkerClick){
                problemKeyId=getArguments().getString("problemKeyId");
                serializeProblem = (SerializeProblem) getArguments().getSerializable("serializeProblem");
                if (serializeProblem != null) {
                    problem = serializeProblem.getProblem();
//                    Log.i("user problem name",GlobalStateApplication.usersHashMap.get(problemKeyId).getFirstName());
                    getProblemUserFirstName();
                    setAllTheProblemDetails();
                    if(CommonData.isAdmin){
                        reportButton.setText("Remove");
                        reportButton.setOnClickListener(this);
                    }else{
                        reportButton.setVisibility(View.GONE);
                    }
                }
            }else{
                serializeProblem = (SerializeProblem) getArguments().getSerializable("serializeProblem");
                if (serializeProblem != null) {
                    problem = serializeProblem.getProblem();
                    setLocationTimeAndDateDetails();
                    problemImageView.setOnClickListener(this);
                    reportButton.setOnClickListener(this);
                }
            }
        }
        return view;
    }

    private void getProblemUserFirstName() {
        GlobalStateApplication.usersDatabaseReference.child(problem.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                problemUserFirstName=user.getFirstName();
                onResume();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**Below method will set the location , time and date details onto their position.This below method will be called when
    // user clicks on google map.*/
    private void setLocationTimeAndDateDetails() {

        LocationAddress locationAddress=problem.getLocationAddress();
        locationEditText.setText(locationAddress.getAddress()+"Latitude: "+locationAddress.getLatitude()+" Longitude: "+
                locationAddress.getLongitude());
        dateEditText.setText(problem.getDate());
        timeEditText.setText(problem.getTime());
        dateEditText.setOnClickListener(this);
        timeEditText.setOnClickListener(this);

        // disabling editText to edit
        dateEditText.setFocusable(false);
        timeEditText.setFocusable(false);
        locationEditText.setFocusable(false);

        chooseImageProgressBar.setVisibility(View.GONE);
    }

    /**below method will set all the problem details onto their position. This below method will be called when
    user clicks on marker.*/
    private void setAllTheProblemDetails() {
        LocationAddress locationAddress=problem.getLocationAddress();
        Picasso.with(context).load(problem.getImageUrl()).into(problemImageView, new Callback() {
            @Override
            public void onSuccess() {
                if(chooseImageProgressBar!=null){
                    chooseImageProgressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onError() {

            }
        });
        problemImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfilePhotoDialogFragment(ProfilePhotoDialogFragment.newInstance(problem.getImageUrl(),false));
            }
        });

        locationEditText.setText(locationAddress.getAddress()+"Latitude: "+locationAddress.getLatitude()+" Longitude: "+
        locationAddress.getLongitude());
        dateEditText.setText(problem.getDate());
        timeEditText.setText(problem.getTime());
        descriptionEditText.setText(problem.getDescription());
        locationEditText.setFocusable(false);
        dateEditText.setFocusable(false);
        timeEditText.setFocusable(false);
        descriptionEditText.setFocusable(false);
        chooseImageTextView.setVisibility(View.GONE);
        plusImageView.setVisibility(View.GONE);
    }

    private void showProfilePhotoDialogFragment(ProfilePhotoDialogFragment profilePhotoDialogFragment) {
        FragmentTransaction fragmentTransaction;
        AppCompatActivity appCompatActivity= (AppCompatActivity) context;
        if (appCompatActivity.getSupportFragmentManager() != null) {
            fragmentTransaction = appCompatActivity.getSupportFragmentManager().beginTransaction();
            profilePhotoDialogFragment.show(fragmentTransaction,getString(R.string.profilePhotoDialogFragment));
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dateEditText:
                showDatePicker();
                break;
            case R.id.timeEditText:
                showTimePicker();
                break;
            case R.id.reportProblemButton:

                /** if it will be admin and he had clicked the marker then he can remove the problem.
                Admin can also report the problem.*/
                if (CommonData.isAdmin && isMarkerClick ) {
                    showRemoveAlertDialog();
                }else{
                    if(isAllDetailsAreValid()){
                        uploadImageAndThenSaveIntoFbDb();
                    }
                }
                break;

            case R.id.problemImageView:
                chooseImage();
                break;
        }
    }

    private void showRemoveAlertDialog() {
        new AlertDialog.Builder(context)
                .setTitle("Remove problem")
                .setIcon(R.drawable.way_for_life_logo)
                .setMessage("Are you sure you want to remove this problem?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ProgressUtils.showKProgressDialog(context,"Removing..");
                        deleteProblemFromFbDb();
                    }
                })
                .setNegativeButton("No",null)
                .show();
    }

    //Below method will delete the problem from firebase database.
    private void deleteProblemFromFbDb() {
        GlobalStateApplication.problemDatabaseReference.child(problemKeyId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    ProgressUtils.cancelKprogressDialog();
                    Toast.makeText(homeActivity,"Removed Successfully",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    /**Below method will first upload the image into firebase storage and then get a download url for that image and then save the
    problem data with image url into firebase database*/
    private void uploadImageAndThenSaveIntoFbDb() {
        ProgressUtils.showKProgressDialog(context,"Reporting...");
        String filename= UUID.randomUUID().toString();
        final StorageReference storageReference=FirebaseStorage.getInstance().getReference("problemImages/"+filename);
        storageReference.putFile(choosenImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                choosenImageUrl=Objects.requireNonNull(task.getResult()).toString();
                                saveProblemIntoFirebaseDatabase();
                            }
                        }
                    });
                }else{
                    ProgressUtils.cancelKprogressDialog();
                    Toast.makeText(homeActivity,Objects.requireNonNull(task.getException()).toString(),Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    //Saving problem details into firebase database
    private void saveProblemIntoFirebaseDatabase() {
        problem.setDescription(descriptionEditText.getText().toString());
        problem.setTime(timeEditText.getText().toString());
        problem.setDate(dateEditText.getText().toString());
        problem.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        problem.setImageUrl(choosenImageUrl);
        GlobalStateApplication.problemDatabaseReference.push().setValue(problem).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    ProgressUtils.cancelKprogressDialog();
                    Toast.makeText(context,"Successfully Reported",Toast.LENGTH_SHORT).show();
                    homeActivity.addNewFragment(HomeMapFragment.newInstance(),"homeMapFragment");
                }else{
                    ProgressUtils.cancelKprogressDialog();
                    Toast.makeText(context,"Not Successfully reported "+ task.getException(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**This below method will send an intent to gallary when user clicks "plus". Once user will choose an image then onActivityResult
    method will be called.*/
    private void chooseImage(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),CommonData.PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CommonData.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            try {
                choosenImageUri = data.getData();

                //getting the bitmap from imageuri and then showing that image into imageview
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(homeActivity.getContentResolver(), choosenImageUri);
                problemImageView.setImageBitmap(bitmap);
                plusImageView.setVisibility(View.GONE);
                chooseImageTextView.setVisibility(View.GONE);

                //FileUtil is a custom class.getPath method will return the path of uri.
                File file = new File(FileUtil.getPath(context,choosenImageUri));
                Log.i("path", choosenImageUri.getPath());
                try {
                    //This below method is used for compressing the image. Image can be of 4-5 mb which leads to take more storage in
                    //firebase storage. So before uploading image into firebase storage i am going to compress the image first.
                    File compressedImage = new Compressor(homeActivity)
                            .setMaxWidth(640)
                            .setMaxHeight(480)
                            .setQuality(65)
                            .setCompressFormat(Bitmap.CompressFormat.WEBP)
                            .compressToFile(file);
                    choosenImageUri = Uri.fromFile(compressedImage);
                    Log.i("choosen Image Uri", " " + choosenImageUri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**This below method will check if all the problem details which user has entered is right or wrong*/
    private boolean isAllDetailsAreValid() {

        if(choosenImageUri==null){
            Toast.makeText(context,"Please first choose the image",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(descriptionEditText.getText().toString().trim().length()==0){
            descriptionEditText.setError("Please enter the description");
            descriptionEditText.requestFocus();
            return false;
        }
        if(dateEditText.getText().toString().trim().length()==0){
            dateEditText.setError("Please choose the date");
            dateEditText.requestFocus();
            return false;
        }
        if(timeEditText.getText().toString().trim().length()==0){
            timeEditText.setError("Please choose the time");
            timeEditText.requestFocus();
            return false;
        }
        return true;
    }

    public static XyzProblemFragment newInstance(SerializeProblem serializeProblem,boolean isMarkerClick,String problemKeyId){
        Bundle args = new Bundle();
        args.putBoolean("isMarkerClick",isMarkerClick);
        args.putSerializable("serializeProblem", serializeProblem);
        args.putString("problemKeyId",problemKeyId);
        XyzProblemFragment fragment = new XyzProblemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //Below method will show a date picker fragment
    private void showDatePicker() {// Get Current Date

        int year,month,day;
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
                        calendar.set(year,monthOfYear,dayOfMonth);
                        dateEditText.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                }, year, month, day);
        datePickerDialog.show();
    }


    //Below method will show a time picker fragment.
    private void showTimePicker(){
        int mHour,mMinute;
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        boolean isPM = (hourOfDay >= 12);
                        timeEditText.setText(String.format("%02d:%02d %s",
                                (hourOfDay == 12 || hourOfDay == 0) ? 12 : hourOfDay % 12, minute, isPM ? "PM" : "AM"));
//                        timeEditText.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isMarkerClick) {
            homeActivity.setActionBarTitle(problemUserFirstName+ " Complaint");
        }else{
            homeActivity.setActionBarTitle("Add your report");
        }
    }
}
