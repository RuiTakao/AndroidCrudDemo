package com.takaobrog.crudapponlyactivity.connection;

import com.google.gson.Gson;
import com.takaobrog.crudapponlyactivity.BuildConfig;
import com.takaobrog.crudapponlyactivity.util.Data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerConnection {

    private static final String METHOD_GET = "GET";
    private static final int TIMEOUT = 0;

    public Data[] getData() {
        Gson gson = new Gson();
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
}
