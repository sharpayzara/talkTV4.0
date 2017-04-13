package com.sumavision.talktv2.model;

import android.content.Context;

import com.sumavision.talktv2.model.entity.PlayerHistoryBean;
import com.sumavision.talktv2.model.entity.SeriesDetail;
import com.sumavision.talktv2.ui.listener.DownloadProgressListener;

import java.io.File;
import java.util.List;

/**
 * Created by sharpay on 16-6-20.
 */
public interface ShowGirlModel extends BaseModel {
    void getShowGirlTopicData(final CallBackListener listener);
    void getShowGirlListData(final CallBackListener listener,int partId,int start,int offset);
}
