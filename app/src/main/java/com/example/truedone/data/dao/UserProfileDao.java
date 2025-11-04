package com.example.truedone.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.truedone.data.model.UserProfile;

@Dao
public interface UserProfileDao {
    @Insert
    void insertUser(UserProfile user);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    UserProfile getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    UserProfile getUserByUsername(String username);

    @Query("SELECT * FROM users WHERE userId = :id LIMIT 1")
    UserProfile getUserById(String id);

    @Query("SELECT count(*) > 0 FROM users WHERE email = :email")
    boolean emailExists(String email);

    @Query("SELECT count(*) > 0 FROM users WHERE username = :username")
    boolean usernameExists(String username);

    @Update
    void updateUser(UserProfile user);
}
