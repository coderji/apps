package com.ji.tree.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.File;

public class ImageUtils {
    public static void with(final ImageView imageView, final String address) {
        WorkUtils.workExecute(new Runnable() {
            @Override
            public void run() {
                final File file = InternetUtils.getFile(address);
                final Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                WorkUtils.uiExecute(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                    }
                });
            }
        });
    }
}
