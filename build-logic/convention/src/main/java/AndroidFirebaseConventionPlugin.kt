
import caios.android.pixiview.bundle
import caios.android.pixiview.implementation
import caios.android.pixiview.library
import caios.android.pixiview.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFirebaseConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.firebase.crashlytics")
            }

            dependencies {
                val bom = libs.library("firebase-bom")

                implementation(platform(bom))
                implementation(libs.bundle("firebase"))
            }
        }
    }
}
