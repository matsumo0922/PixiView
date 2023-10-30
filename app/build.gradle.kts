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
}

android {
    namespace = "caios.android.pixiview"

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
                "debug" -> "PixiView Debug"
                "billing" -> "PixiView Billing"
                else -> "PixiView"
            }

            it.resValues.put(it.makeResValueKey("string", "app_name"), ResValue(appName, null))
            it.buildConfigFields.apply {
                putBuildConfig(localProperties, "VERSION_NAME", libs.versions.versionName.get().toStringLiteral())
                putBuildConfig(localProperties, "VERSION_CODE", libs.versions.versionCode.get().toStringLiteral())
                putBuildConfig(localProperties, "DEVELOPER_PASSWORD")
                putBuildConfig(localProperties, "PIXIV_CLIENT_ID")
                putBuildConfig(localProperties, "PIXIV_CLIENT_SECRET")
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

kapt {
    correctErrorTypes = true
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:datastore"))
    implementation(project(":core:repository"))
    implementation(project(":core:ui"))

    implementation(project(":feature:report"))
    implementation(project(":feature:library"))
    implementation(project(":feature:welcome"))
    implementation(project(":feature:post"))

    implementation(platform(libs.firebase.bom))
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.bundles.ui.implementation)
    implementation(libs.bundles.firebase)
    implementation(libs.bundles.ktor)

    kapt(libs.bundles.ui.kapt)

    implementation(libs.androidx.core.splashscreen)
    implementation(libs.play.service.oss)
    implementation(libs.google.material)

    debugImplementation(libs.facebook.flipper)
    debugImplementation(libs.facebook.flipper.network)
    debugImplementation(libs.facebook.flipper.leakcanary)
    debugImplementation(libs.facebook.soloader)
    // debugImplementation(libs.leakcanary)
}

plugins.apply("com.google.gms.google-services")

fun MapProperty<String, BuildConfigField<out Serializable>>.putBuildConfig(
    localProperties: Properties,
    key: String,
    value: String? = null,
    type: String = "String",
    comment: String? = null
) {
    val property = localProperties.getProperty(key)?.toStringLiteral()
    val env = System.getenv(key)?.toStringLiteral()

    put(key, BuildConfigField(type, value ?: property ?: env ?: "\"\"", comment))
}

fun Any.toStringLiteral(): String {
    val value = toString()

    if (value.first() == '\"' && value.last() == '\"') {
        return value
    }

    return "\"$value\""
}
