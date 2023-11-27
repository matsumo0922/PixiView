plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.secret.gradlePlugin)
    implementation(libs.detekt.gradlePlugin)
    implementation(libs.gms.services)
    implementation(libs.gms.oss)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "pixiview.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "pixiview.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "pixiview.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "pixiview.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidLibraryGlance") {
            id = "pixiview.library.glance"
            implementationClass = "AndroidLibraryGlanceConventionPlugin"
        }
        register("androidLibraryChaquopy") {
            id = "pixiview.library.chaquopy"
            implementationClass = "AndroidLibraryChaquopyConventionPlugin"
        }
        register("androidHilt") {
            id = "pixiview.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidDetekt") {
            id = "pixiview.detekt"
            implementationClass = "AndroidDetektConventionPlugin"
        }
        register("androidFirebase") {
            id = "pixiview.firebase"
            implementationClass = "AndroidFirebaseConventionPlugin"
        }
    }
}
