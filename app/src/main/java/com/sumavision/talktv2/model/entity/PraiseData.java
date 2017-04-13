package com.sumavision.talktv2.model.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 *  desc  点赞
 *  @author  yangjh
 *  created at  16-5-30 上午10:27
 */
@DatabaseTable(tableName = "praise_data")
public class PraiseData {
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField
    private String programId;
    @DatabaseField
    private Boolean isValid;
    @DatabaseField
    private Integer sdkType;
    @DatabaseField
    private Integer videoType;

    public Integer getId() {
        return id;
    }

    public Integer getSdkType() {
        return sdkType;
    }

    public void setSdkType(Integer sdkType) {
        this.sdkType = sdkType;
    }

    public Integer getVideoType() {
        return videoType;
    }

    public void setVideoType(Integer videoType) {
        this.videoType = videoType;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public Boolean getValid() {
        return isValid;
    }

    public void setValid(Boolean valid) {
        isValid = valid;
    }
}
