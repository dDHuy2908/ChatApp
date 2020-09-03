package com.ddhuy4298.chatapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.ddhuy4298.chatapp.listeners.LoginActivityListener;
import com.ddhuy4298.chatapp.R;
import com.ddhuy4298.chatapp.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements LoginActivityListener {

    public static final int REQUEST_CODE = 1;
    private ActivityLoginBinding binding;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.login_bk_color));
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setListener(this);

        if (firebaseAuth.getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            startActivity(intent);
            finish();
        }
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
    public void onLoginClick() {
        String email = binding.edtEmail.getText().toString();
        String password = binding.edtPassword.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Empty info", Toast.LENGTH_LONG).show();
        } else {
            login(email, password);
        }
    }

    private void login(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
//                        DatabaseReference reference = FirebaseDatabase.getInstance()
//                                .getReference("Users").child(firebaseAuth.getCurrentUser().getUid());
//                        reference.child("token").setValue(FirebaseInstanceId.getInstance().getToken());

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(binding.layoutLogin, "Login failed", Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRegisterClick() {
        Intent intent = new Intent(this, RegisterActivity.class);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String email = data.getStringExtra("email");
                String password = data.getStringExtra("password");
                binding.edtEmail.setText(email);
                binding.edtPassword.setText(password);
            }
            else if (resultCode == RESULT_CANCELED) {
                Snackbar.make(binding.layoutLogin, "Canceled", Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}
