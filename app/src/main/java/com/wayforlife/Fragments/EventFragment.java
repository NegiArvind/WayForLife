package com.wayforlife.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.wayforlife.Activities.HomeActivity;
import com.wayforlife.Common.CommonData;
import com.wayforlife.GlobalStateApplication;
import com.wayforlife.Models.MyEvent;
import com.wayforlife.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class EventFragment extends Fragment {

    private CalendarView calendarView;
    private Calendar calendar;
    private Context context;
    private ArrayList<EventDay> eventDays;
    private FloatingActionButton addNewEventFloatingActionButton;
    private HashMap<String,String> myEventKeyHashMap; //key is date and value is eventKeyId
    private ChildEventListener eventChildEventListener;
    private HomeActivity homeActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.event_fragment_layout,container,false);
        context=getContext();
        calendarView=view.findViewById(R.id.calenderView);
        calendar=Calendar.getInstance();

        homeActivity= (HomeActivity) getActivity();
        addNewEventFloatingActionButton=view.findViewById(R.id.addNewEventFloatingActionButton);

        myEventKeyHashMap=new HashMap<>();
        eventDays=new ArrayList<>();

        getAllTheEventsAndSetIntoCalender();

        try {
            calendarView.setDate(calendar.getTime());

            calendarView.setOnDayClickListener(new OnDayClickListener() {
                @Override
                public void onDayClick(EventDay eventDay) {
                    String date=getDateFromTimeInMilliSec(eventDay.getCalendar());
                    //Checking if there is any event on this day. if yes then show the description of event in dialog fragment
                    if(myEventKeyHashMap.containsKey(date)) {
                        showDialogFragment(EventDescriptionDialogFragment.newInstance(myEventKeyHashMap.get(date)), getString(R.string.eventDesriptionDialogFragment));
                    }else{
                        Toast.makeText(context,"There is no any event on this day ", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }

        if(CommonData.isAdmin) {
            addNewEventFloatingActionButton.show();
//            addNewEventFloatingActionButton.setVisibility(View.VISIBLE);
            addNewEventFloatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogFragment(AddEventDialogFragment.newInstance(), "addEventDialogFragment");
                }
            });
        }

        return view;
    }

    private void showDialogFragment(DialogFragment dialogFragment, String tag) {
        FragmentTransaction fragmentTransaction;
        if (getFragmentManager() != null) {
            fragmentTransaction = getFragmentManager().beginTransaction();
            dialogFragment.show(fragmentTransaction,tag);
        }
    }

    private void getAllTheEventsAndSetIntoCalender() {
        eventChildEventListener=GlobalStateApplication.eventsDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MyEvent myEvent=dataSnapshot.getValue(MyEvent.class);
                if(myEvent!=null){
                    Log.i("my event",myEvent.getEventName());

                    Calendar newEventCalender=Calendar.getInstance();
                    newEventCalender.setTimeInMillis(myEvent.getEventDate());
                    String date=getDateFromTimeInMilliSec(newEventCalender); //it will return date in string

                    myEventKeyHashMap.put(date,dataSnapshot.getKey()); //making date as a key and eventKeyId as a value

                    EventDay eventDay=new EventDay(newEventCalender,R.drawable.event_available_image);
                    eventDays.add(eventDay);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                //if any event will be deleted then i need to reflect it into calender also.
                MyEvent myEvent=dataSnapshot.getValue(MyEvent.class);
                if(myEvent!=null) {
                    Calendar deleteCalender = Calendar.getInstance();
                    deleteCalender.setTimeInMillis(myEvent.getEventDate());
                    String date=getDateFromTimeInMilliSec(deleteCalender);
                    myEventKeyHashMap.remove(date);
                    for(EventDay eventDay:eventDays){
                        String tempDate=getDateFromTimeInMilliSec(eventDay.getCalendar());
                        //if "removed event date" is equal to the "event date present in arraylist" then remove that event from arraylist.
                        if(tempDate.equalsIgnoreCase(date)){
                            eventDays.remove(eventDay);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        GlobalStateApplication.eventsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                calendarView.setEvents(eventDays);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public String getDateFromTimeInMilliSec(Calendar calendar){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return simpleDateFormat.format(calendar.getTime());
    }

    public static EventFragment newInstance() {
        Bundle args = new Bundle();
        EventFragment fragment = new EventFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GlobalStateApplication.eventsDatabaseReference.removeEventListener(eventChildEventListener);
//        Toast.makeText(context,"Child event listener removed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        homeActivity.setActionBarTitle("Events");
    }
}
