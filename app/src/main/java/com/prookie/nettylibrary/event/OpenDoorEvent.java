package com.prookie.nettylibrary.event;

import lombok.Data;

/**
 * OpenDoorEvent
 * Created by brin on 2018/7/10.
 */
@Data
public class OpenDoorEvent {

    private String id;
    private String type;

    public OpenDoorEvent(String id, String type) {
        this.id = id;
        this.type = type;
    }
}
