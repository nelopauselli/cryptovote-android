package com.nelo.cryptovote;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class Storage {
    public static void setLastServer(Context context, CharSequence server) {
        try {
            File file = new File(context.getFilesDir(), "servers.txt");
            Log.d("Storage", "path: " + file.getAbsolutePath());

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(server.toString().getBytes("utf-8"));
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Log.e("Storage", e.getMessage(), e);
        }
    }

    public static CharSequence getLastServer(Context context) {
        try {
            File file = new File(context.getFilesDir(), "servers.txt");
            Log.d("Storage", "path: " + file.getAbsolutePath());

            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
                String line = reader.readLine();
                return line;
            }
        } catch (Exception e) {
            Log.e("Storage", e.getMessage(), e);
        }
        return "";
    }
}
