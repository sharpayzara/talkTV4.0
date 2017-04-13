package com.sumavision.talktv2.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.android.pushservice.PushMessageReceiver;
import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.http.LogRetrofit;
import com.sumavision.talktv2.http.SumaClient;
import com.sumavision.talktv2.ui.activity.MediaDetailActivity;
import com.sumavision.talktv2.ui.activity.ProgramDetailActivity;
import com.sumavision.talktv2.ui.activity.SpecialActivity;
import com.sumavision.talktv2.ui.activity.SpecialDetailActivity;
import com.sumavision.talktv2.ui.activity.TVFANActivity;
import com.sumavision.talktv2.ui.activity.WeBADActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * @author
 * @description 接收推送服务的receiver 主要接收 bind 和onMessage 信息
 */
public class MyBaiduPushMessageReceiver extends PushMessageReceiver {

    public static final String TAG = MyBaiduPushMessageReceiver.class
            .getSimpleName();

    /**
     * 调用PushManager.startWork后，sdk将对push
     * server发起绑定请求，这个过程是异步的。绑定请求的结果通过onBind返回。 如果您需要用单播推送，需要把这里获取的channel
     * id和user id上传到应用server中，再调用server接口用channel id和user id给单个手机或者用户推送。
     *
     * @param context   BroadcastReceiver的执行Context
     * @param errorCode 绑定接口返回值，0 - 成功
     * @param appid     应用id。errorCode非0时为null
     * @param userId    应用user id。errorCode非0时为null
     * @param channelId 应用channel id。errorCode非0时为null
     * @param requestId 向服务端发起的请求id。在追查问题时有用；
     * @return none
     */
    @Override
    public void onBind(final Context context, int errorCode, String appid,
                       final String userId, final String channelId, String requestId) {
        String responseString = "onBind errorCode=" + errorCode + " appid="
                + appid + " userId=" + userId + " channelId=" + channelId
                + " requestId=" + requestId;
        Log.e(TAG, responseString);

        // 绑定成功，设置已绑定flag，可以有效的减少不必要的绑定请求
        if (errorCode == 0) {
            String localUserId = PushUtils.getBindUserId(context);
            String localChannelId = PushUtils.getBindChannelId(context);
            if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(channelId)
                    && (!userId.equals(localUserId) || !channelId.equals(localChannelId))) {
                SumaClient.getRetrofitInstance(LogRetrofit.class, AppGlobalVars.logApiHost).baiduPushUpload(channelId, userId, AppUtil.getDeviceInfo(context), "0000");
                saveBind(context, userId, channelId);
            }
        }
    }

    private void saveBind(Context context, String userId, String channelId) {
        PushUtils.setBind(context, true, userId, channelId);
    }

