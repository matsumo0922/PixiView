plugins {
    id("pixiview.library")
    id("pixiview.detekt")
    id("pixiview.hilt")
    id("pixiview.firebase")
}

android {
    namespace = "caios.android.fanbox.core.repository"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:datastore"))
    implementation(project(":core:logs"))

    implementation(libs.bundles.ktor)
    implementation(libs.androidx.paging)
    implementation(libs.jsoup)
    implementation(libs.unifile)
}
