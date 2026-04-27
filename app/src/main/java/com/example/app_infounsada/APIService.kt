package com.example.app_infounsada

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface APIService {
    @GET("api/modules/with-topics")
    suspend fun getAllModules(): Response<List<ModuleResponse>>

    @GET("api/modules/topic-name/{topicName}")
    suspend fun getModulesByTopicName(@Path("topicName") topicName: String): Response<List<ModuleResponse>>

    @GET("api/final-exams")
    suspend fun getCurrentYearFinalExams(): Response<List<FinalExamResponse>>

    @GET("api/final-exams/year/{year}")
    suspend fun getFinalExamsByYear(@Path("year") year: Int): Response<List<FinalExamResponse>>
}
