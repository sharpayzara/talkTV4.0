package com.sumavision.talktv2.model;

import com.sumavision.talktv2.model.entity.PraiseData;
import com.sumavision.talktv2.model.entity.SeriesDetail;

import java.util.List;

/**
 * Created by sharpay on 16-6-20.
 */
public interface PraiseModel extends BaseModel {
    void saveLocalData(PraiseData praiseData);
    PraiseData loadPraiseData(String programId);
    void sendPraiseData(PraiseData praiseData);
}
