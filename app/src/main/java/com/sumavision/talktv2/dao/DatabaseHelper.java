package com.sumavision.talktv2.dao;

import android.content.Context;

import com.sumavision.talktv2.dao.ormlite.OrmLiteDatabaseHelper;
import com.sumavision.talktv2.model.entity.ActionUrl;
import com.sumavision.talktv2.model.entity.CollectBean;
import com.sumavision.talktv2.model.entity.CpData;
import com.sumavision.talktv2.model.entity.HttpCache;
import com.sumavision.talktv2.model.entity.LiveCollectBean;
import com.sumavision.talktv2.model.entity.PlayerHistoryBean;
import com.sumavision.talktv2.model.entity.PraiseData;
import com.sumavision.talktv2.model.entity.PreferenceBean;

/**
 *  desc  ormlite操作数据库Helper
 *  @author  yangjh
 *  created at  16-5-24 下午5:31
 */
public class DatabaseHelper extends OrmLiteDatabaseHelper {

    /**
     * 数据库名称
     */
    private static final String DATABASE_NAME = "tvFan.db";

    /**
     * 数据库版本号
     */
    private static final int DATABASE_VERSION = 8;
    private static DatabaseHelper instance;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        addTable();
    }

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                if (instance == null) {
                    instance = new DatabaseHelper(context);
                }
            }
        }
        return instance;
    }

    /**
     * 注册数据表
     */
    private void addTable() {
        registerTable(HttpCache.class);
        registerTable(LiveCollectBean.class);
        registerTable(CollectBean.class);
        registerTable(PlayerHistoryBean.class);
        registerTable(CpData.class);
        registerTable(ActionUrl.class);
        registerTable(PraiseData.class);
        registerTable(PreferenceBean.class);
    }
}
