package com.ddhuy4298.chatapp.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.ddhuy4298.chatapp.R;
import com.ddhuy4298.chatapp.adapters.MessageAdapter;
import com.ddhuy4298.chatapp.api.ApiBuilder;
import com.ddhuy4298.chatapp.databinding.ActivityChatBinding;
import com.ddhuy4298.chatapp.listeners.ChatActivityListener;
import com.ddhuy4298.chatapp.models.Data;
import com.ddhuy4298.chatapp.models.FCM;
import com.ddhuy4298.chatapp.models.Message;
import com.ddhuy4298.chatapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements ChatActivityListener {

    private ActivityChatBinding binding;
    private MessageAdapter adapter;
    private ValueEventListener eventListener;
    private final String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private Intent intent;
    private String receiverId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(429899);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.defaultBackgroundColor));
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        intent = getIntent();
        String receiverId = intent.getStringExtra("userId");
        Log.e("ChatActivity", receiverId);

        /**
         * Only show send button when editText.length() >= 1
         * **/
//        binding.edtMessage.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.toString().length()<1){
//                    binding.layoutSend.setVisibility(View.GONE);
//                }
//                else {
//                    binding.layoutSend.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

        setupMessageList(receiverId);

        setupToolbar();

        seenMessage();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    private void setupMessageList(String receiverId) {
        binding.setListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        binding.rvMessage.setLayoutManager(layoutManager);
        adapter = new MessageAdapter(getLayoutInflater());
        binding.rvMessage.setAdapter(adapter);
        DatabaseReference reference1 = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(receiverId)
                .child("avatar");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String avatar = snapshot.getValue(String.class);
                if (avatar.equals("default")) {
                    binding.avatar.setImageResource(R.drawable.ic_avatar);
                } else {
                    Glide.with(binding.avatar).load(avatar).into(binding.avatar);
                }
                adapter.setReceiverAvatar(avatar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Messages");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Message> data = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (message.getSender().equals(currentUserId) && message.getReceiver().equals(receiverId)
                            || message.getSender().equals(receiverId) && message.getReceiver().equals(currentUserId)) {
                        data.add(message);
                    }
                }
                adapter.setData(data);
                adapter.notifyDataSetChanged();
                if (binding.rvMessage.getAdapter().getItemCount() != 0) {
                    binding.rvMessage.smoothScrollToPosition(binding.rvMessage.getAdapter().getItemCount() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter.setListener(this);
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        binding.toolbar.setNavigationIcon(R.drawable.ic_back);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        String userId = intent.getStringExtra("userId");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                binding.tvName.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onSendClick() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Messages");
        Intent intent = getIntent();
        final String receiverId = intent.getStringExtra("userId");
        String content = binding.edtMessage.getText().toString();
        if (content.isEmpty() || content.trim().isEmpty()) {
            return;
        }
        final Message message = new Message();
        message.setSender(currentUserId);
        message.setReceiver(receiverId);
        message.setMessage(content);
        message.setSeen(false);
        reference.child(message.getId() + "").setValue(message)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        binding.edtMessage.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(binding.layoutChat, "Failed", Snackbar.LENGTH_SHORT).show();
                    }
                });

        final DatabaseReference chattedReference1 = FirebaseDatabase.getInstance().getReference("Chatted")
                .child(currentUserId)
                .child(receiverId);
        chattedReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chattedReference1.child("lastedMessageTime").setValue(message.getId());
                if (!dataSnapshot.exists()) {
                    chattedReference1.child("id").setValue(receiverId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        final DatabaseReference chattedReference2 = FirebaseDatabase.getInstance().getReference("Chatted")
                .child(receiverId)
                .child(currentUserId);
        chattedReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chattedReference2.child("lastedMessageTime").setValue(message.getId());
                if (!dataSnapshot.exists()) {
                    chattedReference2.child("id").setValue(currentUserId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final String[] userToken = {""};
        DatabaseReference tokenReference = FirebaseDatabase.getInstance()
                .getReference("Tokens")
                .child(receiverId);
        tokenReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userToken[0] = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(currentUserId);
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Data data = new Data();
                data.setTitle(user.getName());
                data.setBody(content);
                data.setSender(currentUserId);
                data.setReceiver(receiverId);
                FCM fcm = new FCM();
                fcm.setTo(userToken[0]);
                fcm.setData(data);

                ApiBuilder.getInstance()
                        .sendNotification(fcm)
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.code() == 200) {
                                    Toast.makeText(ChatActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ChatActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void seenMessage() {
        Intent intent = getIntent();
        final String receiverId = intent.getStringExtra("userId");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Messages");
        eventListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (message.getReceiver().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            && message.getSender().equals(receiverId)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("seen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void currentReceiver(String userId) {
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentReceiver", userId);
        editor.apply();
    }

    private void status(String status) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(currentUserId);
        reference.child("status").setValue(status);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        currentReceiver(receiverId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent intent = getIntent();
        final String receiverId = intent.getStringExtra("userId");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(receiverId);
        reference.removeEventListener(eventListener);
        status("offline");
        currentReceiver("none");
    }
}
