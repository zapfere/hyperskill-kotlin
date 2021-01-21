package tictactoe


fun main() {
    val field = Field()

    do {
        field.print()
        field.makeMove()
    } while (field.checkResult().notFinished)

    field.print()
    println(field.checkResult().description)
}
