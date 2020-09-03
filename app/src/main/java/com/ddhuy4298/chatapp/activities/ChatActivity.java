package com.ddhuy4298.chatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.ddhuy4298.chatapp.R;
import com.ddhuy4298.chatapp.adapters.MessageAdapter;
import com.ddhuy4298.chatapp.listeners.ChatActivityListener;
import com.ddhuy4298.chatapp.models.Message;
import com.ddhuy4298.chatapp.models.User;
import com.ddhuy4298.chatapp.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity implements ChatActivityListener {

    private ActivityChatBinding binding;
    private MessageAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);

        setupToolbar();

        setupMessageList();
    }

    private void setupMessageList() {
        binding.setListener(this);
        adapter = new MessageAdapter(getLayoutInflater());
        binding.rvMessage.setAdapter(adapter);
        final String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Intent intent = getIntent();
        final String receiverId = intent.getStringExtra("userId");
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
        if (content.isEmpty()) {
            return;
        }
        Message message = new Message();
        message.setSender(senderId);
        message.setReceiver(receiverId);
        message.setMessage(content);
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

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users").child(senderId);
        userReference.child("lastedMessageTime").setValue(System.currentTimeMillis());

        final DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference("Chatted")
                .child(senderId)
                .child(receiverId);
        chatReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatReference.child("id").setValue(receiverId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        final DatabaseReference chatReference1 = FirebaseDatabase.getInstance().getReference("Chatted")
                .child(receiverId)
                .child(senderId);
        chatReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatReference1.child("id").setValue(senderId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onMessageClick(Message message) {
        Snackbar.make(binding.layoutChat, "Clicked", Snackbar.LENGTH_SHORT).show();
    }
}