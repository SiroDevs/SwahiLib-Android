package com.swahilib

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor
import org.gradle.kotlin.dsl.invoke

@Suppress("EnumEntryName")
enum class FlavorDimension {
    contentType,
}

@Suppress("EnumEntryName")
enum class AppFlavor(val dimension: FlavorDimension, val applicationIdSuffix: String? = null) {
    demo(FlavorDimension.contentType, applicationIdSuffix = ".demo"),
    prod(FlavorDimension.contentType),
}

fun configureFlavors(
    commonExtension: CommonExtension,
    flavorConfigurationBlock: ProductFlavor.(flavor: AppFlavor) -> Unit = {},
) {
    commonExtension.apply {
        FlavorDimension.entries.forEach { flavorDimension ->
            flavorDimensions += flavorDimension.name
        }

        productFlavors {
            AppFlavor.entries.forEach { appFlavor ->
                register(appFlavor.name) {
                    dimension = appFlavor.dimension.name
                    flavorConfigurationBlock(this, appFlavor)
                    if (commonExtension is ApplicationExtension && this is ApplicationProductFlavor) {
                        if (appFlavor.applicationIdSuffix != null) {
                            applicationIdSuffix = appFlavor.applicationIdSuffix
                        }
                    }
                }
            }
        }
    }
}
