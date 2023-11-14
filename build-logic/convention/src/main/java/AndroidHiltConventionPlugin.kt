
import caios.android.pixiview.library
import caios.android.pixiview.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("dagger.hilt.android.plugin")
                apply("com.google.devtools.ksp")
            }

            dependencies {
                "implementation"(libs.library("dagger.hilt"))
                "ksp"(libs.library("dagger.hilt.compiler"))
            }
        }
    }
}
