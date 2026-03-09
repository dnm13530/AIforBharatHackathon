package com.manasa.olympiadedgeai.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TutorApiService {
    @POST("tutor/hint")
    Call<TutorApiModels.Response> getHint(@Body TutorApiModels.Request request);
}
