package com.projects.diwanshusoni.studentprofiletracker.helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.projects.diwanshusoni.studentprofiletracker.Activities.ChimeraChatActivity;
import com.projects.diwanshusoni.studentprofiletracker.Activities.UserDashboardActivity;
import com.google.gson.Gson;
import com.projects.diwanshusoni.studentprofiletracker.adapters.C_adapterGridviewChatter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Created by Diwanshu Soni on 15-09-2017.
 */

public class Chatter extends AsyncTask<String, Integer, String> {

    private static final String SEPARATOR = "__::__";
    String TAG ="12345";
    private Context context = null;

    static String apiRoot = "https://api.hutoma.ai/v1";
    // id of the AI that we want to talk to
    static String aiId = "69ebc15a-3835-4a98-8b55-24e2f5cf4b72";
    // the authorization token to access the AI
    static String authToken = "eyJhbGciOiJIUzI1NiIsImNhbGciOiJERUYifQ.eNocyjEOwjAMQNG7eMZSG9tNzIZoh0hRKyEWJhSb9AKICfXuREx_-O8Lt60scP7neS15We_bWh5wgkvOcx-TNvNRKlIiQa6aMJkIBm5hF9_ZYuj6_bGOX1FpqqJIbWgdDwm1smP0xh6DjmwExw8AAP__.b5p73pn6B6IJrYnzV99CZJmh7SfR4OBUu_6kz59ypzY";
    // Gson de/serializer
    private Gson gson;
    // tracks the current chat
    private String chatId = "";


    static String encoding = java.nio.charset.StandardCharsets.UTF_8.name();


    //imporatn
    private String input = "";
    private String output = "";
    private TextView tv_bot;

    private ArrayList arrayList;
    C_adapterGridviewChatter  adapterGridviewChatter;

    public void setInput(String input, ArrayList arrayList, C_adapterGridviewChatter adapterGridviewChatter) {
       // this.tv_bot = tv_bot;
        this.input = input;
        this.adapterGridviewChatter = adapterGridviewChatter;
        this.arrayList = arrayList;
    }

    public Chatter(Context context) {
        this.context = context;
    }

    public String mainBot() throws Exception{
        try {
        gson = new GsonBuilder().create();


        // generate a URL with this data
        URL url = prepareUrl(apiRoot, aiId, input, ChimeraChatActivity.chatId);

        // create a new connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // set the authorization header
        connection.setRequestProperty("Authorization", String.format("Bearer %s", authToken));

        // make the connection and get the response code
        connection.connect();
        int responseCode = connection.getResponseCode();
       // Log.d("12345", "response code: "+responseCode);
        // read an input stream; either the input or the error depending on the HTTP response code
        try
            (InputStream inputStream = (responseCode == HttpURLConnection.HTTP_OK)
                ? connection.getInputStream() : connection.getErrorStream()) {

            // try to deserialize the input stream from JSON
            Result result = deserialize(inputStream);

            // if the call succeeded then we have a Result that contains a ChatResult object
            if (result.getStatus().getCode() == HttpURLConnection.HTTP_OK) {

                // print the answer
//                        System.out.print("AI:");
//                        System.out.println(result.getResult().getAnswer());
             //   tv.setText("AI:");
             //   tv.append("___"+result.getResult().getAnswer());
                Log.d("123456", "result : "+result.getResult().getAnswer());


                // keep track of the chatID for the next line of text
                ChimeraChatActivity.chatId = result.getChatId();
                Log.d(TAG, "got thid "+ChimeraChatActivity.chatId);
                output =  result.getResult().getAnswer();
               // ChimeraChatActivity.setAdapterGridviewChatter(output);
               // arrayList.add("bot"+SEPARATOR+output);
              //  adapterGridviewChatter.notifyDataSetChanged();
                return output;
            } else {

                // otherwise all we have is a Result with a Status object (no ChatResult)
                // so print the code and the info to get more information on what went wrong
                // System.out.println(String.format("Error %d: %s", result.getStatus().getCode(), result.getStatus().getInfo()));
                Log.d("1234", "inputLoop: else "+String.format("Error %d: %s", result.getStatus().getCode(), result.getStatus().getInfo()));
            }

        } catch (JsonSyntaxException jse) {
            // if the error was not generated by the chat server (e.g. an error 502) then
            // JSON deserialisation will fail
            // in this case, print the HTTP info that we have
            //System.out.println(String.format("Error %d: %s", responseCode, connection.getResponseMessage()));
            Log.d("1234", "inputLoop: json");

        } catch (IOException e) {
            // any other io exception
            // System.out.println("Error " + e);
            Log.d("1234", "inputLoop: io");
        }
    } catch (Exception e) {
        // anything else gets caught here (e.g. malformed URL or URL encoding exceptions)
        // System.out.println("Failed with exception " + e);
        Log.d("1234", "inputLoop: e: "+e);
    }

        return output;
    }

    private Result deserialize(InputStream stream) {
        return this.gson.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), Result.class);
    }

    /***
     * Generate a valid url to call the chat API
     * @param apiRoot the root part of the URL
     * @param aiId the ID for this AI
     * @param question the plaintext question to ask
     * @param chatId the ID of the conversation, or empty string to start a new one
     * @return formatted URL as a string
     * @throws UnsupportedEncodingException
     * @throws MalformedURLException
     */
    private URL prepareUrl(String apiRoot, String aiId, String question, String chatId) throws UnsupportedEncodingException, MalformedURLException {
        String query = String.format("q=%s&chatId=%s",
                URLEncoder.encode(question, encoding),
                URLEncoder.encode(chatId, encoding));
        String url = String.format("%s/ai/%s/chat?%s",
                apiRoot, aiId, query);
        return new URL(url);
    }

    @Override
    protected String doInBackground(String... urls) {
        try {
            mainBot();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            return output;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String aLong) {
        super.onPostExecute(aLong);
       // tv_bot.setText(aLong);
    }
}

//remove above

class ChatResult {

    private String answer;
    private String history;

    public String getAnswer() {
        return this.answer;
    }

    public String getHistory() {
        return this.history;
    }
}

class Result {

    private String chatId;
    private ChatResult result;
    private Status status;

    public String getChatId() {
        return this.chatId;
    }

    public ChatResult getResult() {
        return this.result;
    }

    public Status getStatus() {
        return this.status;
    }
}

class Status {
    private int code;
    private String info;

    public int getCode() {
        return this.code;
    }

    public String getInfo() {
        return this.info;
    }
}
