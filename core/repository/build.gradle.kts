plugins {
    id("pixiview.library")
    id("pixiview.library.chaquopy")
    id("pixiview.detekt")
    id("pixiview.hilt")
}

android {
    namespace = "caios.android.pixiview.core.repository"
}

chaquopy {
    defaultConfig {
        version = "3.8"

        pip {
            install("pixivpy3")
        }
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:datastore"))

    implementation(libs.bundles.ktor)
    implementation(libs.jsoup)
}
