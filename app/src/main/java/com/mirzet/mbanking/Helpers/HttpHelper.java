package com.mirzet.mbanking.Helpers;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mirzet.mbanking.interfaces.IDataHandler;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpHelper {

    IDataHandler callback;

    public HttpHelper(IDataHandler callback) {
        this.callback = callback;
    }


    public void HttpRequest_GET(String URL) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onDataFault(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("response ", "onResponse(): " + response.body());
                callback.onDataReceived(response);
            }
        });
    }

    public static <T> T ParseFromJson(String html, Class<T> cls) {
        try {
            String mJsonString = html;
            JsonParser parser = new JsonParser();
            JsonElement mJson = parser.parse(mJsonString);
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(mJson, cls);
        } catch (Exception e) {
            return null;
        }
    }
}
