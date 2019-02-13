package com.example.annamihaleva.testparse;

import io.socket.client.IO;

import static com.example.annamihaleva.testparse.Parsing.resp;
//
//class Socket {
//
//    MessageSocket mesResponse;
//    static AdapterNews adapter;
//
//    static void setAdapter(AdapterNews news){
//        adapter = news;
//    }
//
//    Socket(URI serverUri) {
//        super(serverUri);
//    }
//
//    @Override
//    public void onOpen(ServerHandshake handshakedata) {
//
//    }
//
//    @Override
//    public void onMessage(String message) {
//        Gson g = new Gson();
//        mesResponse = g.fromJson(message, MessageSocket.class);
//
//        if (mesResponse != null) {
//            resp.token = mesResponse.token;
//
//            if (mesResponse.response.deleted != null)
//                for (int i = 0; i < mesResponse.response.deleted.size(); ++i)
//                    adapter.removeAt(mesResponse.response.deleted.get(i));
//
//            if (mesResponse.response.added != null)
//                adapter.addPost(mesResponse.response.added);
//        }
//    }
//
//    @Override
//    public void onClose(int code, String reason, boolean remote) {
//        this.close();
//    }
//
//    @Override
//    public void onError(Exception ex) {
//
//    }
//
//}
