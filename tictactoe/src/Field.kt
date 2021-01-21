package tictactoe

import java.util.*
import kotlin.math.abs


private const val size = 3
private const val x = 'X'
private const val o = 'O'
private const val empty = '_'
private val emptyInput = "".padEnd(size * size, empty)

class Field(input: String) {

    private val cells: Array<CharArray>
    private val horizontalLine = "".padEnd(3 + 2 * size, '-')
    private val resolver: ResultResolver
    private val checker: MoveChecker
    private val selector = MarkSelector()

    init {
        validate(input)
        cells = arrayOf(
            parseRow(input, 0, 2),
            parseRow(input, 3, 5),
            parseRow(input, 6, 8)
        )
        resolver = ResultResolver(cells)
        checker = MoveChecker(cells)
    }

    constructor() : this(emptyInput)

    private fun validate(input: String) {
        val cellCount = size * size
        if (input.length != cellCount) {
            throw IllegalArgumentException("String with $cellCount chars expected")
        }
        val allowedChars = charArrayOf(x, o, empty)
        input.forEach {
            if (it !in allowedChars) {
                throw IllegalArgumentException("Allowed chars: $x, $o, $empty")
            }
        }
    }

    private fun parseRow(input: String, fromIndex: Int, toIndex: Int): CharArray {
        return input.slice(IntRange(fromIndex, toIndex)).toCharArray()
    }

    fun print() {
        println(horizontalLine)
        for (row in cells) {
            println("| ${row.joinToString(" ")} |")
        }
        println(horizontalLine)
    }

    fun checkResult(): Result {
        return resolver.checkResult()
    }

    fun makeMove() {
        val (posX, posY) = checker.getMove()
        cells[posX][posY] = selector.getNextMark()
    }

}

enum class Result(val description: String, val notFinished: Boolean) {
    NOT_FINISHED("Game not finished", true),
    DRAW("Draw", false),
    X_WINS("X wins", false),
    O_WINS("O wins", false),
    IMPOSSIBLE("Impossible", false)
}

private class ResultResolver(private val rows: Array<CharArray>) {

    val xRow = CharArray(size) { x }
    val oRow = CharArray(size) { o }

    fun checkResult(): Result {
        val xWins = checkWin(x, xRow)
        val oWins = checkWin(o, oRow)
        if (xWins && oWins) {
            return Result.IMPOSSIBLE
        }
        if (xWins) {
            return Result.X_WINS
        }
        if (oWins) {
            return Result.O_WINS
        }
        return resolveOthers()
    }

    private fun checkWin(mark: Char, row: CharArray): Boolean {
        return (rows.any { it.contentEquals(row) })
                || checkColumns(mark) || checkDiagonals(mark)
    }

    private fun checkColumns(mark: Char): Boolean {
        for (i in 0 until size) {
            var count = 0
            for (j in 0 until size) {
                if (rows[j][i] == mark) {
                    count++
                }
            }
            if (count == size) {
                return true
            }
        }
        return false
    }

    private fun checkDiagonals(mark: Char): Boolean {
        val indices = IntRange(0, size - 1)
        if (indices.all { rows[it][it] == mark }) {
            return true
        }
        return indices.all { rows[it][size - it - 1] == mark }
    }

    private fun resolveOthers(): Result {
        val draw = checkDraw()
        if (draw && isInProgress()) {
            return Result.NOT_FINISHED
        }
        return if (draw) {
            Result.DRAW
        } else {
            Result.IMPOSSIBLE
        }
    }

    private fun isInProgress() = rows.any { cells -> cells.any { empty == it } }

    private fun checkDraw(): Boolean {
        val (xCount, oCount) = countMarksInField()
        return abs(xCount - oCount) <= 1
    }

    private fun countMarksInField(): Pair<Int, Int> {
        var xCount = 0
        var oCount = 0
        for (row in rows) {
            val (xs, os) = countMarksInRow(row)
            xCount += xs
            oCount += os
        }
        return Pair(xCount, oCount)
    }

    private fun countMarksInRow(row: CharArray): Pair<Int, Int> {
        var xs = 0
        var os = 0
        for (cell in row) {
            if (cell == x) {
                xs++
            }
            if (cell == o) {
                os++
            }
        }
        return Pair(xs, os)
    }
}

private class MoveChecker(val cells: Array<CharArray>) {

    private val scanner = Scanner(System.`in`)

    fun getMove(): Pair<Int, Int> {
        var result: Pair<Int, Int>?
        do {
            result = tryGetMove()
        } while (result == null)
        return result
    }

    private fun tryGetMove(): Pair<Int, Int>? {
        try {
            print("Enter the coordinates: ")
            return getCoordinates()
        } catch (e: NumberFormatException) {
            println("You should enter numbers!")
        } catch (e: IllegalArgumentException) {
            println("Coordinates should be from 1 to $size!")
        } catch (e: IllegalStateException) {
            println("This cell is occupied! Choose another one!")
        }
        return null
    }

    private fun getCoordinates(): Pair<Int, Int> {
        val (posX, posY) = readValues()
        validateRange(posX, posY)
        validateEmpty(posX, posY)
        return Pair(posX, posY)
    }

    private fun readValues(): Pair<Int, Int> {
        val input = scanner.nextLine().split(' ')
        if (input.size != 2) {
            throw NumberFormatException()
        }
        val posX = input[0].toInt() - 1
        val posY = input[1].toInt() - 1
        return Pair(posX, posY)
    }

    private fun validateRange(newX: Int, newY: Int) {
        if (newX !in 0 until size || newY !in 0 until size) {
            throw IllegalArgumentException()
        }
    }

    private fun validateEmpty(posX: Int, posY: Int) {
        if (cells[posX][posY] != empty) {
            throw IllegalStateException()
        }
    }
}

private class MarkSelector {

    private var currentMark = o

    fun getNextMark(): Char {
        currentMark = if (currentMark == x) {
            o
        } else {
            x
        }
        return currentMark
    }
}
