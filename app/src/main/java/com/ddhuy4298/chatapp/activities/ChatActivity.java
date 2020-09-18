package com.ddhuy4298.chatapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.ddhuy4298.chatapp.R;
import com.ddhuy4298.chatapp.adapters.MessageAdapter;
import com.ddhuy4298.chatapp.databinding.ActivityChatBinding;
import com.ddhuy4298.chatapp.listeners.ChatActivityListener;
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

public class ChatActivity extends AppCompatActivity implements ChatActivityListener {

    private ActivityChatBinding binding;
    private MessageAdapter adapter;
    private ValueEventListener eventListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.defaultBackgroundColor));
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);

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

        setupToolbar();

        setupMessageList();

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

    private void setupMessageList() {
        binding.setListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        binding.rvMessage.setLayoutManager(layoutManager);
        adapter = new MessageAdapter(getLayoutInflater());
        binding.rvMessage.setAdapter(adapter);
        final String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Intent intent = getIntent();
        final String receiverId = intent.getStringExtra("userId");
        String receiverAvatar = intent.getStringExtra("userAvatar");
        if (receiverAvatar.equals("default")) {
            binding.avatar.setImageResource(R.drawable.ic_avatar);
        } else {
            Glide.with(binding.avatar).load(receiverAvatar).into(binding.avatar);
        }
        adapter.setReceiverAvatar(receiverAvatar);
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

        Intent intent = getIntent();
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
        final String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String content = binding.edtMessage.getText().toString();
        if (content.isEmpty() || content.trim().isEmpty()) {
            return;
        }
        final Message message = new Message();
        message.setSender(senderId);
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

//        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users").child(senderId);
//        userReference.child("lastedMessageTime").setValue(System.currentTimeMillis());

        final DatabaseReference chattedReference1 = FirebaseDatabase.getInstance().getReference("Chatted")
                .child(senderId)
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
                .child(senderId);
        chattedReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chattedReference2.child("lastedMessageTime").setValue(message.getId());
                if (!dataSnapshot.exists()) {
                    chattedReference2.child("id").setValue(senderId);
                }
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
                        hashMap.put("isSeen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void status(String status) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent intent = getIntent();
        final String receiverId = intent.getStringExtra("userId");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(receiverId);
        reference.removeEventListener(eventListener);
        status("offline");
    }
}
