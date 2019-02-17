package com.wayforlife.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.wayforlife.Common.CommonData;
import com.wayforlife.GlobalStateApplication;
import com.wayforlife.Models.Comment;
import com.wayforlife.Models.User;
import com.wayforlife.R;

import java.util.ArrayList;
import java.util.Objects;

//This is an recycler view array adapter which is used to set the comments on recyclerView
public class CommentCustomArrayAdapter extends RecyclerView.Adapter<CommentCustomArrayAdapter.CommentViewHolder>{

    private ArrayList<Comment> commentArrayList;
    private Context context;
    private String currentPostKeyId;

    public CommentCustomArrayAdapter(Context context,ArrayList<Comment> commentArrayList,String currentPostKeyId) {
        this.context=context;
        this.commentArrayList=commentArrayList;
        this.currentPostKeyId=currentPostKeyId;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.comment_raw_layout,viewGroup,false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentViewHolder commentViewHolder, final int i) {
        //Binding comments on viewholder
        GlobalStateApplication.usersDatabaseReference.child(commentArrayList.get(i).getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User commentUser=dataSnapshot.getValue(User.class);
                if(commentUser!=null) {
                    String imageUrl = commentUser.getImageUrl();
                    commentViewHolder.commentUserImageViewProgressBar.setVisibility(View.VISIBLE);
                    if (imageUrl != null) {
                        Picasso.with(context).load(imageUrl).into(commentViewHolder.commentUserImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                if(commentViewHolder.commentUserImageViewProgressBar!=null) {
                                    commentViewHolder.commentUserImageViewProgressBar.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onError() {

                            }
                        });
                    } else {
                        commentViewHolder.commentUserImageView.setImageResource(R.drawable.person_image);
                        if(commentViewHolder.commentUserImageViewProgressBar!=null) {
                            commentViewHolder.commentUserImageViewProgressBar.setVisibility(View.GONE);
                        }
                    }
                    if(CommonData.isAdmin){
                        commentViewHolder.deleteCommentImageButton.setVisibility(View.VISIBLE);
                        commentViewHolder.deleteCommentImageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDeleteCommentAlertDialog(commentViewHolder);
                            }
                        });
                    }
                    commentViewHolder.timeDateCommentTextView.setText(commentArrayList.get(commentViewHolder.getAdapterPosition()).getTimeDate());
                    commentViewHolder.commentorNameTextView.setText(commentUser.getFirstName());
                    commentViewHolder.commentTextView.setText(commentArrayList.get(commentViewHolder.getAdapterPosition()).getCommentedMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showDeleteCommentAlertDialog(final CommentViewHolder commentViewHolder) {
        new AlertDialog.Builder(context)
                .setTitle("Delete")
                .setIcon(R.drawable.way_for_life_logo)
                .setMessage("Are you sure you want to delete this comment?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        commentArrayList.remove(commentViewHolder.getAdapterPosition());
                        GlobalStateApplication.feedsDatabaseReference.child(currentPostKeyId).child("commentArrayList").setValue(commentArrayList);
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No",null)
                .show();
    }

    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{

        private ImageView commentUserImageView;
        private TextView commentTextView;
        private TextView commentorNameTextView;
        private TextView timeDateCommentTextView;
        private ProgressBar commentUserImageViewProgressBar;
        private ImageButton deleteCommentImageButton;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentUserImageView=itemView.findViewById(R.id.commentUserImageView);
            commentTextView=itemView.findViewById(R.id.commentTextView);
            commentorNameTextView=itemView.findViewById(R.id.commentorNameTextView);
            timeDateCommentTextView=itemView.findViewById(R.id.timeDateCommentTextView);
            commentUserImageViewProgressBar=itemView.findViewById(R.id.commentUserImageViewProgressBar);
            deleteCommentImageButton=itemView.findViewById(R.id.deleteCommentImageButton);
        }


    }
}
