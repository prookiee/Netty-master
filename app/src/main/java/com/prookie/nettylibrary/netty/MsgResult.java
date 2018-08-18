/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.prookie.nettylibrary.netty;

import lombok.Data;

/**
 * MsgResult
 * Created by brin on 2018/7/12.
 */
@Data
public class MsgResult {

    private String code;
    private Object result;
    private String message;


    public void setOk(Object o, String m) {
        result = o;
        code = "00";
        message = m;
    }

    public void setError(Object o, String c, String m) {
        result = o;
        code = c;
        message = m;
    }



}
