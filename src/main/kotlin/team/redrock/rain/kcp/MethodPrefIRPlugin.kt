package team.redrock.rain.kcp

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

/**
 * team.redrock.rain.kcp.MethodPrefIRPlugin.kt
 * kcp-learning
 *
 * @author 寒雨
 * @since 2022/10/27 上午11:50
 */
class MethodPrefIRPlugin : KotlinCompilerPluginSupportPlugin {

    lateinit var project: Project

    override fun apply(target: Project) {
        super.apply(target)
        project = target
    }

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        val extension = project.extensions.getByType(MethodPrefExtension::class.java) as MethodPrefExtension
        return project.provider {
            listOf(
                SubpluginOption(key = "string", value = extension.string),
                SubpluginOption(key = "file", value = extension.file),
            )
        }
    }

    override fun getCompilerPluginId(): String {
        return "team.redrock.rain.kcp"
    }

    override fun getPluginArtifact(): SubpluginArtifact  = SubpluginArtifact(
        groupId = "team.redrock.rain",
        artifactId = "kcp",
        version = "0.0.1"
    )

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = project.plugins.hasPlugin(MethodPrefIRPlugin::class.java)
}