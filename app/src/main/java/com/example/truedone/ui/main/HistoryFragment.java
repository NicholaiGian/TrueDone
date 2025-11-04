package com.example.truedone.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.truedone.data.AppDatabase;
import com.example.truedone.data.model.Task;
import com.example.truedone.databinding.BottomSheetTaskDetailBinding;
import com.example.truedone.databinding.FragmentHistoryBinding;
import com.example.truedone.utils.SessionManager;
import com.example.truedone.utils.TimeUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class HistoryFragment extends Fragment {
    private FragmentHistoryBinding binding;
    private HistoryAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        adapter = new HistoryAdapter(this::showTaskDetail);
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvHistory.setAdapter(adapter);

        SessionManager sm = new SessionManager(requireContext());
        String userId = sm.getSavedUserId();

        // Last 7 days Task history
        long sevenDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7);

        AppDatabase.getDatabase(getContext()).taskDao().getCompletedTasksSince(userId, sevenDaysAgo)
                .observe(getViewLifecycleOwner(), tasks -> {
                    if (tasks.isEmpty()) {
                        binding.emptyState.setVisibility(View.VISIBLE);
                        binding.rvHistory.setVisibility(View.GONE);
                    } else {
                        binding.emptyState.setVisibility(View.GONE);
                        binding.rvHistory.setVisibility(View.VISIBLE);
                        adapter.submitList(tasks);
                    }
                });
    }

    private void showTaskDetail(Task task) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        BottomSheetTaskDetailBinding sheetBinding = BottomSheetTaskDetailBinding.inflate(getLayoutInflater());
        dialog.setContentView(sheetBinding.getRoot());

        // Images
        ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(Arrays.asList(task.beforeImagePath, task.afterImagePath));
        sheetBinding.viewPagerDetail.setAdapter(sliderAdapter);

        // Text Data
        sheetBinding.tvSheetTitle.setText(task.taskTitle);
        sheetBinding.tvSheetTime.setText(TimeUtils.formatDuration(task.timeTaken));
        sheetBinding.tvSheetStatement.setText(task.aiStatement != null ? task.aiStatement : "No Analysis");

        // Improvements
        if (task.aiImprovements != null && !task.aiImprovements.isEmpty()) {
            ImprovementAdapter impAdapter = new ImprovementAdapter(task.aiImprovements);
            sheetBinding.rvSheetImprovements.setLayoutManager(new LinearLayoutManager(getContext()));
            sheetBinding.rvSheetImprovements.setAdapter(impAdapter);
        }

        sheetBinding.btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
