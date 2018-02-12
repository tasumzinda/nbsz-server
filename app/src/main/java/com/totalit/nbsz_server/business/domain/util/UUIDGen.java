package com.totalit.nbsz_server.business.domain.util;

import java.util.UUID;

/**
 * @uthor Tasu Muzinda
 */
public class UUIDGen {

    public static String generateUUID(){
        return UUID.randomUUID().toString();
    }
}
