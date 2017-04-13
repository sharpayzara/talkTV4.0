package com.sumavision.talktv2.util;

/**
 * Created by sharpay on 16-9-13.
 */
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * 选择本地图片工具类
 * <br>
 * 因为直接获取图片容易崩溃，所以直接存入SD卡，再获取
 * <br>
 * 又因为写法不正确容易导致部分机型无法使用，所以封装起来复用
 * <br>
 * 使用方法：
 * <br>
 * 1、调用getByAlbum、getByCamera去获取图片
 * <br>
 * 2、在onActivityResult中调用本工具类的onActivityResult方法进行处理
 * <br>
 * 3、onActivityResult返回的Bitmap记得空指针判断
 *
 * <br><br>
 * PS：本工具类只能处理裁剪图片，如果不想裁剪，不使用本工具类的onActivityResult，自己做处理即可
 *
 *
 */
public class SelectPicUtil {

    /**
     * 临时存放图片的地址，如需修改，请记得创建该路径下的文件夹
     */
    // private static final String lsimg = "file:///sdcard/temp.jpg";
    public static Uri photoUri;
    public static File picFile;
    public static File compressFile;
    public static final int GET_BY_ALBUM = 801;
    public static final int GET_BY_CAMERA = 802;
    public static final int CROP = 803;
    public static Context context;
    public static boolean isCrop = false;

    public static void createPicFile() {
        isCrop = false;
        File pictureFileDir = new File(Environment.getExternalStorageDirectory(), "/.upload");
        if (!pictureFileDir.exists()) {
            pictureFileDir.mkdirs();
        } else {
            pictureFileDir.delete();
            pictureFileDir.mkdirs();
        }
        picFile = new File(pictureFileDir, "upload" + new Random().nextInt(900) + ".jpeg");
        if (!picFile.exists()) {
            try {
                picFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        photoUri = Uri.fromFile(picFile);
    }

    /**
     * 从相册获取图片
     */
    public static void getByAlbum(Activity act) {
        context = act;
        createPicFile();
        Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
        getAlbum.setType("image/*");
        act.startActivityForResult(getAlbum, GET_BY_ALBUM);
    }

    /**
     * 通过拍照获取图片
     */
    public static void getByCamera(Activity act) {
        context = act;
        createPicFile();
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent getImageByCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            getImageByCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            act.startActivityForResult(getImageByCamera, GET_BY_CAMERA);
        } else {
            Toast.makeText(act, "请确认已经插入SD卡", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 处理获取的图片，注意判断空指针，默认大小480*480，比例1:1
     */
    public static Bitmap onActivityResult(Activity act, int requestCode, int resultCode, Intent data) {
        return onActivityResult(act, requestCode, resultCode, data, 0, 0, 0, 0);
    }

    /**
     * 处理获取的图片，注意判断空指针
     */
    public static Bitmap onActivityResult(Activity act, int requestCode, int resultCode, Intent data,
                                          int w, int h, int aspectX, int aspectY) {
        Bitmap bm = null;
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            switch (requestCode) {
                case GET_BY_ALBUM:
                    uri = data.getData();
                    act.startActivityForResult(crop(uri, w, h, aspectX, aspectY), CROP);
                    break;
                case GET_BY_CAMERA:
                    act.startActivityForResult(crop(photoUri, w, h, aspectX, aspectY), CROP);
                    break;
                case CROP:
                    bm = dealCrop(act);
                    //  compressFile(photoUri);
                    recursionCompress(photoUri);
                    isCrop = true;
                    break;
            }
        }
        return bm;
    }

    public static void recursionCompress(Uri uri) {
        File file = null;
        try {
            file = new File(new URI(uri.toString()));
            if(file.exists()){
                double size = (double) file.length() / 1024 ;
                if(size >= 100){
                    compressFile(uri);
                }else{
                    compressFile = file;
                }
            }else{
                return;
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    /**
     * 默认裁剪输出480*480，比例1:1
     */
    public static Intent crop(Uri uri) {
        return crop(uri, 320, 320, 1, 1);
    }

    /**
     * 裁剪，例如：输出100*100大小的图片，宽高比例是1:1
     *
     * @param w       输出宽
     * @param h       输出高
     * @param aspectX 宽比例
     * @param aspectY 高比例
     */
    public static Intent crop(Uri uri, int w, int h, int aspectX, int aspectY) {
        if (w == 0 && h == 0) {
            w = h = 320;
        }
        if (aspectX == 0 && aspectY == 0) {
            aspectX = aspectY = 1;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        // 照片URL地址
        intent.setDataAndType(uri, "image/*");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            String url = getPath(context, uri);
            intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
        } else {
            intent.setDataAndType(uri, "image/*");
        }
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", w);
        intent.putExtra("outputY", h);
        // 输出路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        // 输出格式
        intent.putExtra("outputFormat", "JPEG");
        // 不启用人脸识别
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", false);
        return intent;
    }

    /**
     * 处理裁剪，获取裁剪后的图片
     */
    public static Bitmap dealCrop(Context context) {
        // 裁剪返回
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(photoUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    //以下是关键，原本uri返回的是file:///...来着的，android4.4返回的是content:///...
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider

        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static void compressFile(Uri uri) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            bitmap = BitmapFactory.decodeStream(context.getContentResolver()
                    .openInputStream(uri), null, options);
            int picWidth = options.outWidth;
            int picHeight = options.outHeight;
            WindowManager windowManager = ((Activity) context).getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            int screenWidth = display.getWidth();
            int screenHeight = display.getHeight();
            options.inSampleSize = 1;
            if (picWidth > picHeight) {
                if (picWidth > screenWidth)
                    options.inSampleSize = picWidth / screenWidth;
            } else {
                if (picHeight > screenHeight)
                    options.inSampleSize = picHeight / screenHeight;
            }
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeStream(context.getContentResolver()
                    .openInputStream(uri), null, options);
            saveCompressFile(bitmap);
   /*
    * if (bitmap.isRecycled() == false) { bitmap.recycle(); }
    */
            System.gc();
        } catch (Exception e1) {
        }

    }


    public static File saveCompressFile(Bitmap bm) {
        File pictureFileDir = new File(Environment.getExternalStorageDirectory(), "/.upload");
        compressFile = new File(pictureFileDir, "compress" + new Random().nextInt(900)+ ".jpeg");
        try {
            BufferedOutputStream bos = null;
            bos = new BufferedOutputStream(new FileOutputStream(compressFile));
            bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
            if (compressFile.length()/1024 > 100){
                recursionCompress(Uri.fromFile(compressFile));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return compressFile;
    }
}
