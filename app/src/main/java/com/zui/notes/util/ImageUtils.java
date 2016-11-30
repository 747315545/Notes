package com.zui.notes.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by huangfei on 2016/11/21.
 */

public class ImageUtils {
    public static Bitmap drawableToBitmap(Drawable paramDrawable) {
        int i = paramDrawable.getIntrinsicWidth();
        int j = paramDrawable.getIntrinsicHeight();
        Object localObject;
        if (paramDrawable.getOpacity() != PixelFormat.OPAQUE) {
            localObject = Bitmap.Config.ARGB_8888;
        } else
            localObject = Bitmap.Config.RGB_565;
        Bitmap localBitmap = Bitmap.createBitmap(i, j, (Bitmap.Config) localObject);
        localObject = new Canvas(localBitmap);
        paramDrawable.setBounds(0, 0, paramDrawable.getIntrinsicWidth(), paramDrawable.getIntrinsicHeight());
        paramDrawable.draw((Canvas) localObject);
        return localBitmap;
    }

    public static boolean SmallBitmap(String path, int relWidth, File file) {
        Bitmap bitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int width = options.outWidth;
        if (width > relWidth) {
            options.inSampleSize = Math.round((float) width / (float) relWidth);
        }
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(path, options);
        if (bitmap == null) {
            return false;
        } else {
            return compressImage(bitmap, file);
        }
    }

    private static boolean compressImage(Bitmap image, File file) {
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 60, out);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void deleteImagePath(String path) {
        File file1 = new File(path);
        if (file1.exists()) {
            File[] childFiles = file1.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file1.delete();
                return;
            }
            for (int i = 0; i < childFiles.length; i++) {
                childFiles[i].delete();
            }
            file1.delete();
        }
    }

}
