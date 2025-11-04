package com.example.truedone.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ImageUtils {
    public static Bitmap compressImageForGemini(Context context, Uri uri) throws Exception {
        InputStream input = context.getContentResolver().openInputStream(uri);
        Bitmap original = BitmapFactory.decodeStream(input);
        input.close();

        // Calculate aspect ratio to avoid squashing the image
        int originalWidth = original.getWidth();
        int originalHeight = original.getHeight();
        float ratio = (float) originalWidth / (float) originalHeight;

        int maxWidth = 800;
        int maxHeight = 800;
        int finalWidth = maxWidth;
        int finalHeight = maxHeight;

        if (ratio > 1) {
            // Landscape image
            finalWidth = maxWidth;
            finalHeight = (int) ((float) maxWidth / ratio);
        } else {
            // Portrait image
            finalHeight = maxHeight;
            finalWidth = (int) ((float) maxHeight * ratio);
        }

        return Bitmap.createScaledBitmap(original, finalWidth, finalHeight, true);
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }

    public static String saveBitmapToInternalStorage(Context context, Bitmap bitmap, String filename) {
        File file = new File(context.getFilesDir(), filename);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String copyUriToInternalStorage(Context context, Uri uri, String filename) {
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            File file = new File(context.getFilesDir(), filename);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            is.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
