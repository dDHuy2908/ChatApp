package com.ddhuy4298.chatapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ddhuy4298.chatapp.R;
import com.ddhuy4298.chatapp.activities.ChatActivity;
import com.ddhuy4298.chatapp.activities.ProfileActivity;
import com.ddhuy4298.chatapp.adapters.UserAdapter;
import com.ddhuy4298.chatapp.databinding.FragmentUserBinding;
import com.ddhuy4298.chatapp.listeners.UserListener;
import com.ddhuy4298.chatapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserFragment extends BaseFragment<FragmentUserBinding> implements UserListener, View.OnClickListener {

    private UserAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupToolbar();

        showUsers();
    }

    private void showUsers() {
        adapter = new UserAdapter(getLayoutInflater());
        binding.rvUser.setAdapter(adapter);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<User> data = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (!user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        data.add(user);
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
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
        ((AppCompatActivity) getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        binding.toolbar.setNavigationIcon(null);
        ImageView imageView = binding.toolbar.findViewById(R.id.avatar);
        imageView.setOnClickListener(this);
    }

    @Override
    public void onUserClick(User user) {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra("userId", user.getId());
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        startActivity(intent);
    }
}
