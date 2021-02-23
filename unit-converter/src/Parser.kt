package converter

class ParseException : RuntimeException("Parse error")
class NegativeValueException(type: Unit.Type) : RuntimeException("$type shouldn't be negative")

fun parse(input: String): Source {
    return Parser(input).parse()
}

private class Parser(input: String) {

    companion object {
        private val degreePrefix = listOf("degree", "degrees")
        private val nonNegativeTypes = listOf(Unit.Type.Length, Unit.Type.Weight)
        private val tokenToUnitMapper = Unit.values()
            .map { Pair(it, listOf(it.singular, it.plural, *it.tokens)) }
            .flatMap { p -> p.second.asSequence().map { Pair(it.toLowerCase(), p.first) } }
            .toMap()
    }

    private val tokens = input.trim().split(' ')
    private var currentPos = 0

    fun parse(): Source {
        if (tokens.size < 4 || tokens.size > 6) {
            throw ParseException()
        }
        val value = parseValue()
        val unit = parseUnit()
        validate(value, unit)
        currentPos++  // skip auxiliary word
        val targetUnit = parseUnit()
        val from = if (unit == null) null else Value(value, unit)
        return Source(from, targetUnit)
    }

    private fun parseValue(): Double {
        try {
            val value = tokens[currentPos].toDouble()
            currentPos++
            return value
        } catch (e: NumberFormatException) {
            throw ParseException()
        }
    }

    private fun parseUnit(): Unit? {
        val name = extractName()
        currentPos++
        return unitFromString(name)
    }

    private fun extractName(): String {
        val token = tokens[currentPos].toLowerCase()
        if (token !in degreePrefix) {
            return token
        }
        currentPos++
        return "$token ${tokens[currentPos]}"
    }

    private fun unitFromString(value: String): Unit? {
        val lower = value.toLowerCase()
        return tokenToUnitMapper[lower]
    }

    private fun validate(value: Double, unit: Unit?) {
        if (value < 0.0 && unit != null && unit.type in nonNegativeTypes) {
            throw NegativeValueException(unit.type)
        }
    }
}
