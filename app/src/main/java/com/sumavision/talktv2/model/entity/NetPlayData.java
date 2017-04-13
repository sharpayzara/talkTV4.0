package com.sumavision.talktv2.model.entity;

/**
 * Created by sharpay on 16-6-22.
 */

import java.io.Serializable;


public class NetPlayData implements Serializable {
    /**
     * 点播列表信息记录
     */
    private static final long serialVersionUID = 1L;
    public String url;
    // 网页平台名字 例如 乐视 CNTV
    public String name;
    public String actor;
    public VideoType type;
    public String score; // 分数
    public String newestSelection;
    // 网页平台图片地址
    public String pic;
    public String topicId;
    public String videoPath;
    public String intro;
    public String channelName;
    // 给播放器或�?网页的title
    public String title;
    public int playType;
    public int platformId;
    public int dbposition;
    public int id;
    public long programChannelId;
    public int subid;
    public boolean isZan;
    public boolean isCai;
    public boolean isOpened;
    public boolean isChecked;
    public String isdownload = "0";// 0表示非缓存 1表示缓存
    public boolean needparse;
    public String channelIdStr;//p2p channel
    public String showUrl;
    public String webPage; //版权规避加载地址
}
