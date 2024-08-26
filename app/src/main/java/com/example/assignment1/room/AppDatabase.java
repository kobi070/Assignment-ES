package com.example.assignment1.room;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.assignment1.utils.Constants;

@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDAO userDAO();

    private static volatile AppDatabase instance;

    /**
     * Provides a singleton instance of AppDatabase.
     * Building the Room based on the instance of AppDatabase
     * @return The singleton instance of AppDatabase.
     */
    public static void init(final Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, Constants.ROOM_DATABASE_NAME)
                            .build();
                }
            }
        }
    }

    public static AppDatabase getInstance() {
        return instance;
    }
}
