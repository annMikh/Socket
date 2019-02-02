package com.example.annamihaleva.testparse;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static com.example.annamihaleva.testparse.Parsing.getJSON;
import static com.example.annamihaleva.testparse.Parsing.resp;


public class AdapterNews extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context cont;
    ILoadMore loadMore;
    boolean isLoading;
    RecyclerView rec;

    List<Posts> partOfPosts;
    static List<Posts> allPosts = new ArrayList<>();
    HashMap<Integer, Posts> map = new HashMap<>();

    int visibleThreshold = 5;
    int lastVisibleItem, totalItemCount;
    private final int VIEW_TYPE_ITEM = 0, VIEW_TYPE_LOADING = 1;


    public AdapterNews(Context context, List<Posts> p, RecyclerView r){

        partOfPosts = p;
        rec = r;
        cont = context;

        for (int i = 0; i < resp.response.size(); ++i)
            map.put(resp.response.get(i).postId, resp.response.get(i));

        final LinearLayoutManager linearLayoutManager
                = (LinearLayoutManager) rec.getLayoutManager();

        rec.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {

                    if (loadMore != null)
                        loadMore.onLoadMore();

                    isLoading = true;
                }
            }
        });
    }

    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;

        if (i == VIEW_TYPE_ITEM) {
            view = LayoutInflater.from(cont).inflate(R.layout.activity_post, viewGroup, false);
            return new PostsViewHolder(view);
        }
        else {
            view = LayoutInflater.from(cont).inflate(R.layout.item_loading, viewGroup, false);
            return new LoadingViewHolder(view);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder postsViewHolder, int i) {

        if (postsViewHolder instanceof PostsViewHolder) {

            PostsViewHolder viewHolder = (PostsViewHolder) postsViewHolder;

            viewHolder.postBody.setText(partOfPosts.get(i).postBody);
            viewHolder.date.setText(String.valueOf(partOfPosts.get(i).postId));

            viewHolder.userName.setText(String.valueOf(partOfPosts.get(i).user.userName.firstName + " "
                    + partOfPosts.get(i).user.userName.secondName));

            viewHolder.userPhoto.setImageResource(R.color.colorPrimary);

        } else if (postsViewHolder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingHolder = (LoadingViewHolder) postsViewHolder;
            loadingHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() { return partOfPosts.size(); }

    @Override
    public int getItemViewType(int position) {
        return partOfPosts.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setLoadMore(ILoadMore loadMore) {
        this.loadMore = loadMore;
    }

    public void setLoaded() {
        isLoading = false;
    }


    public class PostsViewHolder extends RecyclerView.ViewHolder {

        TextView userName;
        TextView date;
        TextView postBody;
        TextView showFull;
        ImageView userPhoto;
        ConstraintLayout itemNews;

        PostsViewHolder(final View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.usernameNews);
            date = itemView.findViewById(R.id.dateNews);
            postBody = itemView.findViewById(R.id.post);
            userPhoto = itemView.findViewById(R.id.userphotoNews);
            itemNews = itemView.findViewById(R.id.itemNews);
            //showFull = itemView.findViewById(R.id.showPost);
        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {

        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    public void clear() {
        map.clear();
        partOfPosts.clear();
        allPosts.clear();
        notifyDataSetChanged();
    }

    public synchronized void removeAt(int id) {

        if (map.get(id) != null) {
            int p = partOfPosts.indexOf(map.get(id));
            allPosts.remove(partOfPosts.get(p));

            if (partOfPosts.size() > p) {
                partOfPosts.remove(map.get(id));
                notifyItemRemoved(p);
                notifyItemChanged(p);
            }
             notifyDataSetChanged();

            for(int i = 0; i < allPosts.size(); ++i)
                map.put(allPosts.get(i).postId, allPosts.get(i));
        }
    }

    public synchronized void addPost(List<Integer> id){

        Gson g = new Gson();
        Posts post;

        for (int i = 0; i < id.size(); ++i) {
            post = g.fromJson(getJSON("https://websuck1t.herokuapp.com/posts/" + id.get(i)),
                    Posts.class);

            post.user = g.fromJson(getJSON("https://websuck1t.herokuapp.com/users/"
                    + post.postAuthor), User.class);

            allPosts.add(0, post);
            partOfPosts.add(0, post);

            notifyItemInserted(0);
            notifyItemChanged(0);
        }

        notifyDataSetChanged();
        for(int i = 0; i < allPosts.size(); ++i)
            map.put(allPosts.get(i).postId, allPosts.get(i));

    }
}