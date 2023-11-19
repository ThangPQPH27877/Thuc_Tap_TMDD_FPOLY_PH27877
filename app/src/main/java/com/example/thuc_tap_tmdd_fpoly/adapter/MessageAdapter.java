package com.example.thuc_tap_tmdd_fpoly.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thuc_tap_tmdd_fpoly.R;
import com.example.thuc_tap_tmdd_fpoly.model.Message;

import java.util.ArrayList;
import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_BOT_MESSAGE = 1;
    private static final int VIEW_TYPE_USER_MESSAGE = 2;

    private List<String> messageList;

    public MessageAdapter() {
        messageList = new ArrayList<>();
    }


    public void addBotMessage(String message) {
        messageList.add(message);
        notifyItemInserted(messageList.size() - 1);
    }

    public void addUserMessage(String message) {
        messageList.add(message);
        notifyItemInserted(messageList.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        String message = messageList.get(position);
        if (message.startsWith("Bot:")) {
            return VIEW_TYPE_BOT_MESSAGE;
        } else {
            return VIEW_TYPE_USER_MESSAGE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView;
        if (viewType == VIEW_TYPE_BOT_MESSAGE) {
            itemView = inflater.inflate(R.layout.item_message_bot, parent, false);
            return new BotMessageViewHolder(itemView);
        } else {
            itemView = inflater.inflate(R.layout.item_message_user, parent, false);
            return new UserMessageViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String message = messageList.get(position);
        if (holder instanceof BotMessageViewHolder) {
            ((BotMessageViewHolder) holder).bind(message);
        } else if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void addMessage(Message message) {
        messageList.add(message.getContent());
        notifyItemInserted(messageList.size() - 1);
    }

    private static class BotMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMessageBot;

        public BotMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessageBot = itemView.findViewById(R.id.tv_messageBot);
        }

        public void bind(String message) {
            tvMessageBot.setText(message);
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) tvMessageBot.getLayoutParams();
            layoutParams.horizontalBias = 0; // Đặt canh trái cho tin nhắn từ bot
            tvMessageBot.setLayoutParams(layoutParams);
        }
    }

    private static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMessageUser;

        public UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessageUser = itemView.findViewById(R.id.tv_messageUser);
        }

        public void bind(String message) {
            tvMessageUser.setText(message);
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) tvMessageUser.getLayoutParams();
            layoutParams.horizontalBias = 1; // Đặt canh phải cho tin nhắn từ người dùng
            tvMessageUser.setLayoutParams(layoutParams);
        }
    }
}