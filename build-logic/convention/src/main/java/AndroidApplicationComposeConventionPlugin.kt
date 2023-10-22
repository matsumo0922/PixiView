
import caios.android.pixiview.androidTestImplementation
import caios.android.pixiview.debugImplementation
import caios.android.pixiview.implementation
import caios.android.pixiview.library
import caios.android.pixiview.libs
import caios.android.pixiview.version
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            extensions.configure<ApplicationExtension> {
                buildFeatures.compose = true
                composeOptions.kotlinCompilerExtensionVersion = libs.version("kotlinCompiler")
            }

            dependencies {
                val bom = libs.library("androidx-compose-bom")

                implementation(platform(bom))
                implementation(libs.library("androidx-compose-ui-tooling-preview"))
                debugImplementation(libs.library("androidx-compose-ui-tooling"))
                androidTestImplementation(platform(bom))
            }
        }
    }
}
