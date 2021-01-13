package cinema

import java.util.*


private const val threshold = 60
private const val firstRowsPrice = 10
private const val lastRowsPrice = 8

fun main() {
    val scanner = Scanner(System.`in`)
    val cinema = CinemaHall(scanner)
    performActions(scanner, cinema)
}

private fun performActions(scanner: Scanner, cinema: CinemaHall) {
    var choice: Int
    do {
        println(
            "\n1. Show the seats\n2. Buy a ticket\n3. Statistics\n0. Exit"
        )
        choice = performAction(scanner, cinema)
    } while (choice != 0)
}

private fun performAction(scanner: Scanner, cinema: CinemaHall): Int {
    val choice = scanner.nextInt()
    when (choice) {
        1 -> cinema.print()
        2 -> cinema.buySeat()
        3 -> cinema.statistics()
    }
    return choice
}

class CinemaHall(private val scanner: Scanner) {

    private val seatIndices: IntArray
    private val rowIndices: IntArray
    private val seats: Array<CharArray>
    private val isLarge: Boolean
    private val profit: Int
    private var seatsPurchased = 0
    private var income = 0

    init {
        println("Enter the number of rows:")
        val rows = scanner.nextInt()
        println("Enter the number of seats in each row:")
        val seatsInRow = scanner.nextInt()
        seatIndices = IntArray(seatsInRow) { it + 1 }
        rowIndices = IntArray(rows) { it + 1 }
        seats = Array(rows) { CharArray(seatsInRow) { 'S' } }
        isLarge = rows * seatsInRow > threshold
        profit = calculateProfit()
    }

    fun print() {
        println("\nCinema:")
        println("  " + seatIndices.joinToString(" "))
        repeat(rowIndices.size) {
            println("${rowIndices[it]} ${seats[it].joinToString(" ")}")
        }
    }

    fun buySeat() {
        try {
            tryBuySeat()
            return
        } catch (e: ArrayIndexOutOfBoundsException) {
            println("\nWrong input!")
        } catch (e: IllegalStateException) {
            println(e.message)
        }
        buySeat()
    }

    private fun tryBuySeat() {
        println("\nEnter a row number:")
        val row = scanner.nextInt() - 1
        println("Enter a seat number in that row:")
        val seat = scanner.nextInt() - 1
        val price = getSeatPrice(row)
        if (seats[row][seat] == 'B') {
            throw IllegalStateException(
                "\nThat ticket has already been purchased!"
            )
        }
        println("\nTicket price: $$price")
        seats[row][seat] = 'B'
        seatsPurchased++
        income += price
    }

    private fun getSeatPrice(row: Int): Int {
        return if (isLarge && row + 1 > rowIndices.size / 2) {
            lastRowsPrice
        } else {
            firstRowsPrice
        }
    }

    fun statistics() {
        println("\nNumber of purchased tickets: $seatsPurchased")
        val pct = (100.0 * seatsPurchased) /
                (rowIndices.size * seatIndices.size)
        System.`out`.format(Locale.US, "Percentage: %.02f%%%n", pct)
        println("Current income: $$income")
        println("Total income: $$profit")
    }

    private fun calculateProfit(): Int {
        val rows = rowIndices.size
        val seatsInRow = seatIndices.size
        return if (isLarge) {
            val halfRows = rows / 2
            halfRows * seatsInRow * firstRowsPrice +
                    (rows - halfRows) * seatsInRow * lastRowsPrice
        } else {
            rows * seatsInRow * firstRowsPrice
        }
    }
}
