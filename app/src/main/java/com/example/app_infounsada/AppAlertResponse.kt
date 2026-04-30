package com.example.app_infounsada

import com.google.gson.annotations.SerializedName

data class AppAlertResponse(
    @SerializedName("idappAlert") val idappAlert: Long? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("alertDate") val alertDate: String? = null,
    @SerializedName("endDate") val endDate: String? = null,
    @SerializedName("category") val category: String? = null,
    @SerializedName("audience") val audience: String? = null,
    @SerializedName("important") val important: Boolean? = null,
    @SerializedName("sourceUrl") val sourceUrl: String? = null
)
