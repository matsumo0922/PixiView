plugins {
    id("kanade.library")
    id("kanade.detekt")
    id("kanade.hilt")
}

android {
    namespace = "caios.android.pixiview.core.repository"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:datastore"))

    implementation(libs.bundles.ktor)
    implementation(libs.jsoup)
}
