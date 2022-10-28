package team.redrock.rain.kcp.transformer

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrValueDeclaration
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.expressions.addArgument
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.render
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.name.FqName
import team.redrock.rain.kcp.utils.elapsedNowFunc
import team.redrock.rain.kcp.utils.markNowFunc
import team.redrock.rain.kcp.utils.monotonicClass
import team.redrock.rain.kcp.utils.printlnFunc

/**
 * team.redrock.rain.kcp.transformer.MethodCostTimeTransformer.kt
 * kcp-learning
 *
 * @author 寒雨
 * @since 2022/10/27 下午7:40
 */
class MethodCostTimeTransformer(
    private val pluginContext: IrPluginContext
): IrElementTransformerVoidWithContext() {
    override fun visitFunctionNew(declaration: IrFunction): IrStatement {
        println("visitFunctionNew: ${declaration.render()}")
        if (declaration.hasAnnotation(FqName("team.redrock.rain.kcp.annotation.Cost"))) {
            declaration.body = declaration.body?.let { body ->
                DeclarationIrBuilder(pluginContext, declaration.symbol).irBlockBody {
                    // 创建变量记录开始时间
                    val startTime = irTemporary(irCall(pluginContext.markNowFunc()).also {
                        it.dispatchReceiver = irGetObject(pluginContext.monotonicClass())
                    })
                    // 插入原有代码片段
                    +irBlock(resultType = declaration.returnType) {
                        for (statement in body.statements) {
                            +statement
                        }
                    }.transform(ReturnTransformer(pluginContext, declaration, startTime), null)
                }
            }
        }
        return super.visitFunctionNew(declaration)
    }

    // 这个transformer用来操作函数的return
    class ReturnTransformer(
        private val pluginContext: IrPluginContext,
        private val irFunction: IrFunction,
        private val startTime: IrValueDeclaration
    ): IrElementTransformerVoidWithContext() {
        override fun visitReturn(expression: IrReturn): IrExpression {
            println("visitReturn:: ${expression.render()}")
            if (expression.returnTargetSymbol != irFunction.symbol) //只 transform 目标函数
                return super.visitReturn(expression)

            println("transform return:: ")
            return DeclarationIrBuilder(pluginContext, irFunction.symbol).irBlock {
                fun exitLogic() {
                    val concat = irConcat()
                    concat.addArgument(irString("方法(${irFunction.name})执行耗时: "))
                    concat.addArgument(irCall(pluginContext.elapsedNowFunc()).also {
                        it.dispatchReceiver = irGet(startTime)
                    })
                    +irCall(pluginContext.printlnFunc()).also {
                        it.putValueArgument(0, concat)
                    }
                }
                if (irFunction.returnType == pluginContext.irBuiltIns.unitType) {
                    exitLogic()
                    return@irBlock
                }
                val result = irTemporary(expression.value) //保存返回表达式
                exitLogic()
                +expression.apply {
                    value = irGet(result) // 将原有的返回表达式补回
                }
            }
        }
    }
}