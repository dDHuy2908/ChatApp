package com.ddhuy4298.chatapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ddhuy4298.chatapp.R;
import com.ddhuy4298.chatapp.activities.ChatActivity;
import com.ddhuy4298.chatapp.activities.ProfileActivity;
import com.ddhuy4298.chatapp.adapters.ChatAdapter;
import com.ddhuy4298.chatapp.databinding.FragmentChatBinding;
import com.ddhuy4298.chatapp.listeners.UserListener;
import com.ddhuy4298.chatapp.models.Chatted;
import com.ddhuy4298.chatapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class ChatFragment extends BaseFragment<FragmentChatBinding> implements UserListener, View.OnClickListener {

    private ChatAdapter adapter;
    private String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chat;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupToolbar();

        adapter = new ChatAdapter(getLayoutInflater());
        binding.rvChat.setAdapter(adapter);
        adapter.setListener(this);

        showChattedUsers();
    }

    private void showChattedUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chatted").child(currentUserId);
        Query query = reference.orderByChild("lastedMessageTime");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Chatted> chattedList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chatted chatted = snapshot.getValue(Chatted.class);
                    chattedList.add(chatted);
                }
                Collections.reverse(chattedList);
                getChattedList(chattedList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setupToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
        ((AppCompatActivity) getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        binding.toolbar.setNavigationIcon(null);
        final ImageView imageView = binding.toolbar.findViewById(R.id.avatar);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getActivity() == null) {
                    return;
                }
                User user = dataSnapshot.getValue(User.class);
                if (user.getAvatar().equals("default")) {
                    imageView.setImageResource(R.drawable.ic_avatar);
                } else {
                    Glide.with(getActivity().getApplicationContext()).load(user.getAvatar()).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        imageView.setOnClickListener(this);
    }

    private void getChattedList(final ArrayList<Chatted> chattedList) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<User> data = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    data.add(user);
                }
                ArrayList<User> data1 = new ArrayList<>();
                for (int i = 0; i < chattedList.size(); i++) {
                    for (int j = 0; j < data.size(); j++) {
                        if (chattedList.get(i).getId().equals(data.get(j).getId())) {
                            data1.add(data.get(j));
                        }
                    }
                }
                adapter.setData(data1);
                adapter.notifyDataSetChanged();
                if (data1.size() == 0) {
                    binding.tvNoMessage.setVisibility(View.VISIBLE);
                } else {
                    binding.tvNoMessage.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onUserClick(User receiver) {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra("userId", receiver.getId());
        intent.putExtra("userAvatar", receiver.getAvatar());
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        startActivity(intent);
    }
}
