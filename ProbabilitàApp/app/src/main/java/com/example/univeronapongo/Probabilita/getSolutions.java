package com.example.univeronapongo.Probabilita;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.univeronapongo.Probabilita.ApiCaller;
import com.example.univeronapongo.Probabilita.FileHelper;
import com.example.univeronapongo.Probabilita.Visualizzatore;
import com.example.univeronapongo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class getSolutions extends AppCompatActivity {

    private static TextView output;
    private Button bottoneRicerca, invia, informazioni;
    private WebView webView;
    private EditText nome, password, matricola, cerca;

    private static final OkHttpClient client = new OkHttpClient();
    private static final Random random = new Random();

    private static final String[] softwareNames = {"Chrome"};
    private static final String[] operatingSystems = {"Windows", "Linux"};

    private static final String[] proxies = {"31.130.247.185:9874:sdvpasd:boiling", "31.130.247.186:9874:sdvpasd:boiling"};

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static String getRandomProxy() {
        return proxies[random.nextInt(proxies.length)];
    }

    private static String formatProxy(String proxy) {
        String[] p = proxy.split(":");
        return String.format("http://%s:%s@%s:%s", p[2], p[3], p[0], p[1]);
    }

    private static String getRandomUserAgent() {
        String softwareName = softwareNames[random.nextInt(softwareNames.length)];
        String operatingSystem = operatingSystems[random.nextInt(operatingSystems.length)];
        return String.format("%s/%s", softwareName, operatingSystem);
    }

    public static String getAddress(String username, String password, int matricola) throws IOException {
        String url = "http://datascience.maths.unitn.it/ocpu/library/psDoexercises/R/getSolutions";

        JSONObject json = new JSONObject();
        try {
            json.put("user", username + ":" + password);
            json.put("id", matricola);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, json.toString());

        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", getRandomUserAgent())
                .addHeader("Accept", "text/plain, */*; q=0.01")
                .addHeader("Accept-Language", "en-US,en;q=0.5")
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Origin", "http://datascience.maths.unitn.it")
                .addHeader("Connection", "keep-alive")
                .addHeader("Referer", "http://datascience.maths.unitn.it/ocpu/library/psDoexercises/www/")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                Log.d("Response", responseBody);
                return "http://datascience.maths.unitn.it" + responseBody.split("\n")[0] + "/json";
            } else {
                throw new IOException("Unexpected code " + response);
            }
        }
    }

    private String generateHtmlContent(String latexContent) {
        String template = "<html>" +
                "<head>" +
                "<script type=\"text/javascript\" async " +
                "src=\"https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-mml-chtml.js\">" +
                "</script>" +
                "<script type=\"text/x-mathjax-config\">" +
                "MathJax = {" +
                "  tex: {" +
                "    inlineMath: [['$', '$'], ['\\(', '\\)']]," +
                "    displayMath: [['$$', '$$'], ['\\[', '\\]']]" +
                "  }" +
                "};" +
                "</script>" +
                "</head>" +
                "<body>" +
                latexContent + // Inserisci il contenuto LaTeX generato
                "<script type=\"text/javascript\">" +
                "MathJax.typeset();" +
                "</script>" +
                "</body>" +
                "</html>";

        return template;
    }



    private void settaRisultati(List<String> searchResults) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div>"); // Aggiungi un div per contenere tutto il contenuto
        for (String result : searchResults) {
            result = result.replaceAll("(La risposta corretta è: \\d+)", "<strong>$1</strong>");
            sb.append("<p>").append(result).append("</p><br>"); // Usa il delimitatore MathJax per ogni risultato
        }
        sb.append("</div>"); // Chiudi il div

        output.setVisibility(View.GONE); // Nascondi il TextView
        bottoneRicerca.setVisibility(View.GONE);
        cerca.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE); // Mostra il WebView
        String formattedHtml = generateHtmlContent(sb.toString());
        webView.loadDataWithBaseURL(null, formattedHtml, "text/html", "UTF-8", null);
    }




    @SuppressLint("StaticFieldLeak")
    private void getRequest(String url, String username) {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... urls) {
                try {
                    Request request = new Request.Builder()
                            .url(urls[0])
                            .addHeader("User-Agent", getRandomUserAgent())
                            .addHeader("Accept", "text/plain, */*; q=0.01")
                            .addHeader("Accept-Language", "en-US,en;q=0.5")
                            .addHeader("Content-Type", "application/json")
                            .addHeader("X-Requested-With", "XMLHttpRequest")
                            .addHeader("Origin", "http://datascience.maths.unitn.it")
                            .addHeader("Connection", "keep-alive")
                            .addHeader("Referer", "http://datascience.maths.unitn.it/ocpu/library/psDoexercises/www/")
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (response.isSuccessful()) {
                            String s = response.body().string();
                            String s2 = new String(s);
                            JSONObject obj = new JSONObject(s2);
                            JSONArray daSplittare = obj.getJSONArray("files");
                            JSONArray daSplittareDate = obj.getJSONArray("dates");
                            String dates = daSplittareDate.toString();
                            String files = daSplittare.toString();
                            files = files.substring(1, files.length() - 1); //tolgo le quadre
                            dates = dates.substring(1, dates.length() - 1);
                            String[] splitDate = dates.split(",");
                            String[] split = files.split(",");
                            //butto i token in un array
                            for (int i = 0; i < split.length; i++) {
                                String[] splittatoCentrale = split[i].split("-");
                                if (splittatoCentrale.length > 1) {
                                    //faccio la richiesta a tutti i token
                                    splitDate[i] = splitDate[i].substring(1, splitDate[i].length() - 1); //tolgo le ""
                                    String htmlPath = ApiCaller.getApi(splittatoCentrale[1], username, splitDate[i]);
                                    FileHelper.saveTextToFile(getApplicationContext(), Objects.requireNonNull(Visualizzatore.getHtmlContent(htmlPath, username, splitDate[i], output)), "testi.txt");
                                    File directory = getApplicationContext().getFilesDir();
                                    String filePath = new File(directory, "testi.txt").getAbsolutePath();
                                    Log.d("File Path", filePath);
                                }
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    output.setText("Presi e salvati sul file, clicca su cerca soluzioni G :)");
                                    settingRicerca();
                                }
                            });
                        } else {
                            throw new IOException("Unexpected code " + response);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(url);
    }

    private void settingRicerca(){
        // Nascondi gli altri elementi
        invia.setVisibility(View.GONE);
        nome.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        matricola.setVisibility(View.GONE);
        bottoneRicerca.setVisibility(View.VISIBLE);
        cerca.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.weight = 1.0f;
        bottoneRicerca.setLayoutParams(layoutParams);
        cerca.setLayoutParams(layoutParams);
        bottoneRicerca.setOnClickListener(event -> {
            List<String> risultati = FileHelper.searchInFile(getApplicationContext(), "testi.txt", cerca.getText().toString());

            settaRisultati(risultati);
        });
    }

    private void dialogoConferma() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialogoconferma, null);
        builder.setView(dialogView);

        TextView textViewDialogTitle = dialogView.findViewById(R.id.textViewDialogTitle);
        textViewDialogTitle.setText("Vuoi visualizzare i file esistenti o crearne uno nuovo?");

        // Dichiarazione della variabile dialog come finale
        final AlertDialog dialog = builder.create();

        Button buttonViewExisting = dialogView.findViewById(R.id.buttonViewExisting);
        buttonViewExisting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //risultati esistenti
                System.out.println(FileHelper.readFileContent(getApplicationContext(),"testi.txt"));
                settingRicerca();
                dialog.dismiss();
            }

        });

        Button buttonCreateNew = dialogView.findViewById(R.id.buttonCreateNew);
        buttonCreateNew.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "INSERISCI CORRETTAMENTE LE CREDENZIALI!", Toast.LENGTH_LONG).show();
                dialog.dismiss();
                invia.setOnClickListener(event->{
                    output.setText("Sto prendendo gli esercizi NON TOCCARE NULLA");

                    new AsyncTask<Void, Void, Void>() {
                        @SuppressLint("StaticFieldLeak")
                        @Override
                        protected Void doInBackground(Void... voids) {
                            try {
                                String username = nome.getText().toString().toLowerCase();
                                String passwordString = password.getText().toString();
                                int matricolaString = Integer.parseInt(matricola.getText().toString());
                                String address = getAddress(username, passwordString, matricolaString);
                                getRequest(address, username);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                    }.execute();

                });
            }
        });

        dialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getsolutions);
        informazioni=findViewById(R.id.informazioni);
        output = findViewById(R.id.output);
        bottoneRicerca = findViewById(R.id.bottoneCerca);
        invia = findViewById(R.id.invia);
        nome = findViewById(R.id.nome);
        password = findViewById(R.id.password);
        webView = findViewById(R.id.web_view);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        matricola = findViewById(R.id.matricola);
        cerca = findViewById(R.id.cerca);

        informazioni.setOnClickListener(event->{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Informazioni");
            builder.setMessage("Per scrivere le variabili scrivete \\(X\\) nel caso di un problema tipo 'Sia X una variabile aleatoria...'. Questo anche con tutti i valori scritti in termini matematici es: \\(n=2\\)");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        });
        dialogoConferma();
    }
}