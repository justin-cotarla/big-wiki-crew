package org.wikipedia.search;

import android.content.Context;
import android.os.StrictMode;

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

    public ImageSearch(Vision vision) {
        this.vision = vision;
    }

    public String searchPhoto(byte[] photo) throws ImageSearchException {
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
        try {
            BatchAnnotateImagesResponse batchResponse =
                    vision.images().annotate(batchRequest).execute();

            return extractEntityFromResponse(batchResponse);
        } catch (IOException e) {
            throw new ImageSearchException("Could Not Send Images to Google Vision", e);
        }
    }

    public String extractEntityFromResponse(BatchAnnotateImagesResponse batchResponse) throws ImageSearchException {
        AnnotateImageResponse response = batchResponse.getResponses()
                .get(0);
        try {
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray entities = jsonResponse.getJSONObject("webDetection").getJSONArray("webEntities");

            return entities.getJSONObject(0).getString("description");
        } catch (org.json.JSONException e) {
            throw new ImageSearchException("No Image Search Result Found", e);
        }
    }
}
