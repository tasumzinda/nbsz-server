package com.totalit.nbsz_server.util;

import android.util.Log;
import fi.iki.elonen.NanoHTTPD;

import java.util.Arrays;
import java.util.Map;

/**
 * @uthor Tasu Muzinda
 */
public class Server extends NanoHTTPD {

    public Server(int port){
        super(port);
    }

    public Server(String hostName, int port){
        super(hostName, port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        //String msg = "<html><body><h1>Hello server</h1>\n";
        Map<String, String> uri = session.getParms();
        Log.d("Result", Arrays.asList(uri) + "");
        /*Log.d("Uri", uri.toString());
        Map<String, String> parms = session.getParms();
        if (parms.get("username") == null) {
            msg += "<form action='?' method='get'>\n";
            msg += "<p>Your name: <input type='text' name='username'></p>\n";
            msg += "</form>\n";
        } else {
            msg += "<p>Hello, " + parms.get("username") + "!</p>";
        }*/
        return newFixedLengthResponse("Result");
    }
}
