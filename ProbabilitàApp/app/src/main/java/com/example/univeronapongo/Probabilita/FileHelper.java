package com.example.univeronapongo.Probabilita;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {

    public static void saveTextToFile(Context context, String text, String fileName) {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_APPEND);
            fos.write(text.getBytes());
            fos.write("\n\n".getBytes()); // Aggiunge due righe vuote tra i testi
            Log.d("FileHelper", "Text saved to file: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static List<String> searchInFile(Context context, String fileName, String searchText) {
        List<String> searchResults = new ArrayList<>();
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder paragraph = new StringBuilder();
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    if (paragraph.toString().contains(searchText)) {
                        searchResults.add(paragraph.toString());
                    }
                    paragraph.setLength(0);
                } else {
                    paragraph.append(line).append("\n");
                }
            }
            if (paragraph.length() > 0 && paragraph.toString().contains(searchText)) {
                searchResults.add(paragraph.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return searchResults;
    }


    public static String readFileContent(Context context, String fileName) {
        FileInputStream fis = null;
        StringBuilder content = new StringBuilder();
        try {
            fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content.toString();
    }

    public static void resetFile(Context context, String fileName) {
        // Cancella il file se esiste già
        context.deleteFile(fileName);
        Log.d("FileHelper", "File reset: " + fileName);
    }
}

