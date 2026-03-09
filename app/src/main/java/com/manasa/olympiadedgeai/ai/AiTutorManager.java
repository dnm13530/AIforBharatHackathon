package com.manasa.olympiadedgeai.ai;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.manasa.olympiadedgeai.BuildConfig;
import com.manasa.olympiadedgeai.network.TutorApiModels;
import com.manasa.olympiadedgeai.network.TutorApiService;
import com.manasa.olympiadedgeai.ui.Message;

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
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(TutorApiService.class);
    }

    public void getSocraticHint(int questionId, String questionText, List<Message> history, TutorCallback callback) {
        
        // 1. Prepare simple history
        List<Message> cleanHistory = new ArrayList<>();
        for (Message m : history) {
            String text = m.getText();
            if (text != null && !text.isEmpty() && !text.trim().startsWith("{")) {
                // Keep everything else so the AI has memory
                cleanHistory.add(m);
            }
        }

        // 2. Create request exactly as the model expects
        TutorApiModels.Request request = new TutorApiModels.Request(
                String.valueOf(questionId),
                questionText,
                cleanHistory
        );

        Log.d(TAG, "Sending Request to AWS: " + gson.toJson(request));

        apiService.getHint(request).enqueue(new Callback<TutorApiModels.Response>() {
            @Override
            public void onResponse(Call<TutorApiModels.Response> call, Response<TutorApiModels.Response> response) {
                if (!response.isSuccessful()) {
                    callback.onError("Server Error: " + response.code());
                    return;
                }

                TutorApiModels.Response responseBody = response.body();
                if (responseBody == null) {
                    callback.onError("Empty response from server");
                    return;
                }

                // Try to find the AI response in 'body' or top-level
                String tutorText = responseBody.getEffectiveResponse();
                
                // If it's a proxy response, it's wrapped in a string
                if (tutorText == null && responseBody.body != null) {
                    try {
                        JsonObject inner = gson.fromJson(responseBody.body, JsonObject.class);
                        if (inner.has("tutorResponse")) {
                            tutorText = inner.get("tutorResponse").getAsString();
                        }
                    } catch (Exception ignored) {}
                }

                if (tutorText != null) {
                    callback.onResponse(tutorText);
                } else {
                    callback.onError("AI could not formulate a hint. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<TutorApiModels.Response> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public interface TutorCallback {
        void onResponse(String response);
        void onError(String error);
    }
}
