package org.wikipedia.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class ImageUtilTest {
    @SuppressWarnings("checkstyle:magicnumber")
    @Test
    public void testRotateImage() {
        // Initial bitmap
        Bitmap bmp = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);

        // Initial bitmap rotated
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rotated = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

        assertThat(ImageUtil.rotateImage(bmp), is(FileUtil.compressBmpToJpg(rotated).toByteArray()));
    }

    @Test
    public void testEmptyBitmapArgument() {
        assertThat(ImageUtil.rotateImage(null), is(new byte[0]));
    }
}
