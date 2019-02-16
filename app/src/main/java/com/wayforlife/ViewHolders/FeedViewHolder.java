package com.wayforlife.ViewHolders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wayforlife.R;

public class FeedViewHolder extends RecyclerView.ViewHolder{

    public ImageView feedUserImageView,feedLikeImageView,feedCommentImageView;
    public TextView feedTitleTextView;
    public TextView feedDescriptionReadMoreTextView;
    public LinearLayout optionLinearLayout;
    public TextView optionOneTextView,optionTwoTextView,feedNoOfLikes,feedUserNameTextView,feedTimeDateTextView;

    public FeedViewHolder(@NonNull View itemView) {
        super(itemView);
        feedUserImageView=itemView.findViewById(R.id.feedUserImageView);
        feedLikeImageView=itemView.findViewById(R.id.feedLikeImageView);
        feedCommentImageView=itemView.findViewById(R.id.feedCommentImageView);
        feedTitleTextView=itemView.findViewById(R.id.feedTitleTextView);
        feedDescriptionReadMoreTextView=itemView.findViewById(R.id.feedDescriptionReadMoreTextView);
        optionLinearLayout=itemView.findViewById(R.id.optionLinearLayout);
        optionOneTextView=itemView.findViewById(R.id.optionOneTextView);
        optionTwoTextView=itemView.findViewById(R.id.optionTwoTextView);
        feedNoOfLikes=itemView.findViewById(R.id.feedNoOfLikes);
        feedUserNameTextView=itemView.findViewById(R.id.feedUserNameTextView);
        feedTimeDateTextView=itemView.findViewById(R.id.feedTimeDateTextView);
    }
}
