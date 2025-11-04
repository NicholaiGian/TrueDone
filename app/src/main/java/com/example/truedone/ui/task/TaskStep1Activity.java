package com.example.truedone.ui.task;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.bumptech.glide.Glide;
import com.example.truedone.data.AppDatabase;
import com.example.truedone.data.model.Task;
import com.example.truedone.databinding.ActivityTaskStep1Binding;
import com.example.truedone.utils.ImageUtils;
import com.example.truedone.utils.SessionManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TaskStep1Activity extends AppCompatActivity {
    private ActivityTaskStep1Binding binding;
    private String selectedImagePath;
    private String currentPhotoPath; // Temp path for the camera to save to
    private Uri photoUri;

    // 1. Define Permissions
    private String[] getRequiredPermissions() {
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        return permissions.toArray(new String[0]);
    }

    // 2. Permission Launcher
    private final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                boolean allGranted = true;
                for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                    if (!entry.getValue()) {
                        allGranted = false;
                        break;
                    }
                }
                if (!allGranted) {
                    Toast.makeText(this, "Permissions required to capture images.", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to Home if denied
                }
            });

    // 3. Native Camera App Launcher
    private final ActivityResultLauncher<Uri> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(), success -> {
                if (success) {
                    // Update the path and overwrite the preview
                    selectedImagePath = currentPhotoPath;
                    showImagePreview(selectedImagePath);
                } else {
                    Toast.makeText(this, "Camera capture cancelled", Toast.LENGTH_SHORT).show();
                }
            });

    // 4. Gallery Picker Launcher
    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    try {
                        String filename = "before_" + System.currentTimeMillis() + ".jpg";
                        // Update the path and overwrite the preview
                        selectedImagePath = ImageUtils.copyUriToInternalStorage(this, uri, filename);
                        showImagePreview(selectedImagePath);
                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskStep1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Check Permissions
        if (!hasAllPermissions()) {
            permissionLauncher.launch(getRequiredPermissions());
        }

        // Setup Buttons
        binding.btnCancel.setOnClickListener(v -> finish()); // The close button to cancel the task creation
        binding.btnCamera.setOnClickListener(v -> launchCamera());
        binding.btnGallery.setOnClickListener(v -> galleryLauncher.launch("image/*"));
        binding.btnCreate.setOnClickListener(v -> createTask());
    }

    private boolean hasAllPermissions() {
        for (String permission : getRequiredPermissions()) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void launchCamera() {
        // Create a temporary file to store the high-res photo from the native camera app
        File photoFile = new File(getFilesDir(), "before_" + System.currentTimeMillis() + ".jpg");
        currentPhotoPath = photoFile.getAbsolutePath();

        // Use FileProvider to safely share the file URI with the camera app
        photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
        cameraLauncher.launch(photoUri);
    }

    private void showImagePreview(String path) {
        // Hide the empty placeholder icon
        binding.ivPlaceholder.setVisibility(View.GONE);

        // Glide automatically replaces/overwrites whatever was there before
        Glide.with(this).load(path).into(binding.ivPreview);
    }

    private void createTask() {
        // Clear previous error
        binding.tilTitle.setError(null);

        String title = binding.etTitle.getText().toString().trim();

        if (selectedImagePath == null) {
            Toast.makeText(this, "Please capture or select an image", Toast.LENGTH_SHORT).show();
            return;
        }
        if (title.isEmpty()) {
            binding.tilTitle.setError("Task title required");
            return;
        }

        SessionManager sm = new SessionManager(this);
        String userId = sm.getSavedUserId();

        Task task = new Task();
        task.taskId = UUID.randomUUID().toString();
        task.userId = userId;
        task.taskTitle = title;
        task.beforeImagePath = selectedImagePath;
        task.createdTimestamp = System.currentTimeMillis();
        task.isCompleted = false;

        AppDatabase.databaseWriteExecutor.execute(() -> {
            AppDatabase.getDatabase(this).taskDao().insertTask(task);
            runOnUiThread(() -> {
                Intent intent = new Intent(this, TaskStep2Activity.class);
                intent.putExtra("TASK_ID", task.taskId);
                startActivity(intent);
                finish();
            });
        });
    }
}