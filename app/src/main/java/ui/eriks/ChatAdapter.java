package ui.eriks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import api.eriks.Message;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {

    private final LayoutInflater inflater;
    private final List<Message> messages;

    public ChatAdapter(Context context, List<Message> messages) {
        inflater = LayoutInflater.from(context);
        this.messages = messages;
    }

    public void addMessage(Message message) {
        messages.add(messages.size(), message);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0)
            return new ChatHolder(inflater.inflate(R.layout.chat_item_my, parent, false));
        return new ChatHolder(inflater.inflate(R.layout.chat_item_other, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        holder.bind(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isMyChat() ? 0 : 1;
    }

    static class ChatHolder extends RecyclerView.ViewHolder {
        private final TextView messageText;
        private final TextView userText;
        private final TextView timestamp;
        private final SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, h:mm a");

        public ChatHolder(View itemView) {
            super(itemView);
            timestamp = itemView.findViewById(R.id.txtTimestamp);
            messageText = itemView.findViewById(R.id.txtMessage);
            userText = itemView.findViewById(R.id.txtUsername);
        }

        public void bind(Message message) {
            messageText.setText(message.getMessage());
            if (!message.isMyChat())
                userText.setText(message.getEndpoint().getName());
            Date date = new Date();
            date.setTime(message.getTimestamp());
            timestamp.setText(sdf.format(date));
        }
    }
}
