package com.sumavision.talktv2.model.entity;

import com.sumavision.talktv2.model.entity.decor.BaseData;

import java.util.List;

/**
 * Created by sharpay on 16-6-20.
 */
public class ProgramDetail extends BaseData{

    private String actor;
    private String id;
    private String info;
    private String name;
    private String picurl;
    private String release;
    private String newestSelection;
    private String src;
    private String subType;
    private String tabStyle;
    private String total;
    private String type;
    private String zone;
    private String playCount;
    private List<CplistBean> cplist;
    private List<String> tabs;
    private String director;
    private String score;
    private String prompt;

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getPlayCount() {
        return playCount;
    }

    public void setPlayCount(String playCount) {
        this.playCount = playCount;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getTabStyle() {
        return tabStyle;
    }

    public void setTabStyle(String tabStyle) {
        this.tabStyle = tabStyle;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public List<CplistBean> getCplist() {
        return cplist;
    }

    public void setCplist(List<CplistBean> cplist) {
        this.cplist = cplist;
    }

    public List<String> getTabs() {
        return tabs;
    }

    public void setTabs(List<String> tabs) {
        this.tabs = tabs;
    }

    public static class CplistBean {
        private String cpid;
        private String cpname;

        public String getCpid() {
            return cpid;
        }

        public void setCpid(String cpid) {
            this.cpid = cpid;
        }

        public String getCpname() {
            return cpname;
        }

        public void setCpname(String cpname) {
            this.cpname = cpname;
        }
    }
    public CollectBean changeCollectBean(CollectBean bean){
        bean.setSid(id);
        bean.setName(name);
        bean.setActor(actor);
        bean.setScore(score);
        bean.setInfo(info);
        bean.setDirector(director);
        bean.setPicurl(picurl);
        bean.setRelease(release);
        bean.setSrc(src);
        bean.setSubType(subType);
        bean.setTabStyle(tabStyle);
        bean.setTotal(total);
        bean.setZone(zone);
        bean.setPlayCount(playCount);
        return  bean;
    }
}
