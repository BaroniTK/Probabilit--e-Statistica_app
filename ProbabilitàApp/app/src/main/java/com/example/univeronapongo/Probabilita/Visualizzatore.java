package com.example.univeronapongo.Probabilita;
import android.widget.TextView;

import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Visualizzatore {

    private static OkHttpClient client;
    private static final String BASE_URL = "http://datascience.maths.unitn.it";

    public static String getHtmlContent(String htmlPath, String username, String data,TextView output) {
        if (client == null) {
            client = new OkHttpClient();
        }

        String fullUrl = BASE_URL + htmlPath;

        Request request = new Request.Builder()
                .url(fullUrl)
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String html = response.body().string();


                return extractRawText(html, username, "Soluzioni all’esercizio del " + data + " creato per");
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String extractRawText(String html, String username, String exerciseTitle) {
        Document doc = Jsoup.parse(html);
        String rawText = doc.text();
        String regexUsername = username.replaceAll("\\.", "\\\\.");
        rawText = rawText.replaceAll(regexUsername, "");
        rawText = rawText.replaceAll(exerciseTitle, "");
        rawText = rawText.trim().replaceAll("\\s{2,}", " ");
        return rawText;
    }
}
