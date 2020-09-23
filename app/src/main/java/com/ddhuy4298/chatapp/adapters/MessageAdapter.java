package com.ddhuy4298.chatapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ddhuy4298.chatapp.R;
import com.ddhuy4298.chatapp.databinding.ItemMessageLeftBinding;
import com.ddhuy4298.chatapp.databinding.ItemMessageRightBinding;
import com.ddhuy4298.chatapp.listeners.ChatActivityListener;
import com.ddhuy4298.chatapp.models.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {

    private static final int MESSAGE_SENDER = 0;
    private static final int MESSAGE_RECEIVER = 1;
    private ArrayList<Message> data;
    private LayoutInflater inflater;
    private ChatActivityListener listener;
    private String receiverAvatar;

    public MessageAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public void setReceiverAvatar(String receiverAvatar) {
        this.receiverAvatar = receiverAvatar;
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
                return new MessageHolder((ItemMessageRightBinding) binding);
            case MESSAGE_RECEIVER:
                binding = DataBindingUtil.inflate(inflater, R.layout.item_message_left, parent, false);
                return new MessageHolder((ItemMessageLeftBinding) binding);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case MESSAGE_SENDER:
                holder.rightBinding.setItem(data.get(position));
                holder.rightBinding.setListener(listener);
                if (position == 0) {
                    holder.rightBinding.tvTime.setVisibility(View.GONE);
                    holder.rightBinding.tvDate.setVisibility(View.VISIBLE);
                } else {
                    if (checkTime(data.get(position).getId(), data.get(position - 1).getId()) > 300000) {
                        holder.rightBinding.tvTime.setVisibility(View.VISIBLE);
                        holder.rightBinding.tvDate.setVisibility(View.GONE);
                    } else if (checkTime(data.get(position).getId(), data.get(position - 1).getId()) < 300000) {
                        holder.rightBinding.tvTime.setVisibility(View.GONE);
                        holder.rightBinding.tvDate.setVisibility(View.GONE);
                    }
                }
//                else if (checkDate(data.get(position).getId())) {
//                    if (checkTime(data.get(position).getId(), data.get(position - 1).getId()) > 300000) {
//                        holder.rightBinding.tvTime.setVisibility(View.VISIBLE);
//                        holder.rightBinding.tvDate.setVisibility(View.GONE);
//                    } else if (checkTime(data.get(position).getId(), data.get(position - 1).getId()) < 300000) {
//                        holder.rightBinding.tvTime.setVisibility(View.GONE);
//                        holder.rightBinding.tvDate.setVisibility(View.GONE);
//                    }
//                } else if (!checkDate(data.get(position).getId())){
//                    holder.rightBinding.tvTime.setVisibility(View.GONE);
//                    holder.rightBinding.tvDate.setVisibility(View.GONE);
//                }
                if (position == data.size() - 1 && data.size() > 0) {
                    holder.rightBinding.tvSeen.setVisibility(View.VISIBLE);
                    if (data.get(position).isSeen()) {
                        holder.rightBinding.tvSeen.setText("Seen");
                    } else {
                        holder.rightBinding.tvSeen.setText("Delivered");
                    }
                } else {
                    holder.rightBinding.tvSeen.setVisibility(View.GONE);
                }
                break;
            case MESSAGE_RECEIVER:
                holder.leftBinding.setItem(data.get(position));
                holder.leftBinding.setListener(listener);
                if (data.size() == 1) {
                    if (receiverAvatar.equals("default")) {
                        holder.leftBinding.avatar.setImageResource(R.drawable.ic_avatar);
                    } else {
                        Glide.with(holder.leftBinding.avatar).load(receiverAvatar).into(holder.leftBinding.avatar);
                    }
                } else if (position == data.size() - 1) {
                    if (receiverAvatar.equals("default")) {
                        holder.leftBinding.avatar.setImageResource(R.drawable.ic_avatar);
                    } else {
                        Glide.with(holder.leftBinding.avatar).load(receiverAvatar).into(holder.leftBinding.avatar);
                    }
                } else if (position < data.size() - 1 && position > 0 && data.get(position).getSender().equals(data.get(position + 1).getSender())) {
                    holder.leftBinding.avatar.setVisibility(View.INVISIBLE);
                } else if (position < data.size() - 1 && position > 0 && !data.get(position).getSender().equals(data.get(position + 1).getSender())) {
                    if (receiverAvatar.equals("default")) {
                        holder.leftBinding.avatar.setImageResource(R.drawable.ic_avatar);
                    } else {
                        Glide.with(holder.leftBinding.avatar).load(receiverAvatar).into(holder.leftBinding.avatar);
                    }
                }
                if (position == 0) {
                    holder.leftBinding.tvTime.setVisibility(View.GONE);
                    holder.leftBinding.tvDate.setVisibility(View.VISIBLE);
                } else {
                    if (checkTime(data.get(position).getId(), data.get(position - 1).getId()) > 300000) {
                        holder.leftBinding.tvTime.setVisibility(View.VISIBLE);
                        holder.leftBinding.tvDate.setVisibility(View.GONE);
                    } else if (checkTime(data.get(position).getId(), data.get(position - 1).getId()) < 300000) {
                        holder.leftBinding.tvTime.setVisibility(View.GONE);
                        holder.leftBinding.tvDate.setVisibility(View.GONE);
                    }
                }
//                else if (checkDate(data.get(position).getId())) {
//                    if (checkTime(data.get(position).getId(), data.get(position - 1).getId()) > 300000) {
//                        holder.leftBinding.tvTime.setVisibility(View.VISIBLE);
//                        holder.leftBinding.tvDate.setVisibility(View.GONE);
//                    } else if (checkTime(data.get(position).getId(), data.get(position - 1).getId()) < 300000) {
//                        holder.leftBinding.tvTime.setVisibility(View.GONE);
//                        holder.leftBinding.tvDate.setVisibility(View.GONE);
//                    }
//                } else if (!checkDate(data.get(position).getId())) {
//                    holder.leftBinding.tvTime.setVisibility(View.GONE);
//                    holder.leftBinding.tvDate.setVisibility(View.GONE);
//                }
                break;
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
        } else {
            return MESSAGE_RECEIVER;
        }
    }

    public class MessageHolder extends RecyclerView.ViewHolder {

        private ItemMessageLeftBinding leftBinding;
        private ItemMessageRightBinding rightBinding;

        public MessageHolder(@NonNull final ItemMessageLeftBinding binding) {
            super(binding.getRoot());
            this.leftBinding = binding;

            leftBinding.tvReceivedMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (leftBinding.tvTime.getVisibility() == View.GONE) {
                        leftBinding.tvTime.setVisibility(View.VISIBLE);
                    } else if (leftBinding.tvTime.getVisibility() == View.VISIBLE) {
                        leftBinding.tvTime.setVisibility(View.GONE);
                    }
                }
            });

            leftBinding.tvReceivedMsg.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(v.getContext(), data.get(getAdapterPosition()).getMessage(), Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

        public MessageHolder(@NonNull ItemMessageRightBinding binding) {
            super(binding.getRoot());
            this.rightBinding = binding;

            rightBinding.tvMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rightBinding.tvTime.getVisibility() == View.GONE) {
                        rightBinding.tvTime.setVisibility(View.VISIBLE);
                    } else if (rightBinding.tvTime.getVisibility() == View.VISIBLE) {
                        rightBinding.tvTime.setVisibility(View.GONE);
                    }
                }
            });
            rightBinding.tvMessage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(v.getContext(), data.get(getAdapterPosition()).getMessage(), Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }

    private long checkTime(long time1, long time2) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String time1String = format.format(time1);
        String time2String = format.format(time2);
        long diff = 0;
        try {
            Date date1 = format.parse(time1String);
            Date date2 = format.parse(time2String);
            diff = date1.getTime() - date2.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diff;
    }

    private boolean checkDate(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String lastedTime = dateFormat.format(time);
        String currentTime = dateFormat.format(System.currentTimeMillis());
        long diff = 0;
        try {
            Date date1 = dateFormat.parse(lastedTime);
            Date date2 = dateFormat.parse(currentTime);
            diff = date2.getTime() - date1.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diff == 0;
    }
}
