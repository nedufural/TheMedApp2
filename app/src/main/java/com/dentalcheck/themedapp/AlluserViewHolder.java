package com.dentalcheck.themedapp;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.dentalcheck.themedapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AlluserViewHolder extends RecyclerView.ViewHolder {
    public View mView;
    CircleImageView circleImageView;
    public AlluserViewHolder(View view) {
        super(view);
        mView=view;
    }

    public void setUser_name(String Name){
      TextView setUserName = mView.findViewById(R.id.textView_name);
      setUserName.setText(Name);
    }
    public void setUser_image(final Context context,final String Image){
         circleImageView  = mView.findViewById(R.id.imageView_profile_picture);

        Picasso.with(context).load(Image)
                .networkPolicy(NetworkPolicy.OFFLINE).into(circleImageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(context).load(Image).placeholder(R.drawable.ic_person_black_24dp).into(circleImageView);
            }
        });

    }
    public void setuser_status(String Status){
        TextView setUserImage = mView.findViewById(R.id.textView_status);
        setUserImage.setText(Status);
    }

}
