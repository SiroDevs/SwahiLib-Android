package com.swahilib.domain.repos

import com.revenuecat.purchases.*
import javax.inject.*

@Singleton
class SubsRepo @Inject constructor() {
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
                    customerInfo.entitlements.active.isEmpty()
                completion(!isActive)
            }
        )
    }
}
