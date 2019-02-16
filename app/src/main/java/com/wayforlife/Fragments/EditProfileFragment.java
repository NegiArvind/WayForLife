package com.wayforlife.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.wayforlife.Activities.HomeActivity;
import com.wayforlife.Common.CommonData;
import com.wayforlife.GlobalStateApplication;
import com.wayforlife.Models.User;
import com.wayforlife.R;
import com.wayforlife.Utils.AuthUtil;
import com.wayforlife.Utils.FileUtil;
import com.wayforlife.Utils.ProgressUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class EditProfileFragment extends Fragment implements AdapterView.OnItemSelectedListener,View.OnClickListener {

    private Context context;
    private ImageView editProfileUserImageView;
    private EditText firstNameEditProfileEditText,lastNameEditProfileEditText,phoneNumberEditProfileEditText;
    private Spinner editProfileCitySpinner,editProfilestateSpinner;
    private Button editProfileSubmitButton;
    private String earlierFirstName,earlierLastName,earlierMobileNumber,earlierCityName,earlierStateName,tempCityName,tempStateName;
    private User user;
    private HashMap<String,ArrayList<String>> stateAndCityHashMap=new HashMap<>();
    private ArrayList<String> stateArrayList=new ArrayList<>();
    private ArrayList<String> cityArrayList;
    private ArrayAdapter<String> stateAdapter;
    private ArrayAdapter<String> cityAdapter;
    private String changedState,changedCity;
    private HomeActivity homeActivity;
    private String newPhoneNumber,changedFirstName,changedLastName;
    private String choosenImageUrl;
    private Uri choosenImageUri;
    private boolean isNumberAlsoChanged=false;
    private boolean firstNameChange=false,lastNameChange=false,cityStateChange=false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.edit_profile_fragment_layout,container,false);

        context=getContext();
        homeActivity= (HomeActivity) getActivity();
        firstNameEditProfileEditText=view.findViewById(R.id.editProfileFirstNameEditText);
        lastNameEditProfileEditText=view.findViewById(R.id.editProfileLastNameEditText);
        phoneNumberEditProfileEditText=view.findViewById(R.id.editProfilePhoneNumberEditText);
        editProfileCitySpinner=view.findViewById(R.id.editProfileCitySpinner);
        editProfilestateSpinner=view.findViewById(R.id.editProfilestateSpinner);
        editProfileSubmitButton=view.findViewById(R.id.editPrifleSubmitButton);
        editProfileUserImageView=view.findViewById(R.id.editProfileUserImageView);

        cityArrayList=new ArrayList<>();

