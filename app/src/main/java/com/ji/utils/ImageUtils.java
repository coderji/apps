package com.ji.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.File;

public class ImageUtils {
    private static final String TAG = "ImageUtils";
    private static LruCache<String, Bitmap> mBitmapLruCache;

    public static void create() {
        if (mBitmapLruCache == null) {
            int cacheMemory = (int) Runtime.getRuntime().maxMemory() / 1024 / 8;
            LogUtils.v(TAG, "create cacheMemory:" + cacheMemory);

            mBitmapLruCache = new LruCache<String, Bitmap>(cacheMemory) {
                @Override
                protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                    super.entryRemoved(evicted, key, oldValue, newValue);
                    LogUtils.v(TAG, "entryRemoved evicted:" + evicted + " key:" + key + " oldValue:" + oldValue + " newValue:" + newValue);
                }

                @Override
                protected int sizeOf(String key, Bitmap value) {
                    int size = value.getRowBytes() * value.getHeight() / 1024;
                    LogUtils.v(TAG, "sizeOf key:" + key + " value:" + value + " size:" + size);
                    return size;
                }
            };
        }
    }

    public static void destroy() {
        if (mBitmapLruCache != null && mBitmapLruCache.size() > 0) {
            mBitmapLruCache.evictAll();
        }
    }

    public static void with(final ImageView imageView, final String address) {
        with(imageView, address, 0, 0);
    }

    public static void with(final ImageView imageView, final String address, final int reqWidth, final int reqHeight) {
        LogUtils.v(TAG, "bindBitmap address:" + address + " reqWidth:" + reqWidth + " reqHeight:" + reqHeight);
        ThreadUtils.workExecute(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = mBitmapLruCache.get(address);
                if (bitmap == null) {
                    File file = DiskUtils.getFile(address, DiskUtils.getImageCacheDir());
                    if (file == null) {
                        file = InternetUtils.getFile(address, DiskUtils.getImageCacheDir());
                    }
                    if (file != null) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(file.getPath(), options);
                        options.inJustDecodeBounds = false;
                        options.inSampleSize = calculateInSampleSize(options.outWidth, options.outHeight, reqWidth, reqHeight);
                        bitmap = BitmapFactory.decodeFile(file.toString(), options);
                    }
                    if (bitmap != null) {
                        LogUtils.v(TAG, "bindBitmap put:" + address);
                        mBitmapLruCache.put(address, bitmap);
                    }
                }
                if (bitmap != null) {
                    final Bitmap fBitmap = bitmap;
                    ThreadUtils.uiExecute(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(fBitmap);
                        }
                    });
                }
            }
        });
    }

    private static int calculateInSampleSize(int width, int height, int reqWidth, int reqHeight) {
        int sampleSize = 1;
        if (reqWidth != 0 && reqHeight != 0) {
            while (width / sampleSize >= reqWidth && height / sampleSize >= reqHeight) {
                sampleSize = sampleSize * 2;
            }
        }
        return sampleSize;
    }
}
