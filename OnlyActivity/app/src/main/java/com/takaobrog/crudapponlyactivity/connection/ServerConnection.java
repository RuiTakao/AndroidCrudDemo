package com.takaobrog.crudapponlyactivity.connection;

import com.google.gson.Gson;
import com.takaobrog.crudapponlyactivity.BuildConfig;
import com.takaobrog.crudapponlyactivity.util.Data;
import com.takaobrog.crudapponlyactivity.util.Logging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerConnection {

    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_DELETE = "DELETE";
    private static final String HEADER_KEY = "Content-Type";
    private static final String HEADER_VALUE = "application/json; charset=utf-8";
    private static final String CHARSET = "UTF-8";
    private static final int TIMEOUT = 0;
    private final Gson gson = new Gson();

    public Data[] getData() {
        StringBuilder builder = new StringBuilder();
        Data[] data = new Data[0];
        try {
            URL url = new URL(BuildConfig.BASE_URL + "todo/getTodoList/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(TIMEOUT);
            connection.setReadTimeout(TIMEOUT);
            connection.setRequestMethod(METHOD_GET);
            connection.setUseCaches(false);
            connection.setDoOutput(false);
            connection.setDoInput(true);

            connection.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line);
                data = gson.fromJson(line, Data[].class);
            }
            connection.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return data;
    }

    public Data getData(int id) {
        Data data;
        try {
            URL url = new URL(BuildConfig.BASE_URL + "todo/getTodo/" + id);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(TIMEOUT);
            connection.setReadTimeout(TIMEOUT);
            connection.setRequestMethod(METHOD_GET);
            connection.setUseCaches(false);
            connection.setDoOutput(false);
            connection.setDoInput(true);
            connection.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = br.readLine();
            data = gson.fromJson(line, Data.class);
            connection.disconnect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return data;
    }

    public void create(String title, String memo) {
        String body;
        try {
            JSONObject json = new JSONObject();
            json.put("title", title);
            json.put("memo", memo);
            body = json.toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        try {
            URL url = new URL(BuildConfig.BASE_URL + "todo/create/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(METHOD_POST);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty(HEADER_KEY, HEADER_VALUE);
            connection.connect();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(connection.getOutputStream(), CHARSET)
            );
            writer.write(body);
            writer.close();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Logging.d("DEBUG", "HTTP_OK");
            } else {
                Logging.d("DEBUG", "responseCode " + responseCode);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) {
        String body;
        try {
            JSONObject json = new JSONObject();
            json.put("id", id);
            body = json.toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        try {
            URL url = new URL(BuildConfig.BASE_URL + "todo/delete/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(METHOD_DELETE);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty(HEADER_KEY, HEADER_VALUE);
            connection.connect();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(connection.getOutputStream(), CHARSET)
            );
            Logging.d("DEBUG", "body: " + body);
            writer.write(body);
            writer.close();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Logging.d("DEBUG", "HTTP_OK");
            } else {
                Logging.d("DEBUG", "responseCode " + responseCode);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
