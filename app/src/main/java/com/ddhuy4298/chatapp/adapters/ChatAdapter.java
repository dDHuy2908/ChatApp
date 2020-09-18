package com.ddhuy4298.chatapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.ddhuy4298.chatapp.R;
import com.ddhuy4298.chatapp.databinding.ItemChatBinding;
import com.ddhuy4298.chatapp.listeners.UserListener;
import com.ddhuy4298.chatapp.models.Message;
import com.ddhuy4298.chatapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {

    private ArrayList<User> data;
    private LayoutInflater inflater;
    private UserListener listener;
    private String lastedMessage;

    public ChatAdapter(LayoutInflater inflater) {
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
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChatBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_chat, parent, false);
        return new ChatHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        holder.binding.setItem(data.get(position));
        showLastedMessage(data.get(position).getId(), holder);
        holder.binding.tvLatestMessage.setText(lastedMessage);
        if (listener != null) {
            holder.binding.setListener(listener);
        }
        if (data.get(position).getStatus().equals("online")) {
            holder.binding.status.setVisibility(View.VISIBLE);
        } else {
            holder.binding.status.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class ChatHolder extends RecyclerView.ViewHolder {
        private ItemChatBinding binding;

        public ChatHolder(@NonNull ItemChatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private void showLastedMessage(final String receiverId, final ChatHolder holder) {
        final String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Messages");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (message.getSender().equals(currentUserId) && message.getReceiver().equals(receiverId)
                            || message.getSender().equals(receiverId) && message.getReceiver().equals(currentUserId)) {
                        lastedMessage = message.getMessage();
                        if (message.getSender().equals(currentUserId)) {
                            holder.binding.tvLatestMessage.setText("You: " + lastedMessage);
                            holder.binding.tvTime.setText(checkTime(message.getId()));
                        } else if (message.getSender().equals(receiverId)) {
//                            if (message.isSeen()) {
//                                holder.binding.tvLatestMessage.setTypeface(Typeface.DEFAULT);
//                            } else {
//                                holder.binding.tvLatestMessage.setTypeface(Typeface.DEFAULT_BOLD);
//                            }
                            holder.binding.tvLatestMessage.setText(lastedMessage);
                            holder.binding.tvTime.setText(checkTime(message.getId()));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String checkTime(long time) {
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM");
        String lastedTime = dateFormat.format(time);
        String lastedTime1 = dateFormat1.format(time);
        String currentTime = dateFormat.format(System.currentTimeMillis());
        long diff = 0;
        try {
            Date date1 = dateFormat.parse(lastedTime);
            Date date2 = dateFormat.parse(currentTime);
            diff = date2.getTime() - date1.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (diff == 86400000) {
            return "1 day";
        } else if (diff > 86400000) {
            return lastedTime1;
        } else {
            return hourFormat.format(time);
        }
    }
}
