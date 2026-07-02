package com.malak.bitebridge.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.malak.bitebridge.R;

import java.util.List;

public class ChatAdapter extends
        RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    public static class ChatMessage {
        public String text;
        public boolean isUser;

        public ChatMessage(String text, boolean isUser) {
            this.text = text;
            this.isUser = isUser;
        }
    }

    private List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message,
                        parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ChatViewHolder holder, int position) {
        ChatMessage message = messages.get(position);

        if (message.isUser) {
            holder.tvUser.setVisibility(View.VISIBLE);
            holder.tvAi.setVisibility(View.GONE);
            holder.tvUser.setText(message.text);
        } else {
            holder.tvUser.setVisibility(View.GONE);
            holder.tvAi.setVisibility(View.VISIBLE);
            holder.tvAi.setText(message.text);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvUser, tvAi;

        ChatViewHolder(View itemView) {
            super(itemView);
            tvUser = itemView.findViewById(R.id.tv_user_message);
            tvAi = itemView.findViewById(R.id.tv_ai_message);
        }
    }
}