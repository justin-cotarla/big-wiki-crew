package org.wikipedia.search;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import org.json.JSONArray;
import org.json.JSONObject;
import org.wikipedia.R;

import java.io.IOException;
import java.util.Arrays;


public class ImageSearch {
    private Vision vision;

    //Pass in the application context with this.getApplicationContext() to access resources
    public ImageSearch(Context applicationContext) {
        Vision.Builder visionBuilder = new Vision.Builder(
                new NetHttpTransport(),
                new AndroidJsonFactory(),
                null);

        visionBuilder.setVisionRequestInitializer(
                new VisionRequestInitializer(applicationContext.getString(R.string.google_vision_api_key)));

        vision = visionBuilder.build();
    }

    public String searchPhoto(byte[] photo) throws IOException, org.json.JSONException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Image inputImage = new Image();
        inputImage.encodeContent(photo);

        Feature desiredFeature = new Feature();
        desiredFeature.setType("WEB_DETECTION");

        AnnotateImageRequest request = new AnnotateImageRequest();
        request.setImage(inputImage);
        request.setFeatures(Arrays.asList(desiredFeature));

        BatchAnnotateImagesRequest batchRequest =
                new BatchAnnotateImagesRequest();

        batchRequest.setRequests(Arrays.asList(request));

        BatchAnnotateImagesResponse batchResponse =
                vision.images().annotate(batchRequest).execute();

        AnnotateImageResponse response = batchResponse.getResponses()
                .get(0);

        JSONObject jsonResponse = new JSONObject(response.toString());
        JSONArray entities = jsonResponse.getJSONObject("webDetection").getJSONArray("webEntities");

        if (entities.length()>0) {
            return entities.getJSONObject(0).getString("description");
        } else {
            throw new IOException("No Image Search Result Found");
        }
    }
}
