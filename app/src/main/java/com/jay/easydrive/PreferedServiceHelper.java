package com.jay.easydrive;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferedServiceHelper {
    private static final String DRIVE_FILE_ID = "drive_file_id";
    private final SharedPreferences sharedPreferences;
    private final String DRIVE_SHARED_PREF_NAME ="drive_uploaded_data";
    private SharedPreferences.Editor editor;

    public PreferedServiceHelper(Context context) {

        sharedPreferences = context.getSharedPreferences(DRIVE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveDriveSession(String drivefileId){
        editor.putString(DRIVE_FILE_ID,drivefileId).commit();
    }

    public String getDriveSession(){
        return sharedPreferences.getString(DRIVE_FILE_ID,"");
    }

//    public void removeSession(){
//        editor.remove("is_drive_uploaded").commit();
//    }
}
