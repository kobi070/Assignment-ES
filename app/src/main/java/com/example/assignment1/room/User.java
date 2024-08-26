package com.example.assignment1.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users_table")
public class User {
    @PrimaryKey
    @ColumnInfo
    public int id;

    @ColumnInfo
    public String email;

    @ColumnInfo
    public String first_name;

    @ColumnInfo
    public String last_name;

    @ColumnInfo
    public String avatar;
}
