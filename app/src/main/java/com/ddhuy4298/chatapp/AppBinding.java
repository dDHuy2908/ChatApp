package com.ddhuy4298.chatapp;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class AppBinding {

    @BindingAdapter("avatar")
    public static void setAvatar(CircleImageView im, String avatarUrl) {
        if (avatarUrl.equals("default")) {
            im.setImageResource(R.drawable.ic_avatar);
        }
        else {
            Glide.with(im.getContext()).load(avatarUrl).into(im);
        }
    }
}
