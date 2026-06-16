package com.swahilib.core.network.dtos

import com.google.gson.annotations.SerializedName

data class PaystackInitializeRequest(
    @SerializedName("email") val email: String,
    @SerializedName("amount") val amount: Long,
    @SerializedName("callback_url") val callbackUrl: String,
    @SerializedName("metadata") val metadata: PaystackMetadata? = null,
)

data class PaystackMetadata(
    @SerializedName("custom_fields") val customFields: List<PaystackCustomField> = emptyList(),
)

data class PaystackCustomField(
    @SerializedName("display_name") val displayName: String,
    @SerializedName("variable_name") val variableName: String,
    @SerializedName("value") val value: String,
)

data class PaystackInitializeResponse(
    @SerializedName("status") val status: Boolean = false,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: PaystackInitializeData? = null,
)

data class PaystackInitializeData(
    @SerializedName("authorization_url") val authorizationUrl: String? = null,
    @SerializedName("access_code") val accessCode: String? = null,
    @SerializedName("reference") val reference: String? = null,
)
