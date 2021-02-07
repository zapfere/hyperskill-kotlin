package machine

class CoffeeMachine {

    private var water: Int = 400
    private var milk: Int = 540
    private var coffee: Int = 120
    private var money: Int = 550
    private var cups: Int = 9
    private var state: State = State.CHOOSING_ACTION
    val prompt: String
        get() = state.message
    val running: Boolean
        get() = state != State.FINISHED

    private enum class State(val message: String) {
        CHOOSING_ACTION("Write action (buy, fill, take, remaining, exit): "),
        CHOOSING_COFFEE(
            "\nWhat do you want to buy? 1 - espresso, 2 - latte, " +
                    "3 - cappuccino, back - to main menu: "
        ),
        FILLING_WATER("Write how many ml of water do you want to add: "),
        FILLING_MILK("Write how many ml of milk do you want to add: "),
        FILLING_COFFEE("Write how many grams of coffee beans do you want to add: "),
        FILLING_CUPS("Write how many disposable cups of coffee do you want to add: "),
        FINISHED("")
    }

    private enum class Coffee(
        val water: Int,
        val milk: Int,
        val coffee: Int,
        val cost: Int
    ) {
        ESPRESSO(250, 0, 16, 4),
        LATTE(350, 75, 20, 7),
        CAPPUCCINO(200, 100, 12, 6);

        companion object {
            val allowed = values().map { (it.ordinal + 1).toString() }.toTypedArray()
            fun fromCode(code: String) = values()[code.toInt() - 1]
        }
    }

    fun processCommand(input: String) {
        when (state) {
            State.CHOOSING_ACTION -> performAction(input)
            State.CHOOSING_COFFEE -> buy(input)
            State.FILLING_WATER -> water += fill(input, State.FILLING_MILK)
            State.FILLING_MILK -> milk += fill(input, State.FILLING_COFFEE)
            State.FILLING_COFFEE -> coffee += fill(input, State.FILLING_CUPS)
            State.FILLING_CUPS -> fillCups(input)
            State.FINISHED -> Unit
        }
    }

    private fun performAction(action: String) {
        when (action) {
            "buy" -> state = State.CHOOSING_COFFEE
            "fill" -> fill()
            "take" -> take()
            "remaining" -> printStock()
            "exit" -> state = State.FINISHED
            else -> println("Unknown action $action, try again")
        }
    }

    private fun fill() {
        println()
        state = State.FILLING_WATER
    }

    private fun take() {
        println("\nI gave you \$$money\n")
        money = 0
    }

    private fun printStock() {
        println()
        println(
            """
        The coffee machine has:
        $water of water
        $milk of milk
        $coffee of coffee beans
        $cups of disposable cups
        $$money of money
        """.trimIndent()
        )
        println()
    }

    private fun buy(position: String) {
        if (position == "back") {
            state = State.CHOOSING_ACTION
            return
        }
        if (position !in Coffee.allowed) {
            println("Unknown option, try again")
            return
        }
        pourChosenCoffee(Coffee.fromCode(position))
        state = State.CHOOSING_ACTION
    }

    private fun pourChosenCoffee(choice: Coffee) {
        if (notEnough(choice)) {
            return
        }
        water -= choice.water
        milk -= choice.milk
        coffee -= choice.coffee
        cups--
        money += choice.cost
    }

    private fun notEnough(choice: Coffee): Boolean {
        var result: Boolean = notEnough("water", choice.water, water)
        result = result || notEnough("milk", choice.milk, milk)
        result = result || notEnough("coffee beans", choice.coffee, coffee)
        result = result || notEnough("disposable cups", 1, cups)
        if (!result) {
            println("I have enough resources, making you a coffee!\n")
        }
        return result
    }

    private fun notEnough(resource: String, required: Int, inStock: Int): Boolean {
        if (required > inStock) {
            println("Sorry, not enough $resource!\n")
            return true
        }
        return false
    }

    private fun fill(value: String, nextState: State): Int {
        return try {
            val result = value.toInt()
            state = nextState
            result
        } catch (e: NumberFormatException) {
            print("Unsupported value, try again\n")
            0
        }
    }

    private fun fillCups(input: String) {
        cups += fill(input, State.CHOOSING_ACTION)
        println()
    }
}

fun main() {
    val coffeeMachine = CoffeeMachine()
    while (coffeeMachine.running) {
        print(coffeeMachine.prompt)
        coffeeMachine.processCommand(readLine()!!)
    }
}
