package com.example.app_infounsada

import com.google.gson.annotations.SerializedName

data class FinalExamResponse(
    @SerializedName("idfinalExam") val idfinalExam: Long? = null,
    @SerializedName("examYear") val examYear: Int? = null,
    @SerializedName("turnOrder") val turnOrder: Int? = null,
    @SerializedName("turnName") val turnName: String? = null,
    @SerializedName("periodLabel") val periodLabel: String? = null,
    @SerializedName("enrollmentStart") val enrollmentStart: String? = null,
    @SerializedName("examStart") val examStart: String? = null,
    @SerializedName("examEnd") val examEnd: String? = null,
    @SerializedName("notes") val notes: String? = null,
    @SerializedName("sourceUrl") val sourceUrl: String? = null
)
