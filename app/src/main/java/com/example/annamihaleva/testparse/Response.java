package com.example.annamihaleva.testparse;

import java.util.List;

public class Response {

    public List<Posts> response;
    int token;
}

class Posts {

    public String postBody;
    public int postAuthor;
    public int postId;

    public User user;
}

class ResponsePosts {

    UserPosts response;
    int token;
}

class UserPosts {
    public List<Integer> deleted;
    public List<Integer> added;
}

