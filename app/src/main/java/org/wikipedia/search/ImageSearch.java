package org.wikipedia.search;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;

public class ImageSearch {
    private Vision vision;

    public ImageSearch() {
        Vision.Builder visionBuilder = new Vision.Builder(
                new NetHttpTransport(),
                new AndroidJsonFactory(),
                null);

        visionBuilder.setVisionRequestInitializer(
                new VisionRequestInitializer("YOUR_API_KEY"));

        vision = visionBuilder.build();
    }

    public String searchPhoto(byte[] photo) {
        String result = "";


        return result;
    }

}
