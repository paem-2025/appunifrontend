package com.example.app_infounsada

import com.google.gson.annotations.SerializedName

data class IngresanteFaqResponse(
    @SerializedName("idfaq") val idfaq: Long? = null,
    @SerializedName("question") val question: String? = null,
    @SerializedName("answer") val answer: String? = null,
    @SerializedName("category") val category: String? = null,
    @SerializedName("displayOrder") val displayOrder: Int? = null,
    @SerializedName("sourceUrl") val sourceUrl: String? = null
)