//	UploadPushInfoParser uploadInfoparser;

    /**
     * 接收透传消息的函数。
     *
     * @param context             上下文
     * @param message             推送的消息
     * @param customContentString 自定义内容,为空或者json字符串
     */
    @Override
    public void onMessage(Context context, String message,
                          String customContentString) {
        if (TextUtils.isEmpty(message)) {
            message = "";
        }
        if (TextUtils.isEmpty(customContentString)) {
            customContentString = "";
        }
        String messageString = "透传消息 message=\"" + message
                + "\" customContentString=" + customContentString;
        Log.e(TAG, messageString);
        Object tmp = BaseApp.getACache().getAsObject("CLICKNUM_INFO");
        if (tmp != null && !(boolean) tmp) {
            return;
        }
        parseMessage(context, message);
    }

    /**
     * 接收通知点击的函数。注：推送通知被用户点击前，应用无法通过接口获取通知的内容。
     *
     * @param context             上下文
     * @param title               推送的通知的标题
     * @param description         推送的通知的描述
     * @param customContentString 自定义内容，为空或者json字符串
     */
    @Override
    public void onNotificationClicked(Context context, String title,
                                      String description, String customContentString) {
        String notifyString = "通知点击 title=\"" + title + "\" description=\""
                + description + "\" customContent=" + customContentString;
        Log.e(TAG, notifyString);
    }

    @Override
    public void onNotificationArrived(Context context, String s, String s1, String s2) {
        Log.e(TAG, "onNotificationArrived");
    }

    /**
     * setTags() 的回调函数。
     *
     * @param context     上下文
     * @param errorCode   错误码。0表示某些tag已经设置成功；非0表示所有tag的设置均失败。
     * @param successTags 设置成功的tag
     * @param failTags    设置失败的tag
     * @param requestId   分配给对云推送的请求的id
     */
    @Override
    public void onSetTags(Context context, int errorCode,
                          List<String> successTags, List<String> failTags, String requestId) {
        String responseString = "onSetTags errorCode=" + errorCode
                + " sucessTags=" + successTags + " failTags=" + failTags
                + " requestId=" + requestId;
        Log.d(TAG, responseString);

    }

    /**
     * delTags() 的回调函数。
     *
     * @param context     上下文
     * @param errorCode   错误码。0表示某些tag已经删除成功；非0表示所有tag均删除失败。
     * @param successTags 成功删除的tag
     * @param failTags    删除失败的tag
     * @param requestId   分配给对云推送的请求的id
     */
    @Override
    public void onDelTags(Context context, int errorCode,
                          List<String> successTags, List<String> failTags, String requestId) {
        String responseString = "onDelTags errorCode=" + errorCode
                + " sucessTags=" + successTags + " failTags=" + failTags
                + " requestId=" + requestId;
        Log.e(TAG, responseString);
    }

    /**
     * listTags() 的回调函数。
     *
     * @param context   上下文
     * @param errorCode 错误码。0表示列举tag成功；非0表示失败。
     * @param tags      当前应用设置的所有tag。
     * @param requestId 分配给对云推送的请求的id
     */
    @Override
    public void onListTags(Context context, int errorCode, List<String> tags,
                           String requestId) {
        String responseString = "onListTags errorCode=" + errorCode + " tags="
                + tags;
        Log.e(TAG, responseString);

    }

    /**
     * PushManager.stopWork() 的回调函数。
     *
     * @param context   上下文
     * @param errorCode 错误码。0表示从云推送解绑定成功；非0表示失败。
     * @param requestId 分配给对云推送的请求的id
     */
    @Override
    public void onUnbind(Context context, int errorCode, String requestId) {
        String responseString = "onUnbind errorCode=" + errorCode
                + " requestId = " + requestId;
        Log.e(TAG, responseString);

        // 解绑定成功，设置未绑定flag，
        if (errorCode == 0) {
            PushUtils.setBind(context, false, "", "");
        }
    }

    public void parseMessage(Context context, String message) {
        Log.e(TAG, message);
        if (!TextUtils.isEmpty(message)) {
            try {
                JSONObject root = new JSONObject(message);
                JSONObject pushObject = root.getJSONObject("push");
                String title = pushObject.optString("title");
                String text = pushObject.optString("text");
                JSONObject paramObject = pushObject.optJSONObject("param");
                if (paramObject == null) {
                    return;
                }
                int type = paramObject.optInt("type");
                if (type <= 0) {
                    return;
                }
                String code = paramObject.optString("code");
                int videoType = paramObject.optInt("videoType");
                int sdkType = paramObject.optInt("sdkType");
                Bundle bundle = new Bundle();
                bundle.putString("code", code);
                bundle.putInt("videoType", videoType);
                bundle.putInt("sdkType", sdkType);
                sendNotification(context, type, bundle, title, text, null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//			try {
//				JSONObject msgJson = new JSONObject(message);
//				JSONObject pushJson = msgJson.optJSONObject("push");
//				if (pushJson != null) {
//					String text = pushJson.optString("text");
//					String title = pushJson.optString("title");
//					String pic = pushJson.optString("pushPic");
//					JSONObject interactive = pushJson
//							.optJSONObject("interactive");
//					JSONObject concert = pushJson.optJSONObject("live");
//					JSONObject programObj = pushJson.optJSONObject("program");
//					long activityId = pushJson.optLong("activityId", 0);
//					int pushType = pushJson.optInt("type");
//					if (pushType ==1){
//						EventMessage msg = new EventMessage("FeedbackMailActivity");
//						EventBus.getDefault().post(msg);
//						sendNotification(context, NOTIFY_FEEDBACK_MESSAGE,
//								new Bundle(), title, text, pic);
//						return;
//					} else if (pushType ==2){
//						sendNotification(context, NOTIFY_DAY_LOTTERY,
//								new Bundle(), title, text, pic);
//					}
//					if (concert != null) {
//						long id = concert.optLong("id");
//						if (id != 0) {
//							Bundle bundle = new Bundle();
//							bundle.putLong("id", id);
//							sendNotification(context, NOTIFY_CONCERT,
//									bundle, title, text, pic);
//						}
//					}
//					if (interactive != null) {
//						long interactiveId = interactive.optLong(
//								"interactiveId", 0);
//						long zoneId = interactive.optLong("zoneId");
//						if (interactiveId != 0) {
//							Bundle bundle = new Bundle();
//							bundle.putLong("interactiveId", interactiveId);
//							if (zoneId != 0) {
//								bundle.putLong("zoneId", zoneId);
//							}
//							bundle.putBoolean("notice", true);
//							sendNotification(context, NOTIFY_INTERACTION,
//									bundle, title, text, pic);
//						}
//					}
//					long pProgramId = pushJson.optLong("programId",0);
//					if (pProgramId>0){
//						Bundle bundle = new Bundle();
//						bundle.putLong("id", pProgramId);
//						bundle.putBoolean("notice", true);
//						bundle.putInt("where", 3);
//						bundle.putInt("playType",2);
//						sendNotification(context, NOTIFY_PROGRAM, bundle,
//								title, text, pic);
//					}
//					if (programObj != null) {
//						Bundle bundle = new Bundle();
//						long programId = programObj.optLong("programId",0);
//						if (programId<=0){
//							return;
//						}
//						bundle.putLong("id", programId);
//						bundle.putBoolean("notice", true);
//						int subId = programObj.optInt("subId");
//						bundle.putInt("subid",subId);
//						if (subId>0){
//							bundle.putInt("where",4);
//						}else{
//							bundle.putInt("where",3);
//						}
//						bundle.putInt("playType",2);
//						sendNotification(context, NOTIFY_PROGRAM, bundle,
//								title, text, pic);
//					}
//					if (activityId != 0) {
//						Bundle bundle = new Bundle();
//						bundle.putLong("activityId", activityId);
//						bundle.putBoolean("notice", true);
//						sendNotification(context, NOTIFY_ACTIVITY, bundle,
//								title, text, pic);
//					}
//					JSONObject mailObj = pushJson.optJSONObject("mail");
//					SharedPreferences spUser = context.getSharedPreferences(
//							"userInfo", 0);
//					int userId = spUser.getInt("userID", 0);
//					if (mailObj != null) {
//						if (userId == 0) {
//							return;
//						}
//						int sid = mailObj.optInt("sendUserId");
//						String sUserName = mailObj.optString("sendUserName");
//						// String content = mailObj.optString("content");
//						Bundle bundle = new Bundle();
//						bundle.putString("otherUserName", sUserName);
//						bundle.putInt("otherUserId", sid);
//						SharedPreferences pushMsgPreferences = context
//								.getSharedPreferences(Constants.pushMessage, 0);
//						Editor edit = pushMsgPreferences.edit();
//						edit.putBoolean(Constants.key_privateMsg, true);
//						StringBuffer singleKey = new StringBuffer(
//								Constants.key_privateMsg);
//						singleKey.append("_").append(sid).append("-")
//								.append(userId);
//						edit.putBoolean(singleKey.toString(), true);
//						edit.commit();
//						if (AppUtil.getTopActivity(context).contains(
//								UserMailActivity.class.getSimpleName())) {
//							return;
//						}
//						sendNotification(context, NOTIFY_MESSAGE, bundle,
//								title, text, pic);
//					}
//					JSONObject fensiObj = pushJson.optJSONObject("fensi");
//					JSONObject replyObj = pushJson.optJSONObject("reply");
//					JSONObject discovery = pushJson.optJSONObject("discovery");
//					if (discovery != null) {
//						// 发现对象类型objectType=1,活动类型，objectType=2，推荐物品类型
//						@SuppressWarnings("unused")
//						int type = discovery.optInt("objectType");
//						// 发现对象ID：objectType=1,活动ID，objectType=2，推荐物品ID
//						@SuppressWarnings("unused")
//						int id = discovery.optInt("objectId");
//						PreferencesUtils.putBoolean(context,
//								Constants.pushMessage, Constants.KEY_FOUND,
//								true);
//						if (type == 1){
//							PreferencesUtils.putBoolean(context,
//									Constants.pushMessage, Constants.KEY_ACTIVITY,
//									true);
//						}else if(type == 2){
//							PreferencesUtils.putBoolean(context,
//									Constants.pushMessage, Constants.KEY_GOODS,
//									true);
//						}
//					}
//					if (fensiObj != null || replyObj != null) {
//						if (userId == 0) {
//							return;
//						}
//						PreferencesUtils.putBoolean(context,
//								Constants.pushMessage,
//								Constants.KEY_USER_CENTER, true);
//						if (fensiObj != null) {
//							PreferencesUtils.putBoolean(context,
//									Constants.pushMessage, Constants.key_fans,
//									true);
//						}
//						if (replyObj != null) {
//							PreferencesUtils.putBoolean(context,
//									Constants.pushMessage, Constants.key_reply,
//									true);
//							PreferencesUtils.putBoolean(context,
//									Constants.pushMessage,
//									Constants.key_user_comment, true);
//						}
//
//					}
//					JSONObject favUpdate = pushJson
//							.optJSONObject("programUpdate");
//					if (favUpdate != null) {
//						if (userId == 0) {
//							return;
//						}
//						if (TextUtils.isEmpty(text)) {
//							text = "您收藏的节目有更新咯";
//						}
//						long pid = favUpdate.optLong("programId");
//						PreferencesUtils.putBoolean(context,
//								Constants.pushMessage, Constants.key_favourite,
//								true);
//						Bundle bundle = new Bundle();
//						bundle.putLong("id", pid);
//						bundle.putBoolean("isHalf", true);
//						sendNotification(context, NOTIFY_FAV, bundle, title,
//								text, pic);
//					}
//						JSONObject shakeObj = pushJson.optJSONObject("shake");
//						if (shakeObj != null){
//							sendNotification(context,NOTIFY_SHAKE,new Bundle(),title,text,pic);
//						}
//					JSONObject channelObj = pushJson.optJSONObject("channel");
//					if (channelObj != null){
//						Bundle bundle = new Bundle();
//						bundle.putInt("channelId", (int) channelObj.optLong("id"));
//						JSONArray urls = channelObj.optJSONArray("play");
//						if (urls != null && urls.length()>0){
//							bundle.putString("url", urls.optJSONObject(0).optString("url"));
//							bundle.putString("p2pChannel", TextUtils.isEmpty(urls.optJSONObject(0).optString("channelIdStr")) ? "" : urls.optJSONObject(0).optString("channelIdStr"));
//							bundle.putInt("playType", 1);
//							bundle.putString("title", TextUtils.isEmpty(channelObj.optString("name")) ? "电视直播"
//									: channelObj.optString("name"));
//							ArrayList<NetPlayData> playList = new ArrayList<NetPlayData>();
//							for (int i=0; i<urls.length();i++){
//								NetPlayData temp = new NetPlayData();
//								JSONObject obj = urls.optJSONObject(i);
//								temp.url = obj.optString("url");
//								temp.videoPath = obj.optString("videoPath");
//								temp.channelIdStr = obj.optString("channelIdStr");
//								temp.showUrl = obj.optString("showUrl");
//								playList.add(temp);
//							}
//							bundle.putSerializable("NetPlayData", playList);
//							sendNotification(context, NOTIFY_LIVE_CHANNEL, bundle, title, text, pic);
//						}
//					}
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
        }
    }

    private static final int NOTIFY_LIVE_CHANNEL = 1;
    private static final int NOTIFY_PROGRAM = 2;
    private static final int NOTIFY_MEDIA = 3; // 自媒体
    private static final int NOTIFY_ACTIVITY_DUIBA = 4; // 兑吧
    private static final int NOTIFY_SPECIAL_LONG = 5; // 长视频专题
    private static final int NOTIFY_SPECIAL_SHORT = 6; // 短视频专题
    private static final int NOTIFY_ACTIVITY = 7; // web活动

    private void sendNotification(final Context context, int type,
                                  Bundle bundle, String title, final String text, String pic) {
        Intent notificationIntent = new Intent();
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        Class<?> mActivity = null;
        switch (type) {
            case NOTIFY_LIVE_CHANNEL:
                mActivity = TVFANActivity.class;
                notificationIntent.putExtra("liveChannelId", bundle.getString("code"));
                break;
            case NOTIFY_PROGRAM:
                mActivity = ProgramDetailActivity.class;
                notificationIntent.putExtra("idStr", bundle.getString("code"));
                break;
            case NOTIFY_MEDIA:
                mActivity = MediaDetailActivity.class;
                notificationIntent.putExtra("vid", bundle.getString("code"));
                notificationIntent.putExtra("videoType", bundle.getInt("videoType"));
                notificationIntent.putExtra("sdkType", bundle.getInt("sdkType"));
                break;
            case NOTIFY_ACTIVITY_DUIBA://跳转到兑吧活动页面
                mActivity = TVFANActivity.class;
                notificationIntent.putExtra("url", bundle.getString("code"));
                break;
            case NOTIFY_SPECIAL_LONG://跳转到长视频专题
                mActivity = SpecialActivity.class;
                notificationIntent.putExtra("idStr", bundle.getString("code"));
                break;
            case NOTIFY_SPECIAL_SHORT://跳转到短视频专题
                mActivity = SpecialDetailActivity.class;
                notificationIntent.putExtra("idStr", bundle.getString("code"));
                break;
            case NOTIFY_ACTIVITY : // web活动:
                mActivity = WeBADActivity.class;
                notificationIntent.putExtra("url", bundle.getString("code"));
                break;
            default:
                break;
        }
        stackBuilder.addParentStack(mActivity);
        notificationIntent.setClass(context, mActivity);
        stackBuilder.addNextIntent(notificationIntent);
        notificationIntent.putExtras(bundle);
        if (TextUtils.isEmpty(title)) {
            title = "电视粉温馨提示";
        }
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
//                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//        PendingIntent contentIntent;
//        if (type == NOTIFY_PROGRAM || type == NOTIFY_MEDIA || type == NOTIFY_SPECIAL_LONG || type == NOTIFY_SPECIAL_SHORT ||type == NOTIFY_ACTIVITY ) {
//            contentIntent  = stackBuilder.getPendingIntent(0,
//                    PendingIntent.FLAG_UPDATE_CURRENT);
//        } else {
//            contentIntent = PendingIntent.getActivity(context, type, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        }

        PendingIntent contentIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        if (type == NOTIFY_PROGRAM || type == NOTIFY_MEDIA || type == NOTIFY_SPECIAL_LONG || type == NOTIFY_SPECIAL_SHORT ||type == NOTIFY_ACTIVITY ) {
            //跳转到非主体activity必须在这里声明一下
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        } else {
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            contentIntent = PendingIntent.getActivity(context, type, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setTicker(title).setSmallIcon(R.mipmap.app_icon)
                .setAutoCancel(true).setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentTitle(title).setContentText(text)
                .setContentIntent(contentIntent);

       /* if (android.os.Build.VERSION.SDK_INT >= 16 && !TextUtils.isEmpty(pic)) {
//			StringBuilder picPath = new StringBuilder(Constants.picUrlFor + pic
//					+ Constants.PIC_SUFF);
//			ImageLoader imageLoader = ImageLoader.getInstance();
//			imageLoader.loadImage(picPath.toString(),
//					new ImageLoadingListener() {
//						@Override
//						public void onLoadingStarted(String imageUri, View view) {
//						}
//
//						@Override
//						public void onLoadingFailed(String imageUri, View view,
//													FailReason failReason) {
//						}
//
//						@Override
//						public void onLoadingComplete(String imageUri,
//													  View view, Bitmap loadedImage) {
//							NotificationCompat.BigPictureStyle pictureStyle = new NotificationCompat.BigPictureStyle();
//							pictureStyle.bigPicture(loadedImage);
//							pictureStyle.setSummaryText(text);
//							mBuilder.setStyle(pictureStyle);
//							NotificationManager notificationManager = (NotificationManager) context
//									.getSystemService(Context.NOTIFICATION_SERVICE);
//							notificationManager.notify(NOTIFICATION_ID,
//									mBuilder.build());
//						}
//
//						@Override
//						public void onLoadingCancelled(String imageUri,
//								View view) {
//						}
//					});
        }*/

        Notification notification = mBuilder.build();

        // TODO 小米
//		MiUitils.addDesktopCornerMark(notification);

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public static final int NOTIFICATION_ID = 2001;

}
