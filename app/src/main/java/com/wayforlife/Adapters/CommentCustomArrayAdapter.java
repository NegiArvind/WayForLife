package com.wayforlife.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wayforlife.GlobalStateApplication;
import com.wayforlife.Models.Comment;
import com.wayforlife.R;

import java.util.ArrayList;
import java.util.Objects;

//This is an recycler view array adapter which is used to set the comments on recyclerView
public class CommentCustomArrayAdapter extends RecyclerView.Adapter<CommentCustomArrayAdapter.CommentViewHolder>{

    private ArrayList<Comment> commentArrayList;
    private Context context;

    public CommentCustomArrayAdapter(Context context,ArrayList<Comment> commentArrayList) {
        this.context=context;
        this.commentArrayList=commentArrayList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.comment_raw_layout,viewGroup,false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder commentViewHolder, int i) {
        //Binding comments on viewholder
        String imageUrl=Objects.requireNonNull(GlobalStateApplication.usersHashMap.get(commentArrayList.get(i).getUserId())).getImageUrl();
        if(imageUrl!=null){
            Picasso.with(context).load(imageUrl).into(commentViewHolder.commentUserImageView);
        }else{
            commentViewHolder.commentUserImageView.setImageResource(R.drawable.person_image);
        }
        commentViewHolder.timeDateCommentTextView.setText(commentArrayList.get(i).getTimeDate());
        commentViewHolder.commentorNameTextView.setText(GlobalStateApplication.usersHashMap.get(commentArrayList.get(i).getUserId()).getFirstName());
        commentViewHolder.commentTextView.setText(commentArrayList.get(i).getCommentedMessage());
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

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentUserImageView=itemView.findViewById(R.id.commentUserImageView);
            commentTextView=itemView.findViewById(R.id.commentTextView);
            commentorNameTextView=itemView.findViewById(R.id.commentorNameTextView);
            timeDateCommentTextView=itemView.findViewById(R.id.timeDateCommentTextView);
        }
    }
}
