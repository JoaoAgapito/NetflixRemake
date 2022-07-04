package com.example.netflixremake.util.JsonDownloadTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.netflixremake.Model.Category;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class JsonDownloadTask extends AsyncTask <String, Void, List<Category>> {

    private Context context;
    ProgressDialog dialog;

    public JsonDownloadTask(Context context) {
        this.context = context;
    }
    // main - thread (abre a execução na tela)
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = ProgressDialog.show(context, "Carregando", "", true);
    }
    // faz a chamada na api (url)
    @Override
    protected List<Category> doInBackground(String... params) {
        String url = params[0];
        try {
            URL requestUrl = new URL(url);

            try {
                HttpsURLConnection urlConnection = (HttpsURLConnection) requestUrl.openConnection();
                urlConnection.setReadTimeout(2000);
                urlConnection.setConnectTimeout(2000);

                int responseCode = urlConnection.getResponseCode();
                if (responseCode > 400) {
                    throw new IOException("Error na comunicação com o servidor");
                }

                InputStream inputStream = urlConnection.getInputStream();

                BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());

                String jsonAsString = toString(in);

                try {
                    List<Category> categories = getCategories (new JSONObject(jsonAsString));
                    in.close();

                    return categories;

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Category> getCategories(JSONObject json) {
    }

    // main - thread (fecha a execução na tela)
    @Override
    protected void onPostExecute(List<Category> categories) {
        super.onPostExecute(categories);
        dialog.dismiss();
    }
    private String toString (InputStream is) throws IOException {
        byte[] bytes = new  byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int lidos;
        while ((lidos = is.read(bytes))> 0){
            baos.write(bytes, 0, lidos);
        }
        return new String(baos.toByteArray());
    }
}

