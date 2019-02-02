package com.wayforlife.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.wayforlife.Common.CommonData;
import com.wayforlife.GlobalStateApplication;
import com.wayforlife.Models.MyEvent;
import com.wayforlife.R;
import com.wayforlife.Utils.ProgressUtils;

import java.util.Objects;

//This fragment will show the description of event
public class EventDescriptionDialogFragment extends DialogFragment {

    private String eventKeyId;
    private TextView eventNameTextView,eventDescriptionTextView;
    private Button eventRemoveButton;
    private Toolbar eventDescriptionToolbar;
    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,R.style.AppTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.event_description_dialog_fragment_layout,container,false);
        context=getContext();
        eventNameTextView=view.findViewById(R.id.eventNameTextView);
        eventDescriptionTextView=view.findViewById(R.id.eventDescriptionTextView);
        eventRemoveButton=view.findViewById(R.id.eventRemoveButton);
        eventDescriptionToolbar=view.findViewById(R.id.eventDescriptionToolbar);

        eventDescriptionToolbar.setTitle("About Event");
        eventDescriptionToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if(getArguments()!=null){
            //getting the eventKeyId from its parent fragment
            eventKeyId=getArguments().getString("eventKeyId");
            setDetails();
        }

        //If user is admin then he can remove the event.
        if(CommonData.isAdmin){
            eventRemoveButton.setVisibility(View.VISIBLE);
            eventRemoveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteAlertDialog();
                }
            });
        }
        return view;
    }

    private void showDeleteAlertDialog() {
        new AlertDialog.Builder(context)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete it?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ProgressUtils.showKProgressDialog(context,"Deleting..");
                        deleteEventFromFirebaseDatabase();
                    }
                })
                .setNegativeButton("No",null)
                .show();
    }

    private void deleteEventFromFirebaseDatabase() {
        GlobalStateApplication.eventsDatabaseReference.child(eventKeyId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    ProgressUtils.cancelKprogressDialog();
                    Toast.makeText(context, "Event Successfully deleted", Toast.LENGTH_SHORT).show();
                    dismiss();
                }else{
                    ProgressUtils.cancelKprogressDialog();
                    Toast.makeText(context, "Unsuccessful "+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setDetails() {
        GlobalStateApplication.eventsDatabaseReference.child(eventKeyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MyEvent myEvent=dataSnapshot.getValue(MyEvent.class);
                if(myEvent!=null) {
                    eventNameTextView.setText(myEvent.getEventName());
                    eventDescriptionTextView.setText(myEvent.getEventDescription());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static EventDescriptionDialogFragment newInstance(String eventKeyId) {
        Bundle args = new Bundle();
        args.putString("eventKeyId",eventKeyId);
        EventDescriptionDialogFragment fragment = new EventDescriptionDialogFragment();
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
