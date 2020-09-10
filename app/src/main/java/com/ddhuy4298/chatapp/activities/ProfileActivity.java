package com.ddhuy4298.chatapp.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.ddhuy4298.chatapp.R;
import com.ddhuy4298.chatapp.databinding.ActivityProfileBinding;
import com.ddhuy4298.chatapp.listeners.ProfileActivityListener;
import com.ddhuy4298.chatapp.models.User;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

public class ProfileActivity extends AppCompatActivity implements ProfileActivityListener {

    private ActivityProfileBinding binding;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private boolean clickable = true;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.defaultBackgroundColor));
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        binding.setListener(this);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user.getAvatar().equals("default")) {
                    binding.avatar.setImageResource(R.drawable.ic_avatar);
                }
                else {
                    Glide.with(binding.avatar).load(user.getAvatar()).into(binding.avatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onLogOutClick() {
        if (clickable) {
            firebaseAuth.signOut();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            binding.btnLogout.setEnabled(false);
            binding.btnLogout.setClickable(false);
        }
        clickable = false;
    }

    @Override
    public void onChangeAvatarClick() {
        Intent intent = new Intent(ProfileActivity.this, AvatarPreviewActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
