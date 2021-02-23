package converter

enum class Unit(
    val type: Type,
    val k: Double,
    val singular: String,
    val plural: String,
    vararg val tokens: String
) {
    METER(Type.Length, 1.0, "meter", "meters", "m"),
    KILOMETER(Type.Length, 1000.0, "kilometer", "kilometers", "km"),
    CENTIMETER(Type.Length, 0.01, "centimeter", "centimeters", "cm"),
    MILLIMETER(Type.Length, 0.001, "millimeter", "millimeters", "mm"),
    MILE(Type.Length, 1609.35, "mile", "miles", "mi"),
    YARD(Type.Length, 0.9144, "yard", "yards", "yd"),
    FOOT(Type.Length, 0.3048, "foot", "feet", "ft"),
    INCH(Type.Length, 0.0254, "inch", "inches", "in"),
    GRAM(Type.Weight, 1.0, "gram", "grams", "g"),
    KILOGRAM(Type.Weight, 1000.0, "kilogram", "kilograms", "kg"),
    MILLIGRAM(Type.Weight, 0.001, "milligram", "milligrams", "mg"),
    POUND(Type.Weight, 453.592, "pound", "pounds", "lb"),
    OUNCE(Type.Weight, 28.3495, "ounce", "ounces", "oz"),
    CELSIUS(Type.Temperature, 1.0, "degree Celsius", "degrees Celsius", "celsius", "dc", "c"),
    KELVIN(Type.Temperature, 1.0, "kelvin", "kelvins", "k"),
    FAHRENHEIT(Type.Temperature, 1.0, "degree Fahrenheit", "degrees Fahrenheit", "fahrenheit", "df", "f");

    enum class Type {
        Length, Weight, Temperature
    }
}

data class Value(val value: Double, val unit: Unit) {

    override fun toString(): String {
        val label = if (value.compareTo(1.0) == 0) unit.singular else unit.plural
        return "$value $label"
    }
}

data class Source(val from: Value?, val to: Unit?)
