package com.sumavision.talktv2.ui.iview;

import com.sumavision.talktv2.model.entity.WXOrder;
import com.sumavision.talktv2.ui.iview.base.IBaseView;
/**
 *  desc  支付view
 *  @author  yangjh
 *  created at  16-9-2 下午2:26
 */
public interface IPayView extends IBaseView {
    void respWXOrder(WXOrder order);
}
