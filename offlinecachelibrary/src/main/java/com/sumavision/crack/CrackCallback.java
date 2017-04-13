package com.sumavision.crack;

import java.util.HashMap;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.sumavision.offlinelibrary.entity.VideoFormat;

import crack.cracker.JarCracker;
import crack.listener.JarCrackCompleteListener;
import crack.util.CrackRequest;

/**
 * 破解回调
 *
 * @author suma-hpb
 */
public class CrackCallback implements JarCrackCompleteListener {
    protected static final String TAG = "CrackCallback";
    protected static final int TRY_TIME = 3;
    Context mContext;
    OnCrackCompleteListener listener;
    private String definition;
    private String url;

    public CrackCallback(Context c) {
        mContext = c;
    }

    /**
     * @param url 待破解url
     */
    public void parse(String url, String definition) {
        this.url = url;
        this.definition = definition;
        String tmp = CrackRequest.getRequestString(url, 1);
        JarCracker.getInstance().crack(mContext, url,
                tmp, 1, this);
//		if (url.contains("iqiyi")) {
//			if (checkJar(mContext)) {
//				loadJar(mContext, url);
//			} else {
//				new CrackJarInit(mContext).init(0);
//			}
//		} else {
//			new GetCrackResult(url, mContext, this).crack(1);
//		}
    }

//    private boolean checkJar(Context ct) {
//
//        File jarFile = new File(ct.getFilesDir().getPath() + "/"
//                + "Dynamic_temp.jar");
//        if (jarFile.exists()) {
//            return true;
//        }
//        return false;
//
//    }
//
//    @SuppressLint("NewApi")
//    private void loadJar(Context ct, String url) {
//
//        File jarFile = new File(ct.getFilesDir().getPath() + "/"
//                + "Dynamic_temp.jar");
//        DexClassLoader dcl = new DexClassLoader(jarFile.toString(), ct
//                .getFilesDir().getPath(), null, this.getClass()
//                .getClassLoader());
//        try {
//            Class<?> c = dcl
//                    .loadClass("com.sumavision.crack.interfacesImp.TestLoader");
//            ILoader tl = (ILoader) c.newInstance();
//            tl.crack(url, new CrackListener() {
//
//                @Override
//                public void start() {
//
//                }
//
//                @Override
//                public HashMap<String, String> end(
//                        HashMap<String, String> result) {
//                    HashMap<String, String> map = result;
//                    getResult(map, 1);
//                    return null;
//                }
//            });
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (java.lang.InstantiationException e) {
//            e.printStackTrace();
//        }
//
//    }

    public void getResult(HashMap<String, String> map, int i) {

        String standUrl = map.get("standardDef");
        String highUrl = map.get("hightDef");
        String superUrl = map.get("superDef");
        String type = map.get("videoType");
        int videoFormat = VideoFormat.UNKNOW_FORMAT;
        if (!TextUtils.isEmpty(type)) {
            if (type.contains("m3u8")) {
                videoFormat = VideoFormat.M3U8_FORMAT;
            } else if (type.contains("mp4") || type.contains("flv")) {
                videoFormat = VideoFormat.MP4_FORMAT;
            }
        }

        String parseUrl = "";
        if (!TextUtils.isEmpty(definition)
                && !TextUtils.isEmpty(map.get(definition))) {
            parseUrl = map.get(definition);
        } else {
            /*
             * 1.如果存在高清，下载高清
			 * 
			 * 2.如果没有高清，下载标清
			 * 
			 * 3.如果没有高清和标清，下载超清
			 */

            if (!TextUtils.isEmpty(highUrl)) {
                parseUrl = highUrl;
            } else if (!TextUtils.isEmpty(standUrl)) {
                parseUrl = standUrl;
            } else {
                parseUrl = superUrl;
            }
        }
        if (TextUtils.isEmpty(parseUrl)) {
            parseUrl = "";
        }
        if (listener != null) {
            listener.OnCrackComplete(parseUrl, videoFormat);
        }

    }

    public void cancelCallback() {
        listener = null;
    }

    public void setListener(OnCrackCompleteListener listener) {
        this.listener = listener;
    }

//	@Override
//	public void getCrackResultOver(HashMap<String, String> map) {
//		getResult(map, 1);
//	}

    @Override
    public void onJarCrackComplete(HashMap<String, String> map) {
        Log.i(TAG, "crack result:" + map);
        if (!TextUtils.isEmpty(map.get("videoType")) && map.get("videoType").equals("complete")) {
            parse(url, definition);
        } else {
            getResult(map, 1);
        }
    }

    @Override
    public void onCrackFailed(HashMap<String, String> map) {
//        if (listener != null) {
//            listener.OnCrackComplete("", VideoFormat.UNKNOW_FORMAT);
//        }
    }

    @Override
    public void onJarDownLoading(int process) {

    }
}
