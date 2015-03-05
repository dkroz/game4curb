package com.dkroz.game;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by dkroz on 3/5/15.
 */
public class GameHistory {

    private static final String fileName = "GameHistory.log";

    public static void saveResult(Context context, String data) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_APPEND);
            fos.write(data.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String loadHistory(Context context) {
        String result = "";
        try {
            FileInputStream fis = context.openFileInput(fileName);
            int c;
            while( (c = fis.read()) != -1){
                result = result + Character.toString((char)c);
            }
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.replace("|", "\n");
    }

    public static boolean clearHistory(Context context) {
        try {
            context.deleteFile(fileName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
