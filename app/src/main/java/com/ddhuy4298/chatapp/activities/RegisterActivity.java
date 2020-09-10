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

import com.ddhuy4298.chatapp.R;
import com.ddhuy4298.chatapp.listeners.RegisterActivityListener;
import com.ddhuy4298.chatapp.models.User;
import com.ddhuy4298.chatapp.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements RegisterActivityListener {

    private ActivityRegisterBinding binding;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        binding.setListener(this);
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
    public void onRegisterClick() {
        final String email = binding.edtEmail.getText().toString();
        final String password = binding.edtPassword.getText().toString();
        final String name = binding.edtName.getText().toString();
        final String phone = binding.edtPhoneNumber.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
            Snackbar.make(binding.layoutRegister, "Empty info", Snackbar.LENGTH_SHORT).show();
        } else if (password.length() < 6) {
            Snackbar.make(binding.layoutRegister, "Password requires at least 6 characters", Snackbar.LENGTH_SHORT).show();
        } else {
            register(email, password, name, phone);
        }
    }

    private void register(final String email, final String password, final String name, final String phone) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        String userId = firebaseAuth.getCurrentUser().getUid();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                        User user = new User();
                        user.setEmail(email);
                        user.setName(name);
                        user.setPassword(password);
                        user.setPhone(phone);
                        user.setId(userId);
                        user.setAvatar("default");
                        reference.setValue(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        intent.putExtra("email", email);
                                        intent.putExtra("password", password);
                                        setResult(RESULT_OK, intent);
                                        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(RegisterActivity.this, "Add user data failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "Register failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackClick() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
        finish();
    }
}
