package team.redrock.rain.kcp

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.util.dump
import team.redrock.rain.kcp.transformer.MethodCostTimeTransformer

/**
 * team.redrock.rain.kcp.MethodPrefExtension.kt
 * kcp-learning
 *
 * @author 寒雨
 * @since 2022/10/27 下午7:18
 */
class MethodPrefExtension(
    val string: String,
    val file: String
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        println("generate:: string: $string file: $file")

        println("------ before transform dump IR -------")
        println(moduleFragment.dump())

        println("------  Transforming... -------")
        moduleFragment.transform(MethodCostTimeTransformer(pluginContext), null)

        println("------ after transform dump IR -------")
        println(moduleFragment.dump())
    }
}