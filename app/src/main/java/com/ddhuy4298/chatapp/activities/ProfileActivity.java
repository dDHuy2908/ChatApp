package com.ddhuy4298.chatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.ddhuy4298.chatapp.R;
import com.ddhuy4298.chatapp.databinding.ActivityProfileBinding;
import com.ddhuy4298.chatapp.listeners.ProfileActivityListener;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity implements ProfileActivityListener {

    private ActivityProfileBinding binding;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private boolean clickable = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.defaultBackgroundColor));
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        binding.setListener(this);
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
