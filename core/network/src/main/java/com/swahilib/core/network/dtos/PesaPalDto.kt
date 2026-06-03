package com.swahilib.core.network.dtos

import com.google.gson.annotations.SerializedName

data class PesaPalAuthRequest(
    @SerializedName("consumer_key") val consumerKey: String,
    @SerializedName("consumer_secret") val consumerSecret: String,
)

data class PesaPalIpnRequest(
    @SerializedName("url") val url: String,
    @SerializedName("ipn_notification_type") val ipnNotificationType: String = "GET",
)

data class PesaPalOrderRequest(
    @SerializedName("id") val id: String,
    @SerializedName("currency") val currency: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("description") val description: String,
    @SerializedName("callback_url") val callbackUrl: String,
    @SerializedName("notification_id") val notificationId: String,
    @SerializedName("billing_address") val billingAddress: PesaPalBillingAddress,
)

data class PesaPalBillingAddress(
    @SerializedName("email_address") val emailAddress: String,
    @SerializedName("phone_number") val phoneNumber: String = "",
    @SerializedName("country_code") val countryCode: String = "KE",
    @SerializedName("first_name") val firstName: String = "SwahiLib",
    @SerializedName("last_name") val lastName: String = "Donor",
    @SerializedName("line_1") val line1: String = "",
    @SerializedName("city") val city: String = "",
    @SerializedName("state") val state: String = "",
    @SerializedName("postal_code") val postalCode: String = "",
    @SerializedName("zip_code") val zipCode: String = "",
)

data class PesaPalAuthResponse(
    @SerializedName("token") val token: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("message") val message: String? = null,
)

data class PesaPalIpnResponse(
    @SerializedName("ipn_id") val ipnId: String? = null,
    @SerializedName("url") val url: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("message") val message: String? = null,
)

data class PesaPalOrderResponse(
    @SerializedName("order_tracking_id") val orderTrackingId: String? = null,
    @SerializedName("merchant_reference") val merchantReference: String? = null,
    @SerializedName("redirect_url") val redirectUrl: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("message") val message: String? = null,
)
