package tictactoe

import kotlin.math.abs

typealias Board = List<List<Char>>

enum class GameState {
    IMPOSSIBLE,
    X_WINS,
    O_WINS,
    DRAW,
    NOT_FINISHED,
}

fun main() {
    val input = "_________"
    var board = input.chunked(3).map { it.toList() }

    printBoard(board)
    for (player in "XOXOXOXOX") {
        board = promptForMove(board, player)
        printBoard(board)
        if (
            evaluateState(board) in listOf(
                GameState.X_WINS, GameState.O_WINS
            )
        ) break
    }
    printState(board)
}

fun updateBoard(board: Board, newCharacter: Char, coordinates: Pair<Int, Int>): Board {
    val (rowNumber, columnNumber) = coordinates
    return board.mapIndexed { r, row ->
        row.mapIndexed { c, square ->
            if (r == rowNumber && c == columnNumber) newCharacter else square
        }
    }
}

fun parseCoordinates(board: Board, input: String): Pair<Int, Int> {
    val (rowNumber, columnNumber) = try {
        input.split(" ").map { it.toInt() - 1 }
    } catch (e: Exception) {
        throw Exception("You should enter numbers!")
    }

    if (rowNumber !in 0..2 || columnNumber !in 0..2) {
        throw Exception("Coordinates should be from 1 to 3")
    }

    if (board[rowNumber][columnNumber] != '_') {
        throw Exception("This cell is occupied!")
    }

    return Pair(rowNumber, columnNumber)
}

fun promptForMove(board: Board, player: Char): Board {
    while (true) {
        print("Enter the coordinates: ")
        val input = readLine()!!

        try {
            val coordinates = parseCoordinates(board, input)
            return updateBoard(board, player, coordinates)
        } catch (e: Exception) {
            println(e.message)
        }
    }
}

fun evaluateState(board: Board): GameState {
    val countX = board.sumOf { row -> row.count { it == 'X' } }
    val countO = board.sumOf { row -> row.count { it == 'O' } }
    if (abs(countX - countO) > 1) return GameState.IMPOSSIBLE

    val rows = board
    val columns = board[0]
        .indices.map { i ->
            board.map { row -> row[i] }
        }
    val diagonals = listOf(
        board.indices.map { board[it][it] },
        board.indices.map { board[it][board.lastIndex - it] },
    )
    val lines = (rows + columns + diagonals)
        .map { it.joinToString("") }

    val winners = lines
        .filter { it in listOf("XXX", "OOO") }
        .map { it.first() }
        .toSet()

    return when (winners.size) {
        0 -> if (countX + countO == 9) GameState.DRAW else GameState.NOT_FINISHED
        1 -> if ('X' in winners) GameState.X_WINS else GameState.O_WINS
        else -> GameState.IMPOSSIBLE
    }
}

fun printState(board: Board) {
    val state = evaluateState(board)
    println(
        when (state) {
            GameState.IMPOSSIBLE -> "Impossible"
            GameState.X_WINS -> "X wins"
            GameState.O_WINS -> "O wins"
            GameState.DRAW -> "Draw"
            GameState.NOT_FINISHED -> "Game not finished"
        }
    )
}

fun printBoard(board: Board) {
    println("---------")
    board.forEach { row ->
        val marks = row.map { if (it in "XO") it else ' ' }
        println("| ${marks.joinToString(" ")} |")
    }
    println("---------")
}

