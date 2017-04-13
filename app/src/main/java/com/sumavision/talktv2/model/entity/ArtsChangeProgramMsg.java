package com.sumavision.talktv2.model.entity;

/**
 * Created by zjx on 2016/7/4.
 */
public class ArtsChangeProgramMsg {
    private int type;
    private int pso;


    public ArtsChangeProgramMsg(int type, int pso) {
        this.type = type;
        this.pso = pso;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPso() {
        return pso;
    }

    public void setPso(int pso) {
        this.pso = pso;
    }
}
