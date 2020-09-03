package com.ddhuy4298.chatapp.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.ddhuy4298.chatapp.R;
import com.ddhuy4298.chatapp.listeners.UserListener;
import com.ddhuy4298.chatapp.models.User;
import com.ddhuy4298.chatapp.databinding.ItemUserBinding;

import java.util.ArrayList;

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
