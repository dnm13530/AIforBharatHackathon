package com.manasa.olympiadedgeai.ai;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.manasa.olympiadedgeai.BuildConfig;
import com.manasa.olympiadedgeai.network.TutorApiModels;
import com.manasa.olympiadedgeai.network.TutorApiService;
import com.manasa.olympiadedgeai.ui.Message;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AiTutorManager {

    private static final String TAG = "AiTutorManager";
    private final TutorApiService apiService;
    private final Gson gson = new Gson();

    public AiTutorManager() {
        String baseUrl = BuildConfig.API_GATEWAY_URL;

        if (baseUrl == null || baseUrl.isEmpty()) {
            baseUrl = "https://imv94a51qg.execute-api.ap-south-1.amazonaws.com/prod/";
        }

        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(
                message -> Log.d(TAG, "Network: " + message)
        );
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(TutorApiService.class);
    }

    public void getSocraticHint(String userId, int questionId, String questionText, List<Message> history, TutorCallback callback) {
        getHintInternal(userId, questionId, questionText, history, null, callback);
    }

    public void getVisionHint(String userId, int questionId, String questionText, List<Message> history, String imagePath, TutorCallback callback) {
        String base64Image = encodeImageToBase64(imagePath);
        if (base64Image == null) {
            callback.onError("Failed to process image locally");
            return;
        }
        getHintInternal(userId, questionId, questionText, history, base64Image, callback);
    }

    private void getHintInternal(String userId, int questionId, String questionText, List<Message> history, String imageBase64, TutorCallback callback) {
        List<Message> cleanHistory = new ArrayList<>();
        for (Message m : history) {
            String text = m.getText();
            if (text != null && !text.isEmpty() && !text.trim().startsWith("{") && !text.contains("break it down")) {
                cleanHistory.add(m);
            }
        }

        TutorApiModels.Request request = new TutorApiModels.Request(
                userId,
                String.valueOf(questionId),
                questionText,
                cleanHistory,
                imageBase64
        );

        Log.d(TAG, "Sending request for user: " + userId);

        apiService.getHint(request).enqueue(new Callback<TutorApiModels.Response>() {
            @Override
            public void onResponse(Call<TutorApiModels.Response> call, Response<TutorApiModels.Response> response) {
                if (!response.isSuccessful()) {
                    callback.onError("Server HTTP Error: " + response.code());
                    return;
                }

                TutorApiModels.Response responseBody = response.body();
                if (responseBody == null) {
                    callback.onError("Empty response body from AWS");
                    return;
                }

                String tutorText = responseBody.getEffectiveResponse();
                
                if (tutorText == null && responseBody.body != null) {
                    try {
                        JsonObject inner = gson.fromJson(responseBody.body, JsonObject.class);
                        if (inner.has("error") && !inner.get("error").isJsonNull()) {
                            callback.onError("Backend Error: " + inner.get("error").getAsString());
                            return;
                        }
                        if (inner.has("tutorResponse") && !inner.get("tutorResponse").isJsonNull()) {
                            tutorText = inner.get("tutorResponse").getAsString();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to parse proxy body JSON", e);
                    }
                }

                if (tutorText != null) {
                    callback.onResponse(tutorText);
                } else {
                    callback.onError("AI Processing Error");
                }
            }

            @Override
            public void onFailure(Call<TutorApiModels.Response> call, Throwable t) {
                callback.onError("Network connection failure: " + t.getMessage());
            }
        });
    }

    private String encodeImageToBase64(String imagePath) {
        try {
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) return null;

            Bitmap bm = BitmapFactory.decodeFile(imagePath);
            int width = bm.getWidth();
            int height = bm.getHeight();
            float ratio = (float) width / height;
            if (width > 1024) {
                width = 1024;
                height = (int) (width / ratio);
            }
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bm, width, height, true);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos); 
            byte[] b = baos.toByteArray();
            return Base64.encodeToString(b, Base64.NO_WRAP);
        } catch (Exception e) {
            Log.e(TAG, "Error encoding image", e);
            return null;
        }
    }

    public interface TutorCallback {
        void onResponse(String response);
        void onError(String error);
    }
}
