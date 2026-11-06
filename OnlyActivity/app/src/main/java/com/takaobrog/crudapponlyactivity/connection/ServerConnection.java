package com.takaobrog.crudapponlyactivity.connection;

import com.google.gson.Gson;
import com.takaobrog.crudapponlyactivity.util.Data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerConnection {
    public Data[] getData() {
        Gson gson = new Gson();
        Data[] data = new Data[0];
        try {
            URL url = new URL("http://10.0.2.2:8765/todos/todo_list/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(0);
            connection.setReadTimeout(0);
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setDoOutput(false);
            connection.setDoInput(true);
            connection.connect();
            InputStream is = connection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder builder = new StringBuilder();
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
