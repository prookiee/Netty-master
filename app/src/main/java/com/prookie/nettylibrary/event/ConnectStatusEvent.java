package com.prookie.nettylibrary.event;

import lombok.Data;

/**
 * ConnectStatusEvent
 * Created by brin on 2018/7/10.
 */
@Data
public class ConnectStatusEvent {

    public final static int CONNECT_CLOSED = 0;//连接失败
    public final static int CONNECT_SUCCESS = 1;//连接成功

    private int status;//连接状态

    public ConnectStatusEvent(int status) {
        this.status = status;
    }
}
