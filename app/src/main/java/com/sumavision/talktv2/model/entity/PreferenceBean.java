package com.sumavision.talktv2.model.entity;

import com.j256.ormlite.field.DatabaseField;
import com.sumavision.talktv2.model.entity.decor.BaseData;

/**
 * Created by sharpay on 16-7-20.
 */
public class PreferenceBean extends BaseData{
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField
    private String role = "";
    @DatabaseField
    private String preference = "";

    private String did;

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPreference() {
        return preference;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
