package com.example.truedone.ui.main;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.truedone.databinding.ItemImageSliderBinding;
import java.util.List;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.SliderViewHolder> {
    private List<String> imagePaths;

    public ImageSliderAdapter(List<String> imagePaths) {
        this.imagePaths = imagePaths;
    }

    @Override
    public SliderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SliderViewHolder(ItemImageSliderBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(SliderViewHolder holder, int position) {
        String path = imagePaths.get(position);
        if (path != null) {
            Glide.with(holder.itemView)
                    .load(path)
                    .centerCrop()
                    .into(holder.binding.ivSliderImage);
        }
    }

    @Override
    public int getItemCount() {
        return imagePaths.size();
    }

    static class SliderViewHolder extends RecyclerView.ViewHolder {
        ItemImageSliderBinding binding;
        SliderViewHolder(ItemImageSliderBinding b) { super(b.getRoot()); binding = b; }
    }
}
