package com.wayforlife.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wayforlife.Common.CommonData;
import com.wayforlife.GlobalStateApplication;
import com.wayforlife.Models.MyNotification;
import com.wayforlife.R;
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

public class AddNotificationDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {

    private Toolbar notificationToolbar;
    private Context context;
    private EditText titleNotificationEditText,descriptionNotificationEditText;
    private Spinner stateNotificationSpinner,cityNotificationSpinner;
    private Button createNotificationButton;
    private HashMap<String,ArrayList<String>> stateAndCityHashMap=new HashMap<>();
    private ArrayList<String> stateArrayList=new ArrayList<>();
    private ArrayList<String> cityArrayList;
    private ArrayAdapter<String> stateAdapter;
    private String state,city;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,R.style.AppTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.add_notification_dialog_fragment_layout,container,false);
        context=getContext();

        startExecutingAsynckTask();

        notificationToolbar=view.findViewById(R.id.notificationToolbar);
        notificationToolbar.setTitleTextColor(getResources().getColor(R.color.md_white_1000));
        notificationToolbar.setTitle("Create new notification");
        notificationToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        titleNotificationEditText=view.findViewById(R.id.titleNotificationEditText);
        descriptionNotificationEditText=view.findViewById(R.id.descriptionNotificationEditText);
        stateNotificationSpinner=view.findViewById(R.id.stateNotificationSpinner);
        cityNotificationSpinner=view.findViewById(R.id.cityNotificationSpinner);
        createNotificationButton=view.findViewById(R.id.createNotificationButton);

        createNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allDetailsValid()){
                    storeNotificationIntoFirebaseDatabase();
                }
            }
        });
        cityNotificationSpinner.setOnItemSelectedListener(this);
        stateNotificationSpinner.setOnItemSelectedListener(this);

        stateArrayList.add(0,"Select State");
        stateAdapter=new ArrayAdapter<>(context,R.layout.support_simple_spinner_dropdown_item,
                stateArrayList);
        stateNotificationSpinner.setAdapter(stateAdapter);

        return view;
    }

    private void storeNotificationIntoFirebaseDatabase() {
        ProgressUtils.showKProgressDialog(context,"Creating...");
        MyNotification myNotification=new MyNotification();
        myNotification.setTitle(titleNotificationEditText.getText().toString());
        myNotification.setDescription(descriptionNotificationEditText.getText().toString());
        String cityState=city+"_"+state;
        cityState=cityState.replace(' ','_');
        myNotification.setCityState(cityState);
        myNotification.setTimeDate(CommonData.getCurrentTime()+"  "+CommonData.getTodayDate());
        GlobalStateApplication.notificationsDatabaseReference.child(cityState).push().setValue(myNotification).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    ProgressUtils.cancelKprogressDialog();
                    Toast.makeText(context, "Notification Successfully Created", Toast.LENGTH_SHORT).show();
                    dismiss();
                }else{
                    ProgressUtils.cancelKprogressDialog();
                    Toast.makeText(context, "Unsuccessful "+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean allDetailsValid() {

        if (titleNotificationEditText.getText().toString().trim().length() == 0) {
            titleNotificationEditText.setError("Please enter the title name");
            titleNotificationEditText.requestFocus();
            return false;
        }
        if (descriptionNotificationEditText.getText().toString().trim().length() == 0) {
            descriptionNotificationEditText.setError("Please enter the description");
            descriptionNotificationEditText.requestFocus();
            return false;
        }
        if (stateNotificationSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(context, "Please first choose the state name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (cityNotificationSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(context, "Please first choose the city name", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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

    public static AddNotificationDialogFragment newInstance() {
        Bundle args = new Bundle();
        AddNotificationDialogFragment fragment = new AddNotificationDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position!=0) {
            switch (parent.getId()){
                case R.id.stateNotificationSpinner:
                    cityArrayList= new ArrayList<>();
                    state = (String) parent.getItemAtPosition(position);
                    Log.i("choosen state",state);
                    cityArrayList = stateAndCityHashMap.get(state);
                    if (cityArrayList != null) {
                        cityArrayList.add(0, "Select city");
                        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item,
                                cityArrayList);
                        cityNotificationSpinner.setAdapter(cityAdapter);
                    }
                    Toast.makeText(context, state + " selected", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.cityNotificationSpinner:
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
}
