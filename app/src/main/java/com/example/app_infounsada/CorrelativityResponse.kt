package com.example.app_infounsada

import com.google.gson.annotations.SerializedName

data class CorrelativityResponse(
    @SerializedName("idcorrelativity") val idcorrelativity: Long? = null,
    @SerializedName("careerKey") val careerKey: String? = null,
    @SerializedName("careerName") val careerName: String? = null,
    @SerializedName("planName") val planName: String? = null,
    @SerializedName("planYear") val planYear: Int? = null,
    @SerializedName("subjectCode") val subjectCode: String? = null,
    @SerializedName("subjectName") val subjectName: String? = null,
    @SerializedName("subjectYear") val subjectYear: Int? = null,
    @SerializedName("subjectTerm") val subjectTerm: String? = null,
    @SerializedName("requirementCodes") val requirementCodes: String? = null,
    @SerializedName("requirementSubjects") val requirementSubjects: String? = null,
    @SerializedName("notes") val notes: String? = null,
    @SerializedName("sourceUrl") val sourceUrl: String? = null
)
