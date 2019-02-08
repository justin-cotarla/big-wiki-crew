package org.wikipedia.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public final class ImageUtil {
    private static final int OFFSET = 90;

    // Needed to fix pre-rotation of images from camera and gallery
    public static byte[] rotateImage(Bitmap bmp) {
        if (bmp == null) {
            return new byte[0];
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(OFFSET);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        return FileUtil.compressBmpToJpg(rotatedBitmap).toByteArray();
    }

    private ImageUtil() { }
}
