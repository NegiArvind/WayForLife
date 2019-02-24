package com.wayforlife.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.wayforlife.GlobalStateApplication;
import com.wayforlife.Models.MyEvent;
import com.wayforlife.R;
import com.wayforlife.Utils.ProgressUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

//This fragment is used to add new event
public class AddEventDialogFragment extends DialogFragment implements View.OnClickListener {

    private EditText dateEventEditText;
    private EditText eventNameEditText;
    private EditText eventDescriptionEditText;
    private Button addEventButton;
    private Toolbar eventToolbar;
    private Context context;
    private long time;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,R.style.AppTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.add_event_dialog_fragment_layout,container,false);
        context=getContext();
        eventToolbar=view.findViewById(R.id.eventToolbar);
        eventToolbar.setTitleTextColor(getResources().getColor(R.color.md_white_1000));
        eventToolbar.setTitle("Add new event");
        eventToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        dateEventEditText=view.findViewById(R.id.dateEventEditText);
        eventNameEditText=view.findViewById(R.id.eventNameEditText);
        eventDescriptionEditText=view.findViewById(R.id.eventDescriptionEditText);
        addEventButton=view.findViewById(R.id.addEventButton);
        dateEventEditText.setFocusable(false);

        dateEventEditText.setOnClickListener(this);
        addEventButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.addEventButton:
                if(allDetailsValid()){
                    ProgressUtils.showKProgressDialog(context,"Adding..");
                    storeEventIntoFirebaseDatabase();
                }
                break;
            case R.id.dateEventEditText:
                showDatePicker();
                break;
        }
    }

    //saving event into firebase database
    private void storeEventIntoFirebaseDatabase() {
        MyEvent myEvent=new MyEvent();
        myEvent.setEventDate(time);
        myEvent.setEventName(eventNameEditText.getText().toString());
        myEvent.setEventDescription(eventDescriptionEditText.getText().toString());
        GlobalStateApplication.eventsDatabaseReference.push().setValue(myEvent).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    ProgressUtils.cancelKprogressDialog();
                    Toast.makeText(context, "Event Successfully added", Toast.LENGTH_SHORT).show();
                    dismiss();
                }else{
                    Toast.makeText(context, "Unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean allDetailsValid() {

        if(eventNameEditText.getText().toString().trim().length()==0){
            eventNameEditText.setError("Please enter the event name");
            eventNameEditText.requestFocus();
            return false;
        }
        if(dateEventEditText.getText().toString().trim().length()==0){
            dateEventEditText.setError("Please choose the date");
            dateEventEditText.requestFocus();
            return false;
        }
        if(eventDescriptionEditText.getText().toString().trim().length()==0){
            eventDescriptionEditText.setError("Please enter the description");
            eventDescriptionEditText.requestFocus();
            return false;
        }
        return true;
    }

    private void showDatePicker() {// Get Current Date

        final int year,month,day;
        final Calendar calendar=Calendar.getInstance();
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
                        dateEventEditText.setText(simpleDateFormat.format(calendar.getTime()));
//                        dateEventEditText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        time=calendar.getTimeInMillis();
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    public static AddEventDialogFragment newInstance() {
        Bundle args = new Bundle();
        AddEventDialogFragment fragment = new AddEventDialogFragment();
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

}
