package com.sumavision.talktv2.model;

import com.sumavision.talktv2.model.entity.WXOrder;

/**
 * Created by sharpay on 16-9-2.
 */
public interface PayModel extends BaseModel{
    void getWXOrder(final CallBackListener<WXOrder> listener);
}
