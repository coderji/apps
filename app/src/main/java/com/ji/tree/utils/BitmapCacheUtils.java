package com.ji.tree.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.File;

public class BitmapCacheUtils {
    private static final String TAG = "BitmapCacheUtils";
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

    private static int calculateInSampleSize(int width, int height, int reqWidth, int reqHeight) {
        int sampleSize = 1;
        if (reqWidth != 0 && reqHeight != 0) {
            while (width / sampleSize >= reqWidth && height / sampleSize >= reqHeight) {
                sampleSize = sampleSize * 2;
            }
        }
        return sampleSize;
    }

    public static void bindBitmap(final ImageView imageView, final String path, final int reqWidth, final int reqHeight) {
        LogUtils.v(TAG, "bindBitmap path:" + path + " reqWidth:" + reqWidth + " reqHeight:" + reqHeight);
        WorkUtils.workExecute(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = mBitmapLruCache.get(path);
                if (bitmap == null) {
                    File file = DiskCacheUtils.getFile(imageView.getContext(), path);
                    if (file == null) {
                        file = InternetUtils.getFile(path);
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
                        LogUtils.v(TAG, "bindBitmap put:" + path);
                        mBitmapLruCache.put(path, bitmap);
                    }
                }
                if (bitmap != null) {
                    final Bitmap fBitmap = bitmap;
                    imageView.post(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(fBitmap);
                        }
                    });
                }
            }
        });
    }
}
