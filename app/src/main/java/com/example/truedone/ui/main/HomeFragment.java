package com.example.truedone.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.truedone.data.AppDatabase;
import com.example.truedone.databinding.FragmentHomeBinding;
import com.example.truedone.ui.task.TaskStep2Activity;
import com.example.truedone.utils.SessionManager;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private TaskAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        adapter = new TaskAdapter(task -> {
            Intent intent = new Intent(getActivity(), TaskStep2Activity.class);
            intent.putExtra("TASK_ID", task.taskId);
            startActivity(intent);
        });

        binding.rvTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvTasks.setAdapter(adapter);

        SessionManager sm = new SessionManager(requireContext());
        String userId = sm.getSavedUserId();

        AppDatabase.getDatabase(getContext()).taskDao().getActiveTasks(userId).observe(getViewLifecycleOwner(), tasks -> {
            // 1. UPDATE THE COUNT TEXT VIEW
            int count = tasks.size();
            // Make it grammatically correct with (1 task vs 2 tasks)
            String taskText = count == 1 ? count + " task" : count + " tasks";
            binding.tvTaskCount.setText(taskText);

            // 2. TOGGLE EMPTY STATE
            if (tasks.isEmpty()) {
                binding.emptyState.setVisibility(View.VISIBLE);
                binding.rvTasks.setVisibility(View.GONE);
            } else {
                binding.emptyState.setVisibility(View.GONE);
                binding.rvTasks.setVisibility(View.VISIBLE);
                adapter.submitList(tasks);
            }
        });

        // Update name
        AppDatabase.databaseWriteExecutor.execute(() -> {
            String name = AppDatabase.getDatabase(getContext()).userDao().getUserById(userId).firstName;
            getActivity().runOnUiThread(() -> binding.tvName.setText(name));
        });
    }
}