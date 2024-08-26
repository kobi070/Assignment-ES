package com.example.assignment1.screens.user;

import androidx.lifecycle.ViewModel;

import com.example.assignment1.room.AppDatabase;
import com.example.assignment1.room.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserViewModel extends ViewModel {
    private static final String TAG = "UserViewModel";
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final AppDatabase appDatabase = AppDatabase.getInstance();

    public UserViewModel() {}

    /**
     * Deletes a user from the database.
     *
     * @param user The user object to be deleted.
     */
    public void deleteUser(User user) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                appDatabase.userDAO().deleteUser(user);
            }
        });
    }

    /**
     * Updates an existing user in the database.
     *
     * @param user The user object with updated information.
     */
    public void updateUser(User user) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                appDatabase.userDAO().updateUser(user);
            }
        });
    }

    /**
     * Inserts a new user into the database.
     *
     * @param user The user object to be inserted.
     */
    public void insertUser(User user) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                appDatabase.userDAO().insertUser(user);
            }
        });
    }
}
