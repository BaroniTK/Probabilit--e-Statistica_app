package com.example.univeronapongo.Probabilita;

import java.io.IOException;
import java.util.Random;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ApiCaller {

    private static final OkHttpClient client = new OkHttpClient();
    private static final String BASE_URL = "http://datascience.maths.unitn.it/ocpu/library/psDoexercises/R/renderRmd";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String[] softwareNames = {"Chrome"};
    private static final String[] operatingSystems = {"Windows", "Linux"};
    private static final Random random = new Random();

    public static String getApi(String token, String username, String today) {
        String proxy = getRandomProxy();
        String user_agent = getRandomUserAgent();

        MediaType mediaType = MediaType.parse("application/json");

        // Primo tentativo con _1.Rmd
        String json1 = "{\"file\":\"" + username + "-" + token + "-" + today + "_1.Rmd\",\"output_file_name\":\"" + today + ".html\"}";
        String htmlPath = requestHtmlPath(json1, mediaType, user_agent);

        // Se il primo tentativo fallisce, prova con _2.Rmd
        if (htmlPath == null) {
            String json2 = "{\"file\":\"" + username + "-" + token + "-" + today + "_2.Rmd\",\"output_file_name\":\"" + today + ".html\"}";
            htmlPath = requestHtmlPath(json2, mediaType, user_agent);
        }

        return htmlPath;
    }


    private static String requestHtmlPath(String json, MediaType mediaType, String user_agent) {
        RequestBody body = RequestBody.create(json, mediaType);

        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(body)
                .addHeader("Accept", "text/plain, /; q=0.01")
                .addHeader("Accept-Language", "it-IT,it;q=0.9,en-GB;q=0.8,en;q=0.7,en-US;q=0.6")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/json")
                .addHeader("Origin", "http://datascience.maths.unitn.it")
                .addHeader("Pragma", "no-cache")
                .addHeader("Referer", "http://datascience.maths.unitn.it/ocpu/library/psDoexercises/www/")
                .addHeader("User-Agent", user_agent)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("dnt", "1")
                .build();

        try {
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            System.out.println(responseBody);
            // Estrai il percorso del file HTML dalla risposta
            String[] lines = responseBody.split("\n");
            for (String line : lines) {
                if (line.endsWith(".html")) {
                    return line.trim(); // Rimuovi spazi bianchi aggiuntivi
                }
            }
        } catch (IOException e) {
            // Ignora l'errore e restituisci null
        }

        return null;
    }

    private static String getRandomProxy() {
        return "http://65.21.159.49:80";
    }

    private static String getRandomUserAgent() {
        String softwareName = softwareNames[random.nextInt(softwareNames.length)];
        String operatingSystem = operatingSystems[random.nextInt(operatingSystems.length)];
        return String.format("%s/%s", softwareName, operatingSystem);
    }
}
