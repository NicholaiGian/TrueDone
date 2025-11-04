package com.example.truedone.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.truedone.data.dao.TaskDao;
import com.example.truedone.data.dao.UserProfileDao;
import com.example.truedone.data.model.Task;
import com.example.truedone.data.model.UserProfile;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {UserProfile.class, Task.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserProfileDao userDao();
    public abstract TaskDao taskDao();

    private static volatile AppDatabase INSTANCE;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "truedone_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
