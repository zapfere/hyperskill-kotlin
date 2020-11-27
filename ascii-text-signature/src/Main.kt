package signature

import java.io.File
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.max

private const val borderChar = "8"
private const val leftBorder = "88  "
private const val rightBorder = "  88"
private const val borderWidth = leftBorder.length + rightBorder.length
private const val fontDir = "./ascii-text-signature/resources/"

class Letter(val char: Char, val width: Int, val matrix: Array<String>)
class Font(val height: Int, val chars: Map<Char, Letter>)

fun main() {
    val medium = readFontFile(fontDir + "medium.txt", 5)
    val roman = readFontFile(fontDir + "roman.txt", 10)
    val (nameLine, statusLine) = readNameAndStatus()
    printLabel(nameLine, roman, statusLine, medium)
}

private fun readFontFile(fileName: String, spaceSize: Int): Font {
    Scanner(File(fileName)).use { fileReader ->
        val params = fileReader.nextLine().split(' ')
        val height = params[0].toInt()
        val letterCount = params[1].toInt()
        val letters = HashMap<Char, Letter>()
        repeat(letterCount) {
            val letter = readLetter(height, fileReader)
            letters[letter.char] = letter
        }
        letters[' '] = whitespace(height, spaceSize)
        return Font(height, letters)
    }
}

private fun readLetter(height: Int, fileReader: Scanner): Letter {
    val params = fileReader.nextLine().split(' ')
    val char = params[0].first()
    val letterWidth = params[1].toInt()
    val matrix = height.downTo(1)
            .map { fileReader.nextLine() }
            .toTypedArray()
    return Letter(char, letterWidth, matrix)
}

private fun whitespace(height: Int, spaceSize: Int): Letter {
    val matrix = height.downTo(1)
            .map { " ".repeat(spaceSize) }
            .toTypedArray()
    return Letter(' ', spaceSize, matrix)
}

private fun readNameAndStatus(): Pair<String, String> {
    val scanner = Scanner(System.`in`)
    print("Enter name and surname: ")
    val nameLine = scanner.nextLine()
    print("Enter person's status: ")
    val statusLine = scanner.nextLine()
    return Pair(nameLine, statusLine)
}

private fun printLabel(nameLine: String, roman: Font,
                       statusLine: String, medium: Font) {
    val lineLength = calculateLineLength(nameLine, roman, statusLine, medium)
    val hLine = getHLine(lineLength)
    println(hLine)
    printLine(nameLine, lineLength, roman)
    printLine(statusLine, lineLength, medium)
    println(hLine)
}

private fun calculateLineLength(input1: String, font1: Font, input2: String, font2: Font): Int {
    val firstLineLength = getLineLength(input1, font1)
    val secondLineLength = getLineLength(input2, font2)
    return max(firstLineLength, secondLineLength)
}

private fun getLineLength(line: String, font: Font): Int {
    return line.toCharArray()
            .map { font.chars.getValue(it).width }
            .sum()
}

private fun getHLine(length: Int): String {
    return borderChar.repeat(length + borderWidth)
}


private fun printLine(line: String, size: Int, font: Font) {
    for (i in 0 until font.height) {
        val linePart = line.toCharArray()
                .map(font.chars::getValue)
                .joinToString(separator = "") { it.matrix[i] }
        printLine(linePart, size)
    }
}

private fun printLine(line: String, size: Int) {
    print(leftBorder)
    var delta = 0
    var additionalSpace = false
    if (line.length < size) {
        val diff = size - line.length
        delta = diff / 2
        additionalSpace = diff % 2 == 1
    }
    print(" ".repeat(delta))
    print(line)
    if (additionalSpace) delta++
    print(" ".repeat(delta))
    println(rightBorder)
}
