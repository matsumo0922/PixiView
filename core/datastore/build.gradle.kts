plugins {
    id("pixiview.library")
    id("pixiview.detekt")
    id("pixiview.hilt")
    id("pixiview.firebase")
    alias(libs.plugins.protobuf)
}

android {
    namespace = "caios.android.fanbox.core.datastore"
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                register("java") {
                    option("lite")
                }
                register("kotlin") {
                    option("lite")
                }
            }
        }
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:logs"))

    implementation(libs.androidx.datastore)
    implementation(libs.protobuf.kotlin.lite)
}
