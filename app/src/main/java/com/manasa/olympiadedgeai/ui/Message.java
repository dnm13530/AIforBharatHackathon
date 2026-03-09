package com.manasa.olympiadedgeai.ui;

import com.google.gson.annotations.SerializedName;

public class Message {
    public static final int TYPE_STUDENT = 0;
    public static final int TYPE_TUTOR = 1;

    @SerializedName("text")
    private String text;
    
    @SerializedName("type")
    private int type;

    public Message(String text, int type) {
        this.text = text;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public int getType() {
        return type;
    }
    
    // Helper for Bedrock/Lambda roles
    public String getRole() {
        return type == TYPE_STUDENT ? "user" : "assistant";
    }
}
