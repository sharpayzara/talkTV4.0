package com.sumavision.talktv2.model.entity;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by sharpay on 16-7-1.
 */
public class SerializableMap implements Serializable {

    private Map<String,String> map;

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }
}