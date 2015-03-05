package com.dkroz.game;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dkroz on 3/5/15.
 */
public class MyService extends IntentService {

    public MyService() {
        super("MyService");
    }

    public static void sendMove(Context context, int type) {
        Intent intent = new Intent(context, MyService.class);
        intent.setAction(MainActivity.ACTION_SEND_MOVE);
        switch (type) {
            case 0:
                intent.putExtra("url", context.getString(R.string.url_rock));
                break;
            case 1:
                intent.putExtra("url", context.getString(R.string.url_paper));
                break;
            case 2:
                intent.putExtra("url", context.getString(R.string.url_scissors));
                break;
        }
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (action.equals(MainActivity.ACTION_SEND_MOVE)) {
                if (intent.hasExtra("url")){
                    sendMove(intent.getStringExtra("url"));
                }
            }
        }
    }

    protected void sendMove(String url) {
        Intent intent = new Intent(MainActivity.BROADCAST_GET_RESULT);
        HttpClient httpClient = MyHttpClient.getInstance();
        try {
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode()==200) {
                String rawResponse = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
                String textResult = rawResponse.substring(
                        rawResponse.indexOf("<h2>") + 4,
                        rawResponse.indexOf("</h2>")
                );
                String computerChoice = textResult.substring(
                        textResult.indexOf("threw ") + 6,
                        textResult.indexOf(".")
                );
                intent.putExtra("result", textResult.substring(0, textResult.indexOf("!") + 1));
                intent.putExtra("computer", computerChoice);
            }
        } catch (IOException e) {
            e.printStackTrace();
            intent.putExtra("success", false);
        } catch (Exception e) {
            e.printStackTrace();
            intent.putExtra("success", false);
        }
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

}
