package com.ddhuy4298.chatapp.utils;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.ddhuy4298.chatapp.R;

import java.text.SimpleDateFormat;

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

    @BindingAdapter("time")
    public static void setTime(TextView tv, long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        tv.setText(format.format(time));
    }

    @BindingAdapter("date")
    public static void setDate(TextView tv, long time) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        tv.setText(format.format(time));
    }
}
