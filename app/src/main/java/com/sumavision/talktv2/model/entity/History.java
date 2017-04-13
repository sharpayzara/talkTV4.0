package com.sumavision.talktv2.model.entity;

/**
 * Created by sharpay on 16-6-22.
 */


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 记录播放历史
 * */
@DatabaseTable(tableName = "history")
public class History {
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField
    private String url;
    @DatabaseField
    private String name;
    @DatabaseField
    private int programId;
    @DatabaseField
    private int dbposition;
    @DatabaseField
    private String videoPath;
    @DatabaseField
    private String intro;
    @DatabaseField
    private int subid;
    @DatabaseField
    private int platformId;
    @DatabaseField
    private String isdownload = "0";// 0表示非缓存 1表示缓存
    @DatabaseField
    private String topicId;
    @DatabaseField
    private long timestamp;

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getTopicId() {
        return topicId;
    }
    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }
    public String getVideoPath() {
        return videoPath;
    }
    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }
    public String getIntro() {
        return intro;
    }
    public void setIntro(String intro) {
        this.intro = intro;
    }
    public int getPlatformId() {
        return platformId;
    }
    public void setPlatformId(int platformId) {
        this.platformId = platformId;
    }
    public int getDbposition() {
        return dbposition;
    }
    public void setDbposition(int dbposition) {
        this.dbposition = dbposition;
    }
    public int getSubid() {
        return subid;
    }
    public void setSubid(int subid) {
        this.subid = subid;
    }
    public String getIsdownload() {
        return isdownload;
    }
    public void setIsdownload(String isdownload) {
        this.isdownload = isdownload;
    }
    public int getProgramId() {
        return programId;
    }
    public void setProgramId(int programId) {
        this.programId = programId;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public static History getNetPlayDataBean(NetPlayData d, long time) {
        History bean = new History();
        bean.dbposition = d.dbposition;
        bean.programId = d.id;
        bean.intro = d.intro == null ? "" : d.intro;
        bean.isdownload = d.isdownload == null ? "" : d.isdownload;
        bean.name = d.name == null ? "" : d.name;
        bean.platformId = d.platformId;
        bean.subid = d.subid;
        bean.topicId = d.topicId == null ? "" : d.topicId;
        bean.url = d.url == null ? "" : d.url;
        bean.videoPath = d.videoPath == null ? "" : d.videoPath;
        bean.timestamp = time;
        return bean;
    }

    public static NetPlayData getNetPlayData(History bean) {
        NetPlayData d = new NetPlayData();
        d.dbposition = bean.dbposition;
        d.id = bean.programId;
        d.intro = bean.intro == null ? "" : bean.intro;
        d.isdownload = bean.isdownload == null ? "" : bean.isdownload;
        d.name = bean.name == null ? "" : bean.name;
        d.platformId = bean.platformId;
        d.subid = bean.subid;
        d.topicId = bean.topicId == null ? "" : bean.topicId;
        d.url = bean.url == null ? "" : bean.url;
        d.videoPath = bean.videoPath == null ? "" : bean.videoPath;
        return d;
    }

  /*  public static ContentValues getChangedValues(History d) {
        ContentValues values = new ContentValues();
        values.put("dbposition", d.dbposition);
        values.put("programId", d.programId);
        values.put("intro", d.intro);
        values.put("isdownload", d.isdownload);
        values.put("name", d.name);
        values.put("platformId", d.platformId);
        values.put("subid", d.subid);
        values.put("topicId", d.topicId);
        values.put("url", d.url);
        values.put("videoPath", d.videoPath);
        values.put("timestamp", d.timestamp);
        return values;
    }*/
}
