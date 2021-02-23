package converter

fun main() {
    var input: String
    while (true) {
        print("Enter what you want to convert (or exit): ")
        input = readLine()!!
        if (input == "exit") {
            return
        }
        processInput(input)
    }
}

private fun processInput(input: String) {
    try {
        val source = parse(input)
        val result = convert(source)
        println("${source.from} is $result\n")
    } catch (e: Exception) {
        println("${e.message}\n")
    }
}
