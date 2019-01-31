package com.example.annamihaleva.testparse;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.google.gson.Gson;
import org.java_websocket.client.WebSocketClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Parsing extends AsyncTask<Void, Void, Void> {

    private WebSocketClient client = null;
    private ProgressBar progressBar;
    private RecyclerView news;
    Context context;

    public static Response resp = new Response();
    List<Posts> partOfPosts = new ArrayList<>();
    AdapterNews adapter;

    Parsing(Context c, RecyclerView r) {
        news = r;
        context = c;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressBar = new ProgressBar(context,null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(Void... voids) {

        String urlAllPosts = "https://websuck1t.herokuapp.com/posts/all";
        Gson g = new Gson();

        resp = g.fromJson(getJSON(urlAllPosts), Response.class);

        for(int i = 0; resp != null && i < resp.response.size(); i++)
            resp.response.get(i).user = g.fromJson(getJSON("https://websuck1t.herokuapp.com/users/"
                    + resp.response.get(i).postAuthor), User.class);

        fillArray(0, 10);
        AdapterNews.allPosts = resp.response;

        try {
            if (client == null && resp != null)
                client = new Socket(
                    new URI("ws://websuck1t.herokuapp.com/posts/subscribe/" + resp.token));
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if (client != null)
            client.connect();

        adapter = new AdapterNews(context, partOfPosts, news);
        Socket.setAdapter(adapter);

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        progressBar.setVisibility(View.GONE);

        MainActivity.adapter = adapter;
        MainActivity.news.setAdapter(adapter);

        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_down);
        MainActivity.news.setLayoutAnimation(controller);
        MainActivity.news.getAdapter().notifyDataSetChanged();
        MainActivity.news.scheduleLayoutAnimation();

        MainActivity.loading = false;

        MainActivity.adapter.setLoadMore(() -> {
            if (adapter.getItemCount() < resp.response.size()) {
                partOfPosts.add(null);
                adapter.notifyItemInserted(partOfPosts.size() - 1);
                new Handler().postDelayed(() -> {
                    if (partOfPosts.size() > 0)
                        partOfPosts.remove(partOfPosts.size() - 1);
                    adapter.notifyItemRemoved(partOfPosts.size());

                    int start = partOfPosts.size();
                    int end = start + 5;
                    fillArray(start, end);
                    adapter.notifyDataSetChanged();
                    adapter.setLoaded();
                }, 1000);
            }
        });
    }

    private void fillArray(int start, int end){
        for (int i = start; i < end && resp != null && i < resp.response.size(); i++)
            partOfPosts.add(resp.response.get(i));
    }

    static String getJSON(String urlString) {
        URL url;
        HttpURLConnection connect;
        StringBuilder sb = new StringBuilder();
        String inputLine;
        BufferedReader in;

        try {
            url = new URL(urlString);
            connect = (HttpURLConnection) url.openConnection();

            InputStream is = connect.getInputStream();
            in = new BufferedReader(new InputStreamReader(is));
            sb = new StringBuilder();

            while ((inputLine = in.readLine()) != null)
                sb.append(inputLine);
            in.close();

        } catch (IOException e) {
            Log.d("connect", e.getMessage());
        }

        return sb.toString();
    }
}
