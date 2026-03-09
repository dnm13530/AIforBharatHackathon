package com.manasa.olympiadedgeai.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manasa.olympiadedgeai.R;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> messageList = new ArrayList<>();

    public void addMessage(Message message) {
        messageList.add(message);
        notifyItemInserted(messageList.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Message.TYPE_STUDENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_student, parent, false);
            return new StudentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_tutor, parent, false);
            return new TutorMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (holder instanceof StudentMessageViewHolder) {
            ((StudentMessageViewHolder) holder).textViewMessage.setText(message.getText());
        } else {
            ((TutorMessageViewHolder) holder).bind(message.getText());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class StudentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage;

        public StudentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
        }
    }

    static class TutorMessageViewHolder extends RecyclerView.ViewHolder {
        WebView webViewMessage;

        public TutorMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            webViewMessage = itemView.findViewById(R.id.webViewMessage);
            webViewMessage.getSettings().setJavaScriptEnabled(true);
            webViewMessage.setBackgroundColor(0); // Transparent to show bubble
        }

        public void bind(String text) {
            if (text == null) return;
            
            // Fixed color to #333333 because the bubble background is white
            String html = "<html><head>" +
                    "<script type=\"text/javascript\" async src=\"https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.7/MathJax.js?config=TeX-MML-AM_CHTML\"></script>" +
                    "<style>" +
                    "body { font-family: sans-serif; font-size: 15px; color: #333333; line-height: 1.5; margin: 0; padding: 8px; }" +
                    "</style>" +
                    "</head><body>" +
                    text.replace("\n", "<br/>") +
                    "</body></html>";

            webViewMessage.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
        }
    }
}
