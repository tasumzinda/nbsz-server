package com.totalit.nbsz_server.business.domain.util;

import com.totalit.nbsz_server.business.util.StringUtils;

/**
 * Created by tasu on 5/11/17.
 */
public enum PassFail {

    PASS(1), FAIL(2);

    private final Integer code;

    PassFail(Integer code){
        this.code = code;
    }

    public static PassFail get(Integer code){
        switch (code){
            case 1:
                return PASS;
            case 2:
                return FAIL;
            default:
                throw new IllegalArgumentException("Parameter passed to method not recognized: " + code);
        }
    }

    public String getName(){
        return StringUtils.toCamelCase3(super.name());
    }
}
