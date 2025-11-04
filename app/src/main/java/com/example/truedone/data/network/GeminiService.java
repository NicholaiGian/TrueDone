package com.example.truedone.data.network;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GeminiService {
    // Change Gemini version accordingly as needed.
    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    Call<JsonObject> generateContent(@Query("key") String apiKey, @Body JsonObject body);
}