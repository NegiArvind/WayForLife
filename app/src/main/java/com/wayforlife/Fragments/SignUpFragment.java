package com.wayforlife.Fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wayforlife.Activities.LoginActivity;
import com.wayforlife.GlobalStateApplication;
import com.wayforlife.Models.User;
import com.wayforlife.R;
import com.wayforlife.Utils.AuthUtil;
import com.wayforlife.Utils.ProgressUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/** This fragment is used for signUp the user */
public class SignUpFragment extends Fragment implements AdapterView.OnItemSelectedListener,View.OnClickListener {

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText numberEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Spinner citySpinner;
    private Spinner stateSpinner;
    private ImageView visibilityConfirmPasswordImageView;
    private ImageView visibilityPasswordImageView;
    private Button sendOtpButton;
    private String firstName,email,number,password,confirmPassword,lastName,state,city;
    private LoginActivity loginActivity;
    private Context context;
    private HashMap<String,ArrayList<String>> stateAndCityHashMap=new HashMap<>();
    private ArrayList<String> stateArrayList=new ArrayList<>();
    private ArrayList<String> cityArrayList;
    private ArrayAdapter<String> stateAdapter;
    private boolean isPasswordVisible=false;
    private boolean isConfirmPasswordVisible=false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Inflating layout file for this fragment
        View view=inflater.inflate(R.layout.sign_up_fragment_layout,container,false);

        context=getContext();

        loginActivity=(LoginActivity)getActivity();

        //Initializing all the widget
        firstNameEditText=view.findViewById(R.id.firstNameEditText);
        lastNameEditText=view.findViewById(R.id.lastNameEditText);
        emailEditText=view.findViewById(R.id.emailEditText);
        numberEditText=view.findViewById(R.id.numberEditText);
        passwordEditText=view.findViewById(R.id.passwordEditText);
        confirmPasswordEditText=view.findViewById(R.id.confirmPasswordEditText);
        citySpinner=view.findViewById(R.id.citySpinner);
        stateSpinner=view.findViewById(R.id.stateSpinner);
        visibilityConfirmPasswordImageView=view.findViewById(R.id.visibilityConfirmPasswordImageView);
        visibilityPasswordImageView=view.findViewById(R.id.visibilityPasswordImageView);
        sendOtpButton=view.findViewById(R.id.sendOtpButton);

        //Start executing the asynck task
        startExecutingAsynckTask();

//        getAllDataFromJson();

        stateArrayList.add(0,"Select State");
        stateAdapter=new ArrayAdapter<>(context,R.layout.support_simple_spinner_dropdown_item,
                stateArrayList);
        stateSpinner.setAdapter(stateAdapter);


        citySpinner.setOnItemSelectedListener(this);
        stateSpinner.setOnItemSelectedListener(this);
        visibilityPasswordImageView.setOnClickListener(this);
        visibilityConfirmPasswordImageView.setOnClickListener(this);
        sendOtpButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.sendOtpButton:
                verifyAndSendOtp();
                break;

            case R.id.visibilityPasswordImageView:
                if(isPasswordVisible){
                    visibilityPasswordImageView.setImageResource(R.drawable.visibility_off_image);
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    isPasswordVisible=false;
                }else{
                    visibilityPasswordImageView.setImageResource(R.drawable.visibility_image);
                    passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    isPasswordVisible=true;
                }
                break;

            case R.id.visibilityConfirmPasswordImageView:
                if(isConfirmPasswordVisible){
                    visibilityConfirmPasswordImageView.setImageResource(R.drawable.visibility_off_image);
                    confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    isConfirmPasswordVisible=false;
                }else{
                    visibilityConfirmPasswordImageView.setImageResource(R.drawable.visibility_image);
                    confirmPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    isConfirmPasswordVisible=true;
                }
                break;
        }
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


    /** This below function will first verify the user details and then send otp to user entered number*/

    private void verifyAndSendOtp() {
        if(isAllDetailsCorrect()){
            ProgressUtils.showKProgressDialog(context,"Getting you in..");
            FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                @Override
                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                    if(task.isSuccessful()){
                        ArrayList<String> arrayList;
                        arrayList=(ArrayList<String>) Objects.requireNonNull(task.getResult()).getSignInMethods();
                        //if the size is zero it means this email is not authenticated with the firebase
                        if(arrayList.size()==0){

                            User user=new User();
                            user.setFirstName(firstName);
                            if(lastNameEditText.getText().toString().trim().length()!=0)
                                user.setLastName(lastNameEditText.getText().toString());
                            user.setPassword(password);
                            user.setEmail(email);
                            user.setPhoneNumber(number);
                            user.setCityName(city);
                            user.setStateName(state);

//                            if user is new then we will go for verification of number.
//                            loginActivity.addNewFragment(VerificationFragment.newInstance(user));
                            ProgressUtils.cancelKprogressDialog();
                            showDialogFragment(VerificationFragment.newInstance(user,false,null),getString(R.string.verificationDialogFragmentTag));

                        }else{
                            ProgressUtils.cancelKprogressDialog();
                            Toast.makeText(loginActivity,"You are already registered. Please login",Toast.LENGTH_LONG).show();
                            loginActivity.addNewFragment(LoginFragment.newInstance(),getString(R.string.loginFragmentTag));
                        }
                    }
                }
            });
