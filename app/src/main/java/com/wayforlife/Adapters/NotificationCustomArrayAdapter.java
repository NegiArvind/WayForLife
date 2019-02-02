package com.wayforlife.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wayforlife.Models.MyNotification;
import com.wayforlife.R;

import java.util.ArrayList;

public class NotificationCustomArrayAdapter extends RecyclerView.Adapter<NotificationCustomArrayAdapter.NotificationViewHolder> {

    public ArrayList<MyNotification> myNotificationArrayList;
    public Context context;

    public NotificationCustomArrayAdapter(Context context,ArrayList<MyNotification> myNotificationArrayList) {
        this.myNotificationArrayList=myNotificationArrayList;
        this.context=context;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.notification_raw_layout,viewGroup,false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder notificationViewHolder, int i) {
        notificationViewHolder.notificationTitleTextView.setText(myNotificationArrayList.get(i).getTitle());
        notificationViewHolder.notificationDescriptionTextView.setText(myNotificationArrayList.get(i).getDescription());
        notificationViewHolder.notificationTimeDateTextView.setText(myNotificationArrayList.get(i).getTimeDate());
    }

    @Override
    public int getItemCount() {
        return myNotificationArrayList.size();
    }


    class NotificationViewHolder extends RecyclerView.ViewHolder {

        private TextView notificationTitleTextView;
        private TextView notificationDescriptionTextView;
        private TextView notificationTimeDateTextView;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationTitleTextView=itemView.findViewById(R.id.notificationTitleTextView);
            notificationDescriptionTextView=itemView.findViewById(R.id.notificationDescriptionTextView);
            notificationTimeDateTextView=itemView.findViewById(R.id.notificationTimeDateTextView);
        }
    }
}
