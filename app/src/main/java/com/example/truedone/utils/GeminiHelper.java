package com.example.truedone.utils;

import android.graphics.Bitmap;

import com.example.truedone.BuildConfig;
import com.example.truedone.data.network.GeminiService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GeminiHelper {
    public interface GeminiCallback {
        void onSuccess(String statement, List<String> improvements);
        void onError(String error);
    }

    public static void analyzeImages(Bitmap before, Bitmap after, GeminiCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://generativelanguage.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GeminiService service = retrofit.create(GeminiService.class);

        JsonObject body = new JsonObject();
        JsonArray contents = new JsonArray();
        JsonObject contentObj = new JsonObject();
        JsonArray parts = new JsonArray();

        // Text Prompt
        JsonObject textPart = new JsonObject();
        textPart.addProperty("text", "Analyze these before and after images of a task. Provide 1 summary statement and 3 bullet point improvements. Format output strictly as: 'Statement: [text]\\nImprovements:\\n1. [text]\\n2. [text]\\n3. [text]'");
        parts.add(textPart);

        // Before Image
        JsonObject img1 = new JsonObject();
        JsonObject inlineData1 = new JsonObject();
        inlineData1.addProperty("mimeType", "image/jpeg");
        inlineData1.addProperty("data", ImageUtils.bitmapToBase64(before));
        img1.add("inlineData", inlineData1);
        parts.add(img1);

        // After Image
        JsonObject img2 = new JsonObject();
        JsonObject inlineData2 = new JsonObject();
        inlineData2.addProperty("mimeType", "image/jpeg");
        inlineData2.addProperty("data", ImageUtils.bitmapToBase64(after));
        img2.add("inlineData", inlineData2);
        parts.add(img2);

        contentObj.add("parts", parts);
        contents.add(contentObj);
        body.add("contents", contents);

        service.generateContent(BuildConfig.GEMINI_API_KEY, body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String text = response.body().getAsJsonArray("candidates")
                                .get(0).getAsJsonObject().getAsJsonObject("content")
                                .getAsJsonArray("parts").get(0).getAsJsonObject()
                                .get("text").getAsString();

                        String statement = "Analysis Complete";
                        List<String> improvements = new ArrayList<>();

                        String[] lines = text.split("\n");
                        boolean capturingImprovements = false;
                        for(String line : lines) {
                            if(line.startsWith("Statement:")) statement = line.replace("Statement:", "").trim();
                            else if(line.startsWith("Improvements:")) capturingImprovements = true;
                            else if(capturingImprovements && (line.startsWith("1.") || line.startsWith("2.") || line.startsWith("3.") || line.startsWith("-"))) {
                                improvements.add(line.replaceAll("^[0-9.-]+\\s*", "").trim());
                            }
                        }
                        if(improvements.isEmpty()) improvements.add("Good job completing the task!");

                        callback.onSuccess(statement, improvements);
                    } catch (Exception e) {
                        callback.onError("Parsing error: " + e.getMessage());
                    }
                } else {
                    try {
                        String errorDetails = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        callback.onError("API Error " + response.code() + ": " + errorDetails);
                    } catch (Exception e) {
                        callback.onError("API Error: " + response.code());
                    }
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
}