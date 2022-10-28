package team.redrock.rain.kcp.utils

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.name.FqName

/**
 * team.redrock.rain.kcp.utils.IrBuiltinHelper.kt
 * kcp-learning
 *
 * @author 寒雨
 * @since 2022/10/27 下午8:44
 */
fun IrPluginContext.printlnFunc(): IrSimpleFunctionSymbol = referenceFunctions(FqName("kotlin.io.println")).single {
    val parameters = it.owner.valueParameters
    parameters.size == 1 && parameters[0].type == irBuiltIns.anyNType
}

fun IrPluginContext.monotonicClass(): IrClassSymbol = referenceClass(FqName("kotlin.time.TimeSource.Monotonic"))!!

fun IrPluginContext.elapsedNowFunc() = referenceFunctions(FqName("kotlin.time.TimeMark.elapsedNow")).single()

fun IrPluginContext.markNowFunc(): IrSimpleFunctionSymbol = referenceFunctions(FqName("kotlin.time.TimeSource.markNow")).single()