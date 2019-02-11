package org.wikipedia.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(RobolectricTestRunner.class)
@SuppressWarnings("checkstyle:magicnumber")
public class ImageUtilTest {
    @Test
    public void testRotateImage() {
        // Initial bitmap
        Bitmap bmp = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
        // Initial bitmap rotated
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rotated = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

        byte[] rotatedBytes = FileUtil.compressBmpToJpg(rotated).toByteArray();
        byte[] result = FileUtil.compressBmpToJpg(ImageUtil.rotateImage(bmp)).toByteArray();

        assertThat(result, is(rotatedBytes));
    }
    @Test
    public void testRotateImageDegrees() {
        // Initial bitmap
        Bitmap bmp = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
        // Initial bitmap rotated 180 degrees
        Matrix matrix = new Matrix();
        matrix.postRotate(180);
        Bitmap rotated = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

        byte[] rotatedBytes = FileUtil.compressBmpToJpg(rotated).toByteArray();
        byte[] result = FileUtil.compressBmpToJpg(ImageUtil.rotateImage(bmp, 180)).toByteArray();

        assertThat(result, is(rotatedBytes));
    }
    @Test
    public void testEmptyBitmapArgument() {
        byte[] emptyBytes = FileUtil.compressBmpToJpg(Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)).toByteArray();
        byte[] result = FileUtil.compressBmpToJpg(ImageUtil.rotateImage(null)).toByteArray();
        assertThat(result, is(emptyBytes));
    }
}
