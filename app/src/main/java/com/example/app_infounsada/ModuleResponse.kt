package com.example.app_infounsada

import com.google.gson.annotations.SerializedName

data class ModuleResponse(
    @SerializedName("idmodule") val idmodule: Long? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("content") val content: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("deletedAt") val deletedAt: String? = null,
    @SerializedName("imagePath") val imagePath: String? = null,
    @SerializedName("topicId") val topicId: Int? = null,
    @SerializedName("topicName") val topicName: String? = null,
    @SerializedName("sourceUrl") val sourceUrl: String? = null
)
