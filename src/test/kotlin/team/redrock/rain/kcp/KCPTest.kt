package team.redrock.rain.kcp

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * team.redrock.rain.kcp.KCPTest.kt
 * kcp-learning
 *
 * @author 寒雨
 * @since 2022/10/27 下午10:59
 */
class MethodPerfTest {
    @Test
    fun `IR plugin`() {
        val result = compile(
            sourceFile = SourceFile.kotlin("main.kt", """ 
        import team.redrock.rain.kcp.annotation.Cost       

        fun main() {
          println(foo())
          println(foo("Transform", "Kotlin IR"))
        }

        @Cost
        fun foo(param1: String? = "Hello", param2: String? = "World"): String {
          println("foo called param1=[${'$'}param1], param2=[${'$'}param2]")
          Thread.sleep(1000)
          return param1 + param2
        }
      """.trimIndent())
        )

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        //类加载目标产物，看最终运行结果是否符合预期
        val ktClazz = result.classLoader.loadClass("MainKt")
        val main = ktClazz.declaredMethods.single { it.name == "main" && it.parameterCount == 0 }
        main.invoke(null)
    }

    private fun compile(
        sourceFiles: List<SourceFile>,
        plugin: ComponentRegistrar = MethodPerfComponentRegistrar(),
    ): KotlinCompilation.Result {
        return KotlinCompilation().apply {
            sources = sourceFiles
            useIR = true
            compilerPlugins = listOf(plugin)
            inheritClassPath = true
        }.compile()
    }

    private fun compile(
        sourceFile: SourceFile,
        plugin: ComponentRegistrar = MethodPerfComponentRegistrar(),
    ): KotlinCompilation.Result {
        return compile(listOf(sourceFile), plugin)
    }
}