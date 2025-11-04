package com.example.truedone.ui.task;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.bumptech.glide.Glide;
import com.example.truedone.data.AppDatabase;
import com.example.truedone.data.model.Task;
import com.example.truedone.databinding.ActivityTaskStep2Binding;
import com.example.truedone.utils.GeminiHelper;
import com.example.truedone.utils.ImageUtils;
import com.example.truedone.utils.TimeUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TaskStep2Activity extends AppCompatActivity {
    private ActivityTaskStep2Binding binding;
    private String taskId;
    private AppDatabase db;
    private Task currentTask;

    // Timer Variables
    private Handler timerHandler = new Handler(Looper.getMainLooper());
    private long startTime;
    private boolean isTimerRunning = true;

    // Image Capture Variables
    private String tempPhotoPath;
    private Uri tempPhotoUri;

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isTimerRunning) return;
            long millis = System.currentTimeMillis() - startTime;
            binding.tvTimer.setText(TimeUtils.formatTimer(millis));
            timerHandler.postDelayed(this, 1000);
        }
    };

    // 1. Camera Launcher
    private final ActivityResultLauncher<Uri> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(), success -> {
                if (success) {
                    processCompletion(tempPhotoUri);
                } else {
                    Toast.makeText(this, "Capture cancelled", Toast.LENGTH_SHORT).show();
                }
            });

    // 2. Gallery Launcher
    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    processCompletion(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskStep2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        taskId = getIntent().getStringExtra("TASK_ID");
        db = AppDatabase.getDatabase(this);

        loadTask();

        // Button Listeners
        binding.btnBack.setOnClickListener(v -> finish()); // Keeps task Pending in DB
        binding.btnCancel.setOnClickListener(v -> confirmCancelTask()); // Prompts to delete Task
        binding.btnCamera.setOnClickListener(v -> launchCamera());
        binding.btnGallery.setOnClickListener(v -> galleryLauncher.launch("image/*"));
    }

    private void loadTask() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            currentTask = db.taskDao().getTaskById(taskId);
            runOnUiThread(() -> {
                if (currentTask != null) {
                    binding.tvTaskTitle.setText(currentTask.taskTitle);
                    Glide.with(this).load(currentTask.beforeImagePath).into(binding.ivBefore);

                    // Resume timer based on original creation time
                    startTime = currentTask.createdTimestamp;
                    timerHandler.postDelayed(timerRunnable, 0);
                }
            });
        });
    }

    private void launchCamera() {
        File photoFile = new File(getFilesDir(), "after_" + System.currentTimeMillis() + ".jpg");
        tempPhotoPath = photoFile.getAbsolutePath();
        tempPhotoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
        cameraLauncher.launch(tempPhotoUri);
    }

    private void confirmCancelTask() {
        new AlertDialog.Builder(this)
                .setTitle("Discard Task?")
                .setMessage("Are you sure you want to completely delete this task? This cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        db.taskDao().deleteTask(currentTask);
                        runOnUiThread(this::finish);
                    });
                })
                .setNegativeButton("Keep Pending", null)
                .show();
    }

    private void processCompletion(Uri finalImageUri) {
        // Stop Timer & Update UI State
        isTimerRunning = false;
        timerHandler.removeCallbacks(timerRunnable);

        binding.btnCamera.setEnabled(false);
        binding.btnGallery.setEnabled(false);
        binding.btnCamera.setText("Analyzing Image...");
        binding.btnCamera.setIconResource(android.R.drawable.ic_popup_sync);

        long duration = System.currentTimeMillis() - startTime;

        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                // 1. Process & Save the After Image natively to internal storage
                Bitmap afterBitmap = ImageUtils.compressImageForGemini(this, finalImageUri);
                String savedAfterPath = ImageUtils.saveBitmapToInternalStorage(this, afterBitmap, "final_after_" + taskId + ".jpg");

                // 2. Load and Scale the Before Image
                Bitmap beforeBitmap = BitmapFactory.decodeFile(currentTask.beforeImagePath);
                Bitmap beforeScaled = Bitmap.createScaledBitmap(beforeBitmap, 800, 800, true);

                // 3. Call Gemini AI
                GeminiHelper.analyzeImages(beforeScaled, afterBitmap, new GeminiHelper.GeminiCallback() {
                    @Override
                    public void onSuccess(String statement, List<String> improvements) {
                        finalizeTask(savedAfterPath, duration, statement, improvements);
                    }

                    @Override
                    public void onError(String error) {
                        // In case that AI Failed—when API call limit has been reached, but we still successfully complete the task
                        finalizeTask(savedAfterPath, duration, "AI analysis unavailable at this moment.", new ArrayList<>());
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error processing images", Toast.LENGTH_SHORT).show();
                    binding.btnCamera.setEnabled(true);
                    binding.btnGallery.setEnabled(true);
                    binding.btnCamera.setText("Capture After Photo");
                });
            }
        });
    }

    private void finalizeTask(String afterPath, long duration, String statement, List<String> improvements) {
        currentTask.afterImagePath = afterPath;
        currentTask.timeTaken = duration;
        currentTask.completedTimestamp = System.currentTimeMillis();
        currentTask.isCompleted = true;
        currentTask.aiStatement = statement;
        currentTask.aiImprovements = improvements;

        AppDatabase.databaseWriteExecutor.execute(() -> {
            db.taskDao().updateTask(currentTask);
            runOnUiThread(() -> {
                Intent intent = new Intent(this, TaskStep3Activity.class);
                intent.putExtra("TASK_ID", taskId);
                startActivity(intent);
                finish();
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isTimerRunning = false;
        timerHandler.removeCallbacks(timerRunnable);
    }
}