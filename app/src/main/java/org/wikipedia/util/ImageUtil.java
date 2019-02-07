package org.wikipedia.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public final class ImageUtil {
    public static byte[] rotateImage(Bitmap bmp) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        return FileUtil.compressBmpToJpg(rotatedBitmap).toByteArray();
    }
}
