package org.wikipedia.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.media.ExifInterface;

import java.io.IOException;

@SuppressWarnings("checkstyle:magicnumber")
public final class ImageUtil {

    /**
     * This method is needed to fix the pre-rotation on photos taken by Android cameras.
     * @param bmp: The bitmap to rotate by 90 degrees (CW).
     * @return Rotated bitmap
     */
    public static Bitmap rotateImage(Bitmap bmp) {
        if (bmp == null) {
            return Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
    }

    /**
     * Rotates a bitmap by the specified amount in degrees (CW).
     * @param bmp: The bitmap to rotate.
     * @param degrees: Float to specify the amount to rotate by
     * @return Bitmap rotated by specifed degrees.
     */
    public static Bitmap rotateImage(Bitmap bmp, float degrees) {
        if (bmp == null) {
            return Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
    }

    /**
     * This method reads the exif data of a photo from storage to determine its orientation.
     * From this, it can decide how much to rotate the image by to make it upright.
     * @param bitmap: The bitmap from storage to rotate.
     * @param path: The full file path of the bitmap in storage.
     * @return Rotated bitmap
     * @throws IOException
     */
    public static Bitmap rotateWithExif(Bitmap bitmap, String path) throws IOException {
        ExifInterface ei = new ExifInterface(path);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap rotatedBitmap;
        switch(orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;
            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
        }
        return rotatedBitmap;
    }

    private ImageUtil() { }
}
