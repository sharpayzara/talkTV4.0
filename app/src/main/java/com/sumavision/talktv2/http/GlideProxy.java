package com.sumavision.talktv2.http;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.ShareElement;
import com.sumavision.talktv2.ui.widget.GlideCircleTransform;


public class GlideProxy {

    public static final String ANDROID_RESOURCE = "android.resource://";
    public static final String FOREWARD_SLASH = "/";

    private GlideProxy() {
    }

    private static class GlideControlHolder {
        private static GlideProxy instance = new GlideProxy();
    }

    public static GlideProxy getInstance() {
        return GlideControlHolder.instance;
    }

    // 将资源ID转为Uri
    public Uri resourceIdToUri(Context context, int resourceId) {
        return Uri.parse(ANDROID_RESOURCE + context.getPackageName() + FOREWARD_SLASH + resourceId);
    }

    // 加载drawable图片
    public void loadResImage(Context context, int resId, ImageView imageView) {
        Glide.with(context)
                .load(resourceIdToUri(context, resId))
                .placeholder(R.mipmap.default_img_bg)
                .error(R.mipmap.default_img_bg)
                .crossFade()
                .into(imageView);
    }

    public void loadResImage2(Context context, int resId, ImageView imageView) {
        Glide.with(context)
                .load(resourceIdToUri(context, resId))
                .crossFade()
                .into(imageView);
    }

    // 加载drawable图片
    public void loadResImage(Context context, int resId, ImageView imageView, int defaultId) {
        Glide.with(context)
                .load(resourceIdToUri(context, resId))
                .placeholder(defaultId)
                .error(defaultId)
                .crossFade()
                .into(imageView);
    }

    // 加载本地图片
    public void loadLocalImage(Context context, String path, ImageView imageView) {
        Glide.with(context)
                .load("file://" + path)
                .placeholder(R.mipmap.default_img_bg)
                .error(R.mipmap.default_img_bg)
                .crossFade()
                .into(imageView);
    }
    // 加载网络图片
    public void loadImage(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .animate(R.anim.image_load)
                .placeholder(R.mipmap.head_sculpture)
                .error(R.mipmap.head_sculpture)
                .crossFade()
                .into(imageView);
    }

    // 加载乐视插件海报
    public void loadLetvImage(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .animate(R.anim.image_load)
                .placeholder(R.mipmap.play_bg)
                .error(R.mipmap.play_bg)
                .crossFade()
                .into(imageView);
    }

    // 加载直播广告图片
    public void loadLiveAd(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .crossFade()
                .into(imageView);
    }

    public void loadImage(Context context, String url, ImageView imageView,int id) {
        Glide.with(context)
                .load(url)
                .animate(R.anim.image_load)
                .placeholder(id)
                .error(id)
                .crossFade()
                .into(imageView);
    }
    // 加载网络图片
    public void loadVImage(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .animate(R.anim.image_load)
                .placeholder(R.mipmap.default_img_v)
                .error(R.mipmap.error_vertical)
                .crossFade()
                .into(imageView);
    }
    // 加载网络图片
    public void loadHImage(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .animate(R.anim.image_load)
                .placeholder(R.mipmap.default_img_bg)
                .error(R.mipmap.error_horizontal)
                .crossFade()
                .into(imageView);
    }


    // 加载网络圆型图片
    public void loadCircleImage(Context context, String url, ImageView imageView,int idColor) {
        Glide.with(context)
                .load(url)
               /* .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)*/
                .animate(R.anim.image_load)
                .placeholder(R.mipmap.head_sculpture)
                .error(R.mipmap.head_sculpture)
                .crossFade()
                .transform(new GlideCircleTransform(context,context.getResources().getColor(idColor)))
                .into(imageView);
    }

    // 加载drawable圆型图片
    public void loadCircleResImage(Context context, int resId, ImageView imageView) {
        Glide.with(context)
                .load(resourceIdToUri(context, resId))
                .placeholder(R.mipmap.head_sculpture)
                .error(R.mipmap.head_sculpture)
                .crossFade()
                .transform(new GlideCircleTransform(context))
                .into(imageView);
    }

    // 加载drawable圆型图片
    public void loadCircleResImage2(Context context, int resId, ImageView imageView, int idColor) {
        Glide.with(context)
                .load(resourceIdToUri(context, resId))
                .placeholder(R.mipmap.default_img_bg)
                .error(R.mipmap.default_img_bg)
                .crossFade()
                .transform(new GlideCircleTransform(context,context.getResources().getColor(idColor)))
                .into(imageView);
    }

    // 加载本地圆型图片
    public void loadCircleLocalImage(Context context, String path, ImageView imageView,int idColor) {
        Glide.with(context)
                .load( path)
                .placeholder(R.mipmap.default_img_bg)
                .error(R.mipmap.default_img_bg)
                .crossFade()
                .transform(new GlideCircleTransform(context,context.getResources().getColor(idColor)))
                .into(imageView);
    }

}