//        editProfileSubmitButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if(isFirstNameChanged()||isLastNameChanged()||isNumberChanged()||isStateCityChanged()){
//                    showEditAlertDialog();
//                }
//                else{
//                    Toast.makeText(context, "You haven't make any changes", Toast.LENGTH_SHORT).show();
//                }
//                if(isStateCityChanged()){
//                    user.setStateName(changedState);
//                    user.setCityName(changedCity);
//                }
//                if(firstNameEditProfileEditText.getText().toString().trim().length()!=0)
//                user.setFirstName(firstNameEditProfileEditText.getText().toString());
//
//                user.setLastName(lastNameEditProfileEditText.getText().toString());
//                if(isNumberChanged()){
//                    if(AuthUtil.isVailidPhone(newPhoneNumber)) {
//                        //Go for verification
//                        user.setPhoneNumber(newPhoneNumber);
//                        showDialogFragment(VerificationFragment1.newInstance(user),"verificationFragment");
////                        homeActivity.addNewFragment(VerificationFragment.newInstance(user), "verificationFragment");
//                    }else{
//                        phoneNumberEditProfileEditText.setError("Please enter correct number");
//                        phoneNumberEditProfileEditText.requestFocus();
//                    }
//                }else{
//                    GlobalStateApplication.usersDatabaseReference.child(CommonData.firebaseCurrentUserUid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if(task.isSuccessful()){
//                                Toast.makeText(context, "Profile successfully edited", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                }
//            }
//        });
        editProfileSubmitButton.setOnClickListener(this);
        editProfileUserImageView.setOnClickListener(this);


        startExecutingAsynckTask();

        user=User.getCurrentUser();
        if(user!=null){
            setAllDetails();
        }

        editProfileCitySpinner.setOnItemSelectedListener(this);
        editProfilestateSpinner.setOnItemSelectedListener(this);

        return view;
    }

    private void setAllDetails() {
        earlierFirstName=user.getFirstName();
        earlierLastName=user.getLastName();
        tempCityName=user.getCityName();
        earlierCityName=tempCityName+" :)";
        tempStateName=user.getStateName();
        earlierStateName=tempStateName+" :)";
        earlierMobileNumber=user.getPhoneNumber();
        changedCity=earlierCityName;
        changedState=earlierStateName;

        if(user.getImageUrl()!=null){
            Picasso.with(context).load(user.getImageUrl()).into(editProfileUserImageView);
        }else{
            editProfileUserImageView.setImageResource(R.drawable.ic_launcher_background);
        }

        firstNameEditProfileEditText.setText(earlierFirstName);

        if(user.getLastName()!=null)
        lastNameEditProfileEditText.setText(earlierLastName);

        phoneNumberEditProfileEditText.setText(earlierMobileNumber);


        stateArrayList.add(earlierStateName);
        stateAdapter=new ArrayAdapter<>(context,R.layout.support_simple_spinner_dropdown_item,
                stateArrayList);
        editProfilestateSpinner.setAdapter(stateAdapter);


        ArrayList<String> tempArrayList=new ArrayList<>();
        tempArrayList.add(earlierCityName+ " :)");
        ArrayAdapter<String> tempAdapter=new ArrayAdapter<>(context,R.layout.support_simple_spinner_dropdown_item,
                tempArrayList);
        editProfileCitySpinner.setAdapter(tempAdapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.editPrifleSubmitButton:
                firstNameChange=isFirstNameChanged();
                lastNameChange=isLastNameChanged();
                cityStateChange=isStateCityChanged();
                isNumberAlsoChanged=isNumberChanged();

                if(firstNameChange||lastNameChange||cityStateChange||isNumberAlsoChanged||choosenImageUri!=null){
                    showEditAlertDialog();
                }
                else{
                    Toast.makeText(context, "You haven't make any changes", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.editProfileUserImageView:
                chooseImage();
                break;
        }
    }


    private void showEditAlertDialog() {
        new AlertDialog.Builder(context)
                .setTitle("Edit")
                .setMessage("Your updated profile will be reflected everywhere. Are you sure you want to update your profile?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        makeAppropriateUpdate();
                    }
                })
                .setNegativeButton("No",null)
                .show();
    }

    private void makeAppropriateUpdate() {
        if(isNumberAlsoChanged){
            if(AuthUtil.isVailidPhone(newPhoneNumber)) {
                if(choosenImageUri!=null) {
                    uploadImageIntoFirebaseStorage();
                }else{
                    user.setPhoneNumber(newPhoneNumber);
                    startShowingDialogFragment();
                }
            }else{
                phoneNumberEditProfileEditText.setError("Please enter correct number");
                phoneNumberEditProfileEditText.requestFocus();
            }
        }else{
            if(choosenImageUri!=null){
                uploadImageIntoFirebaseStorage();
            }else{
                ProgressUtils.showKProgressDialog(context,"Updating..");
                updateUserInFirebaseDatabase();
            }
        }
    }

    private void updateUserInFirebaseDatabase() {
        GlobalStateApplication.usersDatabaseReference.child(CommonData.firebaseCurrentUserUid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context, "Profile successfully Updated", Toast.LENGTH_SHORT).show();
                    if(cityStateChange){
                        String oldTopic=tempCityName+'_'+tempStateName;
                        final String newTopic=changedCity+'_'+changedState;
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(oldTopic.replace(' ','_')).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                FirebaseMessaging.getInstance().subscribeToTopic(newTopic.replace(' ','_'));
                                Log.i("newTopicUpdate","Good bacho");
                            }
                        });
                    }
                    ProgressUtils.cancelKprogressDialog();
                    homeActivity.addNewFragment(EditProfileFragment.newInstance(),getString(R.string.editProfileFragmentTag));
                }
            }
        });
    }

    private boolean isFirstNameChanged() {
        changedFirstName=firstNameEditProfileEditText.getText().toString().trim();
        if(changedFirstName.equalsIgnoreCase(earlierFirstName)){
            return false;
        }
        user.setFirstName(changedFirstName);
        return true;
    }


    private boolean isLastNameChanged() {
        changedLastName=lastNameEditProfileEditText.getText().toString().trim();
        if(changedLastName.equalsIgnoreCase(earlierLastName)){
            return false;
        }
        user.setLastName(changedLastName);
        return true;
    }

    private boolean isNumberChanged() {
        newPhoneNumber=phoneNumberEditProfileEditText.getText().toString().trim();
        if(newPhoneNumber.length()!=0 && !newPhoneNumber.equalsIgnoreCase(earlierMobileNumber)){
            return true;
        }
        return false;
    }

    private boolean isStateCityChanged() {
        if(!(changedState.equalsIgnoreCase(earlierStateName) || changedCity.equalsIgnoreCase(earlierCityName))){
            Toast.makeText(context, "Will go ahead "+changedState+" "+changedCity, Toast.LENGTH_SHORT).show();
            user.setCityName(changedCity);
            user.setStateName(changedState);
            return true;
        }
        return false;
    }


    private void startExecutingAsynckTask() {

        //Getting the url from firebase database and then download the json file using asynck task.
        FirebaseDatabase.getInstance().getReference("stateAndCityUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String stateAndCityUrl=dataSnapshot.getValue(String.class);

                // executing the asynck task.
                new StateAndCityDownloadTask().execute(stateAndCityUrl);

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

    public static EditProfileFragment newInstance() {
        Bundle args = new Bundle();
        EditProfileFragment fragment = new EditProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (parent.getId()){
                case R.id.editProfilestateSpinner:
//                    cityArrayList = new ArrayList<>();
                    changedState = (String) parent.getItemAtPosition(position);
                    Log.i("choosen state", changedState);
                    cityArrayList = stateAndCityHashMap.get(changedState);
                    if (cityArrayList == null) {
                        cityArrayList=new ArrayList<>();
                    }
                    cityArrayList.add(0, earlierCityName);
                    ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item,
                            cityArrayList);
//                        cityAdapter.notifyDataSetChanged();
                    editProfileCitySpinner.setAdapter(cityAdapter);
                    Toast.makeText(context, changedState + " selected", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.editProfileCitySpinner:
                    if (cityArrayList.size() != 0) {
                        changedCity = (String) parent.getItemAtPosition(position);
                        Toast.makeText(context, changedCity + " selected", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
    }



    /**Below method will first upload the image into firebase storage and then get a download url for that image and then save the
     problem data with image url into firebase database*/
    private void uploadImageIntoFirebaseStorage() {
        ProgressUtils.showKProgressDialog(context,"Updating...");
        String filename= UUID.randomUUID().toString();
        final StorageReference storageReference=FirebaseStorage.getInstance().getReference("userProfileImages/"+filename);
        storageReference.putFile(choosenImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                choosenImageUrl=Objects.requireNonNull(task.getResult()).toString();
                                user.setImageUrl(choosenImageUrl);
                                if(isNumberAlsoChanged) {
                                    user.setPhoneNumber(newPhoneNumber);
                                    ProgressUtils.cancelKprogressDialog();
                                    startShowingDialogFragment();
                                }else{
                                    updateUserInFirebaseDatabase();
                                }
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

    private void startShowingDialogFragment() {
        if(cityStateChange) {
            showDialogFragment(VerificationFragment.newInstance(user, true,tempCityName+'_'+tempStateName), getString(R.string.verificationDialogFragmentTag));
        }else{
            showDialogFragment(VerificationFragment.newInstance(user, true,null), getString(R.string.verificationDialogFragmentTag));
        }
    }

    /**This below method will send an intent to gallary when user clicks "plus". Once user will choose an image then onActivityResult
     method will be called.*/
    private void chooseImage(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Profile Photo"),CommonData.USER_PICK_IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CommonData.USER_PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            try {
                choosenImageUri = data.getData();

                //getting the bitmap from imageuri and then showing that image into imageview
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(homeActivity.getContentResolver(), choosenImageUri);
                editProfileUserImageView.setImageBitmap(bitmap);
//                plusImageView.setVisibility(View.GONE);
//                chooseImageTextView.setVisibility(View.GONE);

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



    protected class StateAndCityDownloadTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            StringBuilder current = new StringBuilder();
            try {
                URL url;
                HttpURLConnection httpURLConnection = null;
                try{
                    url = new URL(strings[0]);
                    httpURLConnection = (HttpURLConnection) url.openConnection();

                    InputStream inputStream= httpURLConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                    int data = inputStreamReader.read();
                    while (data != -1) {
                        current.append((char) data);
                        data = inputStreamReader.read();
//                        Log.i("current data.. ",current);
                    }

                    // return the data to onPostExecute method
                    return current.toString();

                }catch (Exception e) {
                    e.printStackTrace();
                }finally{
                    if(httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
            return current.toString();
        }



        @Override
        protected void onPostExecute(String data) {
            Log.d("data", data);
            try {

                // JSON Parsing of data
                JSONObject stateJsonObject=new JSONObject(data);
                JSONArray jsonArray = stateJsonObject.getJSONArray("states");

                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    JSONArray districtJsonArray=jsonObject.getJSONArray("districts");
                    ArrayList<String> districtArrayList=new ArrayList<>();
                    for(int j=0;j<districtJsonArray.length();j++){
                        districtArrayList.add(districtJsonArray.get(j).toString());
                    }

                    //Adding state name into stateArrayList
                    stateArrayList.add(jsonObject.getString("state"));

                    //Below district arraylist will be mapped with its corresponding state
                    stateAndCityHashMap.put(jsonObject.getString("state"),districtArrayList);
                }
                if(stateAdapter!=null) {
                    stateAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onResume() {
        super.onResume();
        homeActivity.setActionBarTitle("Edit Profile");
    }

}
