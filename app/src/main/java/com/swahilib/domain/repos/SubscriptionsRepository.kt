package com.swahilib.domain.repos

import com.revenuecat.purchases.*
import com.swahilib.core.utils.AppConstants
import javax.inject.*

@Singleton
class SubscriptionsRepository @Inject constructor() {
    suspend fun isProUser(isOnline: Boolean, completion: (Boolean) -> Unit) {
        val fetchPolicy = if (isOnline) {
            CacheFetchPolicy.FETCH_CURRENT
        } else {
            CacheFetchPolicy.CACHE_ONLY
        }
        Purchases.sharedInstance.getCustomerInfoWith(
            fetchPolicy = fetchPolicy,
            onError = { error ->
                completion(false)
            },
            onSuccess = { customerInfo ->
                val isActive =
                    customerInfo.entitlements[AppConstants.ENTITLEMENTS]?.isActive == true
                completion(isActive)
            }
        )
    }
}
