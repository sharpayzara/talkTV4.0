package com.sumavision.talktv2.model.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by zjx on 2016/7/7.
 */
@DatabaseTable(tableName = "CpData")
public class CpData{
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String cpname;
    @DatabaseField
    private String cpid;
    @DatabaseField
    private String picurl;

    public CpData() {
    }

    public CpData(String cpname, String cpid, String picurl) {
        this.cpname = cpname;
        this.cpid = cpid;
        this.picurl = picurl;
    }

    public String getCpname() {
        return cpname;
    }

    public void setCpname(String cpname) {
        this.cpname = cpname;
    }

    public String getCpid() {
        return cpid;
    }

    public void setCpid(String cpid) {
        this.cpid = cpid;
    }

    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }
}
