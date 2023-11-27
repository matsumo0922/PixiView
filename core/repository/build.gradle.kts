plugins {
    id("pixiview.library")
    id("pixiview.detekt")
    id("pixiview.hilt")
    id("pixiview.firebase")
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
    implementation(libs.unifile)
}
