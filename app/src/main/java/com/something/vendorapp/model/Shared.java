package com.something.vendorapp.model;

import android.content.Context;
import android.content.SharedPreferences;

public class Shared {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    int mode = 0; //private mode
    String Filename = "vendorsApp";
    String  FIRST_TIME_LAUNCHED = "firsttimelaunched";


    public Shared(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Filename,mode);
        editor = sharedPreferences.edit();
    }

    public boolean isFirstTimeLaunched() {
        return sharedPreferences.getBoolean(FIRST_TIME_LAUNCHED, true);
    }

    public void setFirstTimeLaunched(Boolean firstTimeLaunched) {
        editor.putBoolean(FIRST_TIME_LAUNCHED, firstTimeLaunched);
        editor.commit();
    }

}