//            if(!isUserAlreadyExist()){
//
//                User user=new User();
//                user.setFirstName(firstName);
//                if(lastNameEditText.getText().toString().trim().length()!=0)
//                user.setLastName(lastNameEditText.getText().toString());
//                user.setPassword(password);
//                user.setEmail(email);
//                user.setPhoneNumber(number);
//                user.setCityName(city);
//                user.setStateName(state);
//
//                //if user is new then we will go for verification of number.
////                loginActivity.addNewFragment(VerificationFragment.newInstance(user));
//                showDialogFragment(VerificationFragment.newInstance(user,false,null),getString(R.string.verificationDialogFragmentTag));
//
//            }else{
//                Toast.makeText(loginActivity,"You are already registered. Please login",Toast.LENGTH_LONG).show();
//                loginActivity.addNewFragment(LoginFragment.newInstance());
//            }
        }
    }

    private void showDialogFragment(DialogFragment dialogFragment, String tag) {
        FragmentTransaction fragmentTransaction;
        if (getFragmentManager() != null) {
            fragmentTransaction = getFragmentManager().beginTransaction();
            dialogFragment.show(fragmentTransaction,tag);
//            fragmentTransaction.add(android.R.id.content,dialogFragment,tag).commit();
        }
    }

//    private boolean isUserAlreadyExist() {
//
//        for(User user:GlobalStateApplication.usersHashMap.values()){
//
//            /** if entered email or mobile number matched with the email or number present in users database
//            then it means users has already sign up.*/
//
//            if(user.getEmail().equals(email)||user.getPhoneNumber().equals(number)){
//                return true;
//            }
//        }
//        return false;
//    }

    private boolean isAllDetailsCorrect() {
        firstName=firstNameEditText.getText().toString().trim();
        email=emailEditText.getText().toString();
        number=numberEditText.getText().toString().trim();
        password=passwordEditText.getText().toString();
        confirmPassword=confirmPasswordEditText.getText().toString();
        if(firstName.equals("")){
            firstNameEditText.setError("Please enter first name");
            firstNameEditText.requestFocus();
            return false;
        }

        if(!AuthUtil.isValidEmail(email)){
            emailEditText.setError("Please enter valid email");
            emailEditText.requestFocus();
            return false;
        }

        if(!AuthUtil.isVailidPhone(number)){
            numberEditText.setError("Please enter valid number");
            numberEditText.requestFocus();
            return false;
        }

        if(stateSpinner.getSelectedItemPosition()==0){
            Toast.makeText(context,"Please first choose the state name",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(citySpinner.getSelectedItemPosition()==0){
            Toast.makeText(context,"Please first choose the city name",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(password.length()<6){
            passwordEditText.setError("Password must contain at least 6 characters");
            passwordEditText.requestFocus();
            return false;
        }

        if(!confirmPassword.equals(password)){
            confirmPasswordEditText.setError("Password mismatched");
            confirmPasswordEditText.requestFocus();
            return false;
        }


        return true;

    }

    public static SignUpFragment newInstance() {

        Bundle args = new Bundle();
        SignUpFragment fragment = new SignUpFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //Whenever user select any item from spinner then this below method will be called.
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(position!=0) {
            switch (parent.getId()){
                
                case R.id.stateSpinner:
                    cityArrayList= new ArrayList<>();
                    state = (String) parent.getItemAtPosition(position);
                    Log.i("choosen state",state);
                    cityArrayList = stateAndCityHashMap.get(state);
                    if (cityArrayList != null) {
                        cityArrayList.add(0, "Select city");
                        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item,
                                cityArrayList);
                        citySpinner.setAdapter(cityAdapter);
                    }
                    Toast.makeText(context, state + " selected", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.citySpinner:
                    if (cityArrayList.size() != 0) {
                        city = (String) parent.getItemAtPosition(position);
                        Toast.makeText(context, city + " selected", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    //This below async task will read the json file and then return the state and city data.

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
                    Log.i("states",jsonObject.getString("state"));

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

//    public String getJson()
//    {
//        String json=null;
//        try
//        {
//            // Opening cities.json file
//            InputStream inputStream = getResources().getAssets().open("states_and_districts.json");
//            // is there any content in the file
//            int size = inputStream.available();
//            byte[] buffer = new byte[size];
//            // read values in the byte array
//            inputStream.read(buffer);
//            // close the stream --- very important
//            inputStream.close();
//            // convert byte to string
//            json = new String(buffer, "UTF-8");
//        }
//        catch (IOException ex)
//        {
//            ex.printStackTrace();
//            return json;
//        }
//        return json;
//    }
//
//
//    private void getAllDataFromJson(){
//
//        String data=getJson();
//        Log.i("data", data);
//        // dismiss the progress dialog after receiving data from API
//
//        try {
//            // JSON Parsing of data
//            JSONObject stateJsonObject=new JSONObject(data);
//            JSONArray jsonArray = stateJsonObject.getJSONArray("states");
////            Log.i("json length",String.valueOf(jsonArray.length()));
//            for(int i=0;i<jsonArray.length();i++){
//                JSONObject jsonObject=jsonArray.getJSONObject(i);
//                JSONArray districtJsonArray=jsonObject.getJSONArray("districts");
//                ArrayList<String> districtArrayList=new ArrayList<>();
////                Log.i("district length",String.valueOf(districtJsonArray.length()));
//                for(int j=0;j<districtJsonArray.length();j++){
//                    districtArrayList.add(districtJsonArray.get(j).toString());
//                }
//                Log.i("states ",jsonObject.getString("state"));
//                stateArrayList.add(jsonObject.getString("state"));
//                stateAndCityHashMap.put(jsonObject.getString("state"),districtArrayList);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }


}
