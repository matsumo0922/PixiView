plugins {
    id("pixiview.library")
    id("pixiview.hilt")
    id("pixiview.detekt")
    id("pixiview.firebase")
}

android {
    namespace = "caios.android.pixiview.core.billing"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:repository"))
    implementation(project(":core:datastore"))

    api(libs.bundles.billing)
}
