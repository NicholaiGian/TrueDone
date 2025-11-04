package com.example.truedone.ui.main;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.truedone.data.model.Task;
import com.example.truedone.databinding.ItemHistoryCardBinding;
import com.example.truedone.utils.TimeUtils;
import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<Task> tasks = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener { void onItemClick(Task task); }

    public HistoryAdapter(OnItemClickListener listener) { this.listener = listener; }

    public void submitList(List<Task> newTasks) {
        this.tasks = newTasks;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ItemHistoryCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(tasks.get(position));
    }

    @Override
    public int getItemCount() { return tasks.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemHistoryCardBinding binding;
        ViewHolder(ItemHistoryCardBinding b) { super(b.getRoot()); binding = b; }

        void bind(Task task) {
            binding.tvTitle.setText(task.taskTitle);
            if (task.timeTaken != null) {
                binding.tvTimeTaken.setText(TimeUtils.formatDuration(task.timeTaken));
            }
            // Use After image, fallback to Before
            String img = task.afterImagePath != null ? task.afterImagePath : task.beforeImagePath;
            Glide.with(itemView).load(img).centerCrop().into(binding.ivThumb);

            itemView.setOnClickListener(v -> listener.onItemClick(task));
        }
    }
}