package com.sumavision.talktv2.ui.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.dao.RxDao;
import com.sumavision.talktv2.dao.ormlite.DbCallBack;
import com.sumavision.talktv2.model.entity.CollectBean;
import com.sumavision.talktv2.model.entity.PraiseData;
import com.sumavision.talktv2.model.entity.VideoType;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.CommonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 朋友圈点赞评论的popupwindow
 *
 * @author wei.yi
 *
 */
public class SpecialPopupWindow extends PopupWindow implements OnClickListener{
	private static RxDao collectDao = new RxDao(BaseApp.getContext(), CollectBean.class);
	Context mContext;
	private RelativeLayout collectRlt,shareRlt,praiseRlt;
	String idStr,picture,programName;
	CheckBox collectCheckBox,praiseCheckBox;
	PraiseData praiseData;
	// 坐标的位置（x、y）
	private final int[] mLocation = new int[2];
	public SpecialPopupWindow(Context context,String idStr,String picture,String programName,PraiseData praiseData) {
		this.idStr = idStr;
		this.picture = picture;
		this.programName = programName;
		this.praiseData = praiseData;
		View view = LayoutInflater.from(context).inflate(R.layout.social_sns_popupwindow, null);
		collectRlt = (RelativeLayout) view.findViewById(R.id.collect_rlt);
		shareRlt = (RelativeLayout) view.findViewById(R.id.share_rlt);
		praiseRlt = (RelativeLayout) view.findViewById(R.id.praise_rlt);
		collectCheckBox = (CheckBox) view.findViewById(R.id.collect_checkBox);
		praiseCheckBox = (CheckBox) view.findViewById(R.id.praise_checkBox);
		if(praiseData != null){
			praiseCheckBox.setChecked(praiseData.getValid());
		}
		findCollectBean();
		shareRlt.setOnClickListener(this);
		collectRlt.setOnClickListener(this);
		praiseRlt.setOnClickListener(this);
		mContext = context;
		this.setContentView(view);
		this.setWidth(CommonUtil.dip2px(context, 100));
		this.setHeight(CommonUtil.dip2px(context, 122));
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		this.update();
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0000000000);
		// 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
		this.setBackgroundDrawable(dw);
		//this.setAnimationStyle(R.style.more_anim);
		view.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				dismiss();
				return true;
			}
		});

	}

	public void showPopupWindow(View parent){
		parent.getLocationOnScreen(mLocation);
		if(!this.isShowing()){
			showAtLocation(parent, Gravity.NO_GRAVITY, mLocation[0] - this.getWidth() + CommonUtil.dip2px(mContext,50)
					, mLocation[1] + CommonUtil.dip2px(mContext,50));
		}else{
			dismiss();
		}
	}

	@Override
	public void onClick(View view) {
		dismiss();
		switch (view.getId()) {
			case R.id.collect_rlt:
				final CollectBean bean = new CollectBean();
				bean.setSid(idStr);
				bean.setName(programName);
				bean.setVideoType(VideoType.speicalLong);
				bean.setPicurl(picture);
				Map<String,String> map = new HashMap<>();
				map.put("sid",bean.getSid());
				collectDao.subscribe();
				collectDao.queryByConditionSync(map, new DbCallBack<List<CollectBean>>() {
					@Override
					public void onComplete(List<CollectBean> list) {
						if(list!=null && list.size() > 0){
							collectDao.deleteById(list.get(0).getId());
							collectCheckBox.setChecked(false);
							collectDao.unsubscribe();
						}else{
							collectDao.insertSync(bean, new DbCallBack<Boolean>() {
								@Override
								public void onComplete(Boolean bool) {
									if(bool){
										collectCheckBox.setChecked(true);
									}else{
										collectCheckBox.setChecked(false);
									}
									collectDao.unsubscribe();
								}
							});
						}

					}
				});

				break;
			case R.id.share_rlt:
				BusProvider.getInstance().post("openShare","");
				break;
			case R.id.praise_rlt:
				praiseClick();
				break;
			default:
				break;
		}
	}

	public void findCollectBean(){
		Map<String,String> map = new HashMap<>();
		map.put("sid",idStr);
		collectDao.subscribe();
		collectDao.queryByConditionSync(map, new DbCallBack<List<CollectBean>>() {
			@Override
			public void onComplete(List<CollectBean> list) {
				if(list != null && list.size() > 0){
					collectCheckBox.setChecked(true);
				}else{
					collectCheckBox.setChecked(false);
				}
				collectDao.unsubscribe();
			}
		});
	}

	public void praiseClick(){
		if(praiseData!=null){
			praiseData.setValid(praiseData.getValid()? false:true);
		}else{
			praiseData = new PraiseData();
			praiseData.setValid(true);
			praiseData.setProgramId(idStr);
		}
		praiseCheckBox.setChecked(praiseData.getValid());
		BusProvider.getInstance().post("savePraise",praiseData);
	}
}
