
import caios.android.fanbox.androidTestImplementation
import caios.android.fanbox.debugImplementation
import caios.android.fanbox.implementation
import caios.android.fanbox.library
import caios.android.fanbox.libs
import caios.android.fanbox.version
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryComposeConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            extensions.configure<LibraryExtension> {
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
