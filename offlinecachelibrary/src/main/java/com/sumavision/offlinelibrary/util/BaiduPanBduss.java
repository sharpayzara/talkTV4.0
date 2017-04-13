//package com.sumavision.offlinelibrary.util;
//
//import java.util.List;
//import java.util.concurrent.CopyOnWriteArrayList;
//import java.util.concurrent.CountDownLatch;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.content.Context;
//import android.util.Log;
//
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response.ErrorListener;
//import com.android.volley.Response.Listener;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.Volley;
//
//public class BaiduPanBduss {
//	private static final String TAG = "BaiduPanBduss";
//	private static final int TRY_TIME = 3;
//	public static List<String> bdussList = new CopyOnWriteArrayList<String>();
//	private static Context context;
//	private static String url;
//	private int tryTime = 0; // 网络访问失败时重试次数
//	private CountDownLatch latch = null; // 用于边看边缓过程中，重新初始化
//
//	public BaiduPanBduss(CountDownLatch latch) {
//		this.latch = latch;
//	}
//
//	public BaiduPanBduss(Context context, String url) {
//		BaiduPanBduss.context = context;
//		BaiduPanBduss.url = url;
//	}
//
//	Listener<JSONObject> listener = new Listener<JSONObject>() {
//
//		@Override
//		public void onResponse(JSONObject response) {
//			Log.i(TAG, "onResponse");
//			try {
//				JSONObject content = response.getJSONObject("content");
//				JSONArray bduss = content.getJSONArray("bduss");
//				for (int i = 0; i < bduss.length(); i++) {
//					JSONObject tmp = bduss.getJSONObject(i);
//					String code = tmp.optString("code");
//					Log.i(TAG, code);
//					if (!bdussList.contains(code)) {
//						bdussList.add(code);
//					}
//				}
//				Log.i(TAG, "get bduss over");
//				if (latch != null) {
//					latch.countDown();
//					Log.i(TAG, "count down the latch");
//				}
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}
//	};
//
//	ErrorListener errorListener = new ErrorListener() {
//
//		@Override
//		public void onErrorResponse(VolleyError error) {
//			Log.i(TAG, "onErrorResponse" + error);
//			if (tryTime < TRY_TIME) {
//				tryTime++;
//				init();
//			}
//		}
//	};
//
//	public void init() {
//		if (bdussList.size() == 0) {
//			RequestQueue requestQueue = Volley.newRequestQueue(context);
//			JSONObject jsonObject = new JSONObject();
//			try {
//				jsonObject.put("method", "bdussList");
//				jsonObject.put("version", "3.1.1");
//				jsonObject.put("client", 1);
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//
//			Request<JSONObject> request = new JsonObjectRequest(url,
//					jsonObject, listener, errorListener);
//
//			requestQueue.add(request);
//		}
//	}
//}
