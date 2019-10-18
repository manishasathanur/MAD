package com.example.manisha.buzzfeedheadlines;


import android.os.AsyncTask;
import android.util.Log;


import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

class GetAPI extends AsyncTask<String, Void, ArrayList<Articles>> {
        MainActivity activity;

        URL url=null;


    public GetAPI(MainActivity activity) {
        this.activity = activity;
    }

    @Override

    protected ArrayList<Articles> doInBackground(String... params) {
        HttpURLConnection connection = null;
        ArrayList<Articles> result = new ArrayList<Articles>();
        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String json = IOUtils.toString(connection.getInputStream(), "UTF8");
                JSONObject root = new JSONObject(json);
                JSONArray airtecles = root.getJSONArray("articles");
                for (int i=0;i<airtecles.length();i++) {
                    JSONObject ariticleJson = airtecles.getJSONObject(i);
                    Articles article = new Articles();
                    article.title = ariticleJson.getString("title");
                    article.publishedAt = ariticleJson.getString("publishedAt");
                    article.urlToImage = ariticleJson.getString("urlToImage");
                    article.description=ariticleJson.getString("description");

                    result.add(article);
                }
            }
        } catch (Exception e) {
            //Handle Exceptions
        } finally {
            //Close the connections
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(ArrayList<Articles> articles) {
        activity.setArticles(articles);
        for(int i=0;i<articles.size();i++)
            Log.d("object"+i, (articles.get(i)).toString());
        //Log.d("hello", "onPostExecute: ");
    }
}
