package com.ddhuy4298.chatapp.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.ddhuy4298.chatapp.R;
import com.ddhuy4298.chatapp.databinding.ItemMessageLeftBinding;
import com.ddhuy4298.chatapp.databinding.ItemMessageRightBinding;
import com.ddhuy4298.chatapp.listeners.ChatActivityListener;
import com.ddhuy4298.chatapp.models.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {

    private static final int MESSAGE_SENDER = 0;
    private static final int MESSAGE_RECEIVER = 1;
    private ArrayList<Message> data;
    private LayoutInflater inflater;
    private ChatActivityListener listener;

    public MessageAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public void setData(ArrayList<Message> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setListener(ChatActivityListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding binding;
        switch (viewType) {
            case MESSAGE_SENDER:
                binding = DataBindingUtil.inflate(inflater, R.layout.item_message_right, parent, false);
                return new MessageHolder((ItemMessageRightBinding)binding);
            case MESSAGE_RECEIVER:
                binding = DataBindingUtil.inflate(inflater, R.layout.item_message_left, parent, false);
                return new MessageHolder((ItemMessageLeftBinding)binding);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case MESSAGE_SENDER:
                holder.rightBinding.setItem(data.get(position));
                holder.rightBinding.setListener(listener);
                break;
            case MESSAGE_RECEIVER:
                holder.leftBinding.setItem(data.get(position));
                holder.leftBinding.setListener(listener);
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public int getItemViewType(int position) {
        String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (data.get(position).getSender().equals(senderId)) {
            return MESSAGE_SENDER;
        }
        else  {
            return MESSAGE_RECEIVER;
        }
    }

    public class MessageHolder extends RecyclerView.ViewHolder {

        private ItemMessageLeftBinding leftBinding;
        private ItemMessageRightBinding rightBinding;

        public MessageHolder(@NonNull ItemMessageLeftBinding binding) {
            super(binding.getRoot());
            this.leftBinding = binding;
        }

        public MessageHolder(@NonNull ItemMessageRightBinding binding) {
            super(binding.getRoot());
            this.rightBinding = binding;
        }
    }
}
