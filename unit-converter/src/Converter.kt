package converter

interface Converter {

    fun convert(from: Value): Value
}

class ConversionException(fromUnit: Unit?, targetUnit: Unit?) :
    RuntimeException(
        "Conversion from ${getUnitName(fromUnit)} to ${getUnitName(targetUnit)} is impossible"
    ) {
    companion object {
        private fun getUnitName(unit: Unit?): String = unit?.plural ?: "???"
    }
}

fun convert(source: Source): Value {
    return ConverterHolder
        .resolveConverter(source.from?.unit, source.to)
        .convert(source.from!!)
}

private abstract class BaseConverter(
    protected val fromUnit: Unit,
    protected val targetUnit: Unit
) : Converter {

    protected abstract fun calculate(fromValue: Double): Double

    override fun convert(from: Value): Value {
        validate(from)
        val resultValue = calculate(from.value)
        return Value(resultValue, targetUnit)
    }

    private fun validate(from: Value) {
        if (from.unit.type != targetUnit.type) {
            throw ConversionException(from.unit, targetUnit)
        }
    }
}

private open class LinearConverter(
    private val k: Double,
    private val b: Double,
    fromUnit: Unit,
    targetUnit: Unit
) : BaseConverter(fromUnit, targetUnit) {

    override fun calculate(fromValue: Double): Double = fromValue * k + b
}

private class ShiftConverter(
    b: Double,
    fromUnit: Unit,
    targetUnit: Unit
) : LinearConverter(1.0, b, fromUnit, targetUnit)

private class TrivialConverter(
    unit: Unit
) : LinearConverter(1.0, 0.0, unit, unit)

private class TwoStepConverter(
    fromUnit: Unit,
    targetUnit: Unit
) : BaseConverter(fromUnit, targetUnit) {

    override fun calculate(fromValue: Double): Double {
        val baseValue = fromValue * fromUnit.k
        return baseValue / targetUnit.k
    }
}

private object ConverterHolder {

    private const val ckShift = 273.15
    private const val cfk = 1.8
    private const val cfb = 32.0
    private const val kfb = -459.67

    private val temperatureConverterMapping: Map<Pair<Unit, Unit>, Converter> = mapOf(
        Pair(
            Pair(Unit.CELSIUS, Unit.KELVIN),
            ShiftConverter(ckShift, Unit.CELSIUS, Unit.KELVIN)
        ),
        Pair(
            Pair(Unit.KELVIN, Unit.CELSIUS),
            ShiftConverter(-ckShift, Unit.KELVIN, Unit.CELSIUS)
        ),
        Pair(
            Pair(Unit.CELSIUS, Unit.FAHRENHEIT),
            LinearConverter(cfk, cfb, Unit.CELSIUS, Unit.FAHRENHEIT)
        ),
        Pair(
            Pair(Unit.FAHRENHEIT, Unit.CELSIUS),
            LinearConverter(1 / cfk, -cfb / cfk, Unit.FAHRENHEIT, Unit.CELSIUS)
        ),
        Pair(
            Pair(Unit.KELVIN, Unit.FAHRENHEIT),
            LinearConverter(cfk, kfb, Unit.KELVIN, Unit.FAHRENHEIT)
        ),
        Pair(
            Pair(Unit.FAHRENHEIT, Unit.KELVIN),
            LinearConverter(1 / cfk, -kfb / cfk, Unit.FAHRENHEIT, Unit.KELVIN)
        )
    )

    fun resolveConverter(fromUnit: Unit?, targetUnit: Unit?): Converter {
        if (fromUnit == null || targetUnit == null || fromUnit.type != targetUnit.type) {
            throw ConversionException(fromUnit, targetUnit)
        }
        if (fromUnit == targetUnit) {
            return TrivialConverter(fromUnit)
        }
        if (fromUnit.type in arrayOf(Unit.Type.Length, Unit.Type.Weight)) {
            return TwoStepConverter(fromUnit, targetUnit)
        }
        return temperatureConverterMapping[Pair(fromUnit, targetUnit)]
            ?: throw ConversionException(fromUnit, targetUnit)
    }
}
