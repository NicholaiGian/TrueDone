package com.example.truedone.ui.main;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.example.truedone.databinding.ItemImprovementBinding;
import java.util.List;

public class ImprovementAdapter extends RecyclerView.Adapter<ImprovementAdapter.ViewHolder> {
    private List<String> improvements;

    public ImprovementAdapter(List<String> improvements) {
        this.improvements = improvements;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ItemImprovementBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.binding.tvImprovement.setText("• " + improvements.get(position));
    }

    @Override
    public int getItemCount() {
        return improvements.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemImprovementBinding binding;
        ViewHolder(ItemImprovementBinding b) { super(b.getRoot()); binding = b; }
    }
}
