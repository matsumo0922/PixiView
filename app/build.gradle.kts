@file:Suppress("UnstableApiUsage")

import com.android.build.api.variant.BuildConfigField
import com.android.build.api.variant.ResValue
import org.jetbrains.kotlin.konan.properties.Properties
import java.io.Serializable

plugins {
    id("pixiview.application")
    id("pixiview.application.compose")
    id("pixiview.hilt")
    id("pixiview.detekt")
    id("pixiview.firebase")
}

android {
    namespace = "caios.android.fanbox"

    splits {
        abi {
            reset()
            include("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
            isUniversalApk = false
        }
    }

    val localProperties = Properties().apply {
        load(project.rootDir.resolve("local.properties").inputStream())
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("${project.rootDir}/gradle/keystore/debug.keystore")
        }
        create("release") {
            storeFile = file("${project.rootDir}/gradle/keystore/release.keystore")
            storePassword = localProperties.getProperty("storePassword") ?: System.getenv("RELEASE_STORE_PASSWORD")
            keyPassword = localProperties.getProperty("keyPassword") ?: System.getenv("RELEASE_KEY_PASSWORD")
            keyAlias = localProperties.getProperty("keyAlias") ?: System.getenv("RELEASE_KEY_ALIAS")
        }
        create("billing") {
            storeFile = file("${project.rootDir}/gradle/keystore/release.keystore")
            storePassword = localProperties.getProperty("storePassword") ?: System.getenv("RELEASE_STORE_PASSWORD")
            keyPassword = localProperties.getProperty("keyPassword") ?: System.getenv("RELEASE_KEY_PASSWORD")
            keyAlias = localProperties.getProperty("keyAlias") ?: System.getenv("RELEASE_KEY_ALIAS")
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = true
            versionNameSuffix = ".D"
            applicationIdSuffix = ".debug"
        }
        create("billing") {
            signingConfig = signingConfigs.getByName("billing")
            isDebuggable = true
            matchingFallbacks.add("debug")
        }
    }

    androidComponents {
        onVariants {
            val appName = when (it.buildType) {
                "debug" -> "FANBOX Debug"
                "billing" -> "FANBOX Billing"
                else -> "FANBOX"
            }

            // This ID must be valid or the app will crash.
            // When building from GitHub, either exclude AdMob code or register with AdMob for an ID.
            val admobTestAppId = "ca-app-pub-0000000000000000~0000000000"
            val bannerAdTestId = "ca-app-pub-3940256099942544/6300978111"
            val nativeAdTestId = "ca-app-pub-3940256099942544/2247696110"

            it.manifestPlaceholders.apply {
                putManifestPlaceholder(localProperties, "ADMOB_APP_ID", defaultValue = admobTestAppId)
            }

            it.resValues.apply {
                put(it.makeResValueKey("string", "app_name"), ResValue(appName, null))
            }

            it.buildConfigFields.apply {
                putBuildConfig(localProperties, "VERSION_NAME", libs.versions.versionName.get().toStringLiteral())
                putBuildConfig(localProperties, "VERSION_CODE", libs.versions.versionCode.get().toStringLiteral())
                putBuildConfig(localProperties, "DEVELOPER_PASSWORD")
                putBuildConfig(localProperties, "PIXIV_CLIENT_ID")
                putBuildConfig(localProperties, "PIXIV_CLIENT_SECRET")
                putBuildConfig(localProperties, "ADMOB_APP_ID", defaultValue = admobTestAppId)
                putBuildConfig(localProperties, "ADMOB_BANNER_AD_UNIT_ID", if (it.buildType != "release") bannerAdTestId else null)
                putBuildConfig(localProperties, "ADMOB_NATIVE_AD_UNIT_ID", if (it.buildType != "release") nativeAdTestId else null)
            }

            if (it.buildType == "release") {
                it.packaging.resources.excludes.add("META-INF/**")
            }
        }
    }

    lint {
        // Error: MusicService must extend android.app.Service [Instantiatable]
        disable.add("Instantiatable")
    }
}

kotlin {
    sourceSets.all {
        languageSettings {
            // Try K2 compiler
            languageVersion = "2.0"
        }
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:datastore"))
    implementation(project(":core:repository"))
    implementation(project(":core:ui"))
    implementation(project(":core:billing"))
    implementation(project(":core:logs"))

    implementation(project(":feature:report"))
    implementation(project(":feature:library"))
    implementation(project(":feature:welcome"))
    implementation(project(":feature:post"))
    implementation(project(":feature:creator"))
    implementation(project(":feature:about"))
    implementation(project(":feature:setting"))

    implementation(libs.bundles.ui.implementation)
    implementation(libs.bundles.ktor)

    implementation(libs.androidx.core.splashscreen)
    implementation(libs.play.review)
    implementation(libs.play.update)
    implementation(libs.play.service.oss)
    implementation(libs.play.service.ads)
    implementation(libs.google.material)

    debugImplementation(libs.facebook.flipper)
    debugImplementation(libs.facebook.flipper.network)
    debugImplementation(libs.facebook.flipper.leakcanary)
    debugImplementation(libs.facebook.soloader)
    // debugImplementation(libs.leakcanary)
}

fun MapProperty<String, BuildConfigField<out Serializable>>.putBuildConfig(
    localProperties: Properties,
    key: String,
    value: String? = null,
    type: String = "String",
    defaultValue: String = "",
    comment: String? = null
) {
    val property = localProperties.getProperty(key)
    val env = System.getenv(key)

    put(key, BuildConfigField(type, (value ?: property ?: env ?: defaultValue).toStringLiteral(), comment))
}

fun MapProperty<String, String>.putManifestPlaceholder(
    localProperties: Properties,
    key: String,
    value: String? = null,
    defaultValue: String = "",
) {
    val property = localProperties.getProperty(key)
    val env = System.getenv(key)

    put(key, (value ?: property ?: env ?: defaultValue).replace("\"", ""))
}

fun Any.toStringLiteral(): String {
    val value = toString()

    if (value.firstOrNull() == '\"' && value.lastOrNull() == '\"') {
        return value
    }

    return "\"$value\""
}
