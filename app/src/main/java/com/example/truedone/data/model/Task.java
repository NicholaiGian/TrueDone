package com.example.truedone.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

@Entity(tableName = "tasks")
@TypeConverters(Task.Converters.class)
public class Task {
    @PrimaryKey @NonNull
    public String taskId;
    public String userId;
    public String taskTitle;
    public String beforeImagePath;
    public String afterImagePath;
    public String aiStatement;
    public List<String> aiImprovements;
    public long createdTimestamp;
    public Long completedTimestamp;
    public Long timeTaken;
    public boolean isCompleted;

    public Task() {}

    public static class Converters {
        @TypeConverter
        public static List<String> fromString(String value) {
            Type listType = new TypeToken<List<String>>() {}.getType();
            return new Gson().fromJson(value, listType);
        }

        @TypeConverter
        public static String fromList(List<String> list) {
            Gson gson = new Gson();
            return gson.toJson(list);
        }
    }
}
