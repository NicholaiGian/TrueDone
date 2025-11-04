package com.example.truedone.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserProfile {
    @PrimaryKey @NonNull
    public String userId;
    public String firstName;
    public String lastName;
    public String username;
    public String email;
    public String passwordHash;
    public boolean isDarkMode;
    public long createdAt;

    public UserProfile() {}
}