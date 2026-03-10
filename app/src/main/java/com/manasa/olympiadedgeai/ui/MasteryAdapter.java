package com.manasa.olympiadedgeai.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manasa.olympiadedgeai.R;
import com.manasa.olympiadedgeai.data.TopicMastery;

import java.util.ArrayList;
import java.util.List;

public class MasteryAdapter extends RecyclerView.Adapter<MasteryAdapter.MasteryViewHolder> {

    private List<TopicMastery> masteryList = new ArrayList<>();

    public void setMasteryList(List<TopicMastery> masteryList) {
        this.masteryList = masteryList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MasteryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topic_mastery, parent, false);
        return new MasteryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MasteryViewHolder holder, int position) {
        TopicMastery mastery = masteryList.get(position);
        holder.textViewTopic.setText(mastery.topic);
        holder.textViewProgress.setText(mastery.solvedCorrectly + " / " + mastery.totalQuestions);
        holder.progressBar.setProgress(mastery.getMasteryPercentage());
        holder.textViewPercentage.setText(mastery.getMasteryPercentage() + "%");
    }

    @Override
    public int getItemCount() {
        return masteryList.size();
    }

    static class MasteryViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTopic;
        TextView textViewProgress;
        ProgressBar progressBar;
        TextView textViewPercentage;

        public MasteryViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTopic = itemView.findViewById(R.id.textViewTopic);
            textViewProgress = itemView.findViewById(R.id.textViewProgress);
            progressBar = itemView.findViewById(R.id.progressBarMastery);
            textViewPercentage = itemView.findViewById(R.id.textViewPercentage);
        }
    }
}
