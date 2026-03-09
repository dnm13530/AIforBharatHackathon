package com.manasa.olympiadedgeai.network;

import com.google.gson.annotations.SerializedName;
import com.manasa.olympiadedgeai.ui.Message;
import java.util.ArrayList;
import java.util.List;

public class TutorApiModels {
    
    public static class HistoryItem {
        @SerializedName("text") public String text;
        @SerializedName("type") public int type;

        public HistoryItem(String text, int type) {
            this.text = text;
            this.type = type;
        }
    }

    public static class Request {
        @SerializedName("questionId") public String questionId;
        @SerializedName("questionText") public String questionText;
        @SerializedName("messages") public List<HistoryItem> history;

        public Request(String questionId, String questionText, List<Message> originalHistory) {
            this.questionId = questionId;
            this.questionText = questionText;
            this.history = new ArrayList<>();
            
            for (Message m : originalHistory) {
                // IMPORTANT: Only add to history if it's NOT a JSON string
                if (m.getText() != null && !m.getText().startsWith("{") && !m.getText().isEmpty()) {
                    this.history.add(new HistoryItem(m.getText(), m.getType()));
                }
            }
        }
    }

    public static class Response {
        @SerializedName("tutorResponse") public String tutorResponse;
        @SerializedName("isSuccess") public boolean isSuccess;
        @SerializedName("error") public String error;
        @SerializedName("body") public String body;
        @SerializedName("message") public String message;

        public String getEffectiveResponse() {
            if (tutorResponse != null && !tutorResponse.isEmpty()) return tutorResponse;
            if (message != null && !message.isEmpty()) return message;
            // Never return 'body' here, as it contains raw JSON
            return null;
        }
    }
}
