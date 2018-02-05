package com.totalit.nbsz_server.business.domain.util;

import com.totalit.nbsz_server.business.util.StringUtils;

/**
 * Created by tasu on 5/4/17.
 */
public enum YesNo {

    YES(1), NO(2);

    private final Integer code;

    private YesNo(Integer code) {
        this.code = code;
    }

    public static YesNo get(Integer code) {
        switch (code) {
            case 1:
                return YES;
            case 2:
                return NO;
            default:
                throw new IllegalArgumentException("Illegal parameter passed to method :" + code);
        }
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return StringUtils.toCamelCase3(super.name());
    }

    @Override
    public String toString() {
        return getName();
    }
}
