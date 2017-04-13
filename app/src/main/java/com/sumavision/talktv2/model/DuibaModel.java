package com.sumavision.talktv2.model;

import com.sumavision.talktv2.model.entity.LoginAddressData;
import com.sumavision.talktv2.model.entity.UserIntegeInfoItem;

/**
 * Created by sharpay on 16-5-26.
 */
public interface DuibaModel extends BaseModel{
      void getUserIntegtion(CallBackListener<UserIntegeInfoItem> listener, String userId);
      void loadLoginAddress(CallBackListener<LoginAddressData> listener, String userId, String credits, String currentUrl);
}
