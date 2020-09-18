package com.ddhuy4298.chatapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ddhuy4298.chatapp.R;
import com.ddhuy4298.chatapp.listeners.UserListener;
import com.ddhuy4298.chatapp.models.User;
import com.ddhuy4298.chatapp.databinding.ItemUserBinding;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder>{

    private ArrayList<User> data;
    private LayoutInflater inflater;
    private UserListener listener;

    public UserAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public void setData(ArrayList<User> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setListener(UserListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_user, parent, false);
        return new UserHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        holder.binding.setItem(data.get(position));
        if (listener != null) {
            holder.binding.setListener(listener);
        }
        if (data.get(position).getStatus().equals("online")) {
            holder.binding.status.setVisibility(View.VISIBLE);
        }
        else {
            holder.binding.status.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class UserHolder extends RecyclerView.ViewHolder {
        private ItemUserBinding binding;

        public UserHolder(@NonNull ItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
