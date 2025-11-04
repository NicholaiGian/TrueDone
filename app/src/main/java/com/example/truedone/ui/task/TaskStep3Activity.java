package com.example.truedone.ui.task;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.truedone.data.AppDatabase;
import com.example.truedone.data.model.Task;
import com.example.truedone.databinding.ActivityTaskStep3Binding;
import com.example.truedone.ui.main.ImageSliderAdapter;
import com.example.truedone.ui.main.ImprovementAdapter;
import com.example.truedone.utils.TimeUtils;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.Arrays;
import java.util.List;

public class TaskStep3Activity extends AppCompatActivity {
    private ActivityTaskStep3Binding binding;
    private String taskId;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskStep3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        taskId = getIntent().getStringExtra("TASK_ID");
        db = AppDatabase.getDatabase(this);

        // Fetch task from database and populate UI
        loadTaskData();

        // Finish activity and return to Home/Main
        binding.btnDone.setOnClickListener(v -> finish());
    }

    private void loadTaskData() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Task task = db.taskDao().getTaskById(taskId);

            runOnUiThread(() -> {
                if (task != null) {
                    updateUI(task);
                }
            });
        });
    }

    private void updateUI(Task task) {
        // 1. Setup Image Slider (Before & After)
        List<String> images = Arrays.asList(task.beforeImagePath, task.afterImagePath);
        ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(images);
        binding.viewPager.setAdapter(sliderAdapter);

        // Link the TabLayout text labels to the ViewPager2
        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("Before");
                    } else {
                        tab.setText("After");
                    }
                }
        ).attach();

        // 2. Setup Time Taken
        if (task.timeTaken != null) {
            binding.tvTimeTaken.setText(TimeUtils.formatTimer(task.timeTaken));
        }

        // 3. Setup AI Statement
        if (task.aiStatement != null) {
            binding.tvAiStatement.setText(task.aiStatement);
        } else {
            binding.tvAiStatement.setText("AI analysis unavailable for this task.");
        }

        // 4. Setup AI Improvements List
        if (task.aiImprovements != null && !task.aiImprovements.isEmpty()) {
            ImprovementAdapter impAdapter = new ImprovementAdapter(task.aiImprovements);
            binding.rvImprovements.setLayoutManager(new LinearLayoutManager(this));
            binding.rvImprovements.setAdapter(impAdapter);
            binding.rvImprovements.setVisibility(View.VISIBLE);
        } else {
            binding.rvImprovements.setVisibility(View.GONE);
        }
    }
}