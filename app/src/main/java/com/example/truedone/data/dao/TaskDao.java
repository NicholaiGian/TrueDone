package com.example.truedone.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.truedone.data.model.Task;
import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    void insertTask(Task task);

    @Update
    void updateTask(Task task);

    @Query("SELECT * FROM tasks WHERE userId = :userId AND isCompleted = 0 ORDER BY createdTimestamp DESC")
    LiveData<List<Task>> getActiveTasks(String userId);

    @Query("SELECT * FROM tasks WHERE userId = :userId AND isCompleted = 1 AND completedTimestamp >= :since ORDER BY completedTimestamp DESC")
    LiveData<List<Task>> getCompletedTasksSince(String userId, long since);

    @Query("SELECT * FROM tasks WHERE taskId = :taskId LIMIT 1")
    Task getTaskById(String taskId);

    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId")
    int getTotalCount(String userId);

    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId AND isCompleted = 1")
    int getCompletedCount(String userId);

    @Delete
    void deleteTask(Task task);
}
