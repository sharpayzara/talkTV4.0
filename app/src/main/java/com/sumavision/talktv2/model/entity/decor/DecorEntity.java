package com.sumavision.talktv2.model.entity.decor;

import java.io.Serializable;

/**
 * Created by sharpay on 16-5-31.
 */
public class DecorEntity<T> implements Serializable{
    public T t;

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }
}
