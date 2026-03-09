package com.manasa.olympiadedgeai.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.manasa.olympiadedgeai.data.QuestionWithStatus;

public class QuestionAdapter extends ListAdapter<QuestionWithStatus, QuestionAdapter.QuestionViewHolder> {

    private OnItemClickListener listener;

    public QuestionAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<QuestionWithStatus> DIFF_CALLBACK = new DiffUtil.ItemCallback<QuestionWithStatus>() {
        @Override
        public boolean areItemsTheSame(@NonNull QuestionWithStatus oldItem, @NonNull QuestionWithStatus newItem) {
            return oldItem.question.id == newItem.question.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull QuestionWithStatus oldItem, @NonNull QuestionWithStatus newItem) {
            return oldItem.isSolved() == newItem.isSolved() &&
                    oldItem.question.topic.equals(newItem.question.topic) &&
                    oldItem.question.difficulty.equals(newItem.question.difficulty);
        }
    };

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new QuestionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        QuestionWithStatus current = getItem(position);
        holder.textViewTitle.setText(current.question.topic);
        
        String status = current.question.difficulty;
        if (current.isSolved()) {
            status += " - SOLVED ✓";
            holder.textViewTitle.setTextColor(Color.parseColor("#4CAF50")); // Green
        } else {
            holder.textViewTitle.setTextColor(Color.BLACK);
        }
        
        holder.textViewSubtitle.setText(status);
    }

    class QuestionViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewSubtitle;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(android.R.id.text1);
            textViewSubtitle = itemView.findViewById(android.R.id.text2);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(QuestionWithStatus questionWithStatus);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
