package com.example.assignment1.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.internal.Primitives;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;

public class SharedPrefsUtil {
    private static final String SP_FILE = "FILE_NAME";
    private static SharedPrefsUtil instance;
    private final SharedPreferences prefs;

    private SharedPrefsUtil(Context context)  {
        prefs = context.getApplicationContext().getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new SharedPrefsUtil(context);
        }
    }

    public static SharedPrefsUtil getInstance() {
        return instance;
    }

    public void putDouble(String key, double defValue) {
        putString(key, String.valueOf(defValue));
    }

    public double getDouble(String key, double defValue) {
        return Double.parseDouble(getString(key, String.valueOf(defValue)));
    }

    public void putInt(String key, int value) {
        prefs.edit().putInt(key, value).apply();
    }

    public int getInt(String key, int defValue) {
        return prefs.getInt(key, defValue);
    }

    public void putString(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }

    public String getString(String key, String defValue) {
        return prefs.getString(key, defValue);
    }

    public String getStringFromSP(String key, String def) {
        return prefs.getString(key, def);
    }

    public void putBooleanToSP(String key, boolean value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }


    public boolean getBooleanFromSP(String key, boolean def) { return prefs.getBoolean(key,def); }

    public void putFloat(String key, float val) {
        prefs.edit().putFloat(key,val).apply();
    }

    public float getFloat(String key, float def) {
        return prefs.getFloat(key, def);
    }

    public <T> void putArray(String KEY, ArrayList<T> array) {
        String json = new Gson().toJson(array);
        prefs.edit().putString(KEY, json).apply();
    }

    public <T> ArrayList<T> getArray(String KEY, TypeToken<ArrayList<T>> typeToken) {
        try {
            ArrayList<T> arr = new Gson().fromJson(prefs.getString(KEY, ""), typeToken.getType());
            if (arr == null) {
                return new ArrayList<>();
            }
            return arr;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void putObject(String KEY, Object value) {
        prefs.edit().putString(KEY, new Gson().toJson(value)).apply();
    }

    public <T> T getObject(String KEY, Class<T> mModelClass) {
        Object object = null;
        try {
            object = new Gson().fromJson(prefs.getString(KEY, ""), mModelClass);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Primitives.wrap(mModelClass).cast(object);
    }

    public <S, T> void putHashMap(String KEY, HashMap<S, T> hashMap) {
        String json = new Gson().toJson(hashMap);
        prefs.edit().putString(KEY, json).apply();
    }

    public <S, T> HashMap<S, T> getHashMap(String KEY, TypeToken<HashMap<S, T>> typeToken) {
        try {
            HashMap<S, T> map = new Gson().fromJson(prefs.getString(KEY, ""), typeToken.getType());
            if (map == null) {
                return new HashMap<>();
            }
            return map;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new HashMap<>();
    }
}
