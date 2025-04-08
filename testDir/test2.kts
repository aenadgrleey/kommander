#!/usr/bin/env kotlinc -script

import java.io.File

println("🔍 Kotlin Source Analyzer\n")

val sourceFiles = File(".")
    .walkTopDown()
    .filter { it.extension in listOf("kt", "kts") }
    .toList()

if (sourceFiles.isEmpty()) {
    println("❌ No Kotlin files found.")
    return
}

val importCounts = mutableMapOf<String, Int>()
var totalLines = 0

for (file in sourceFiles) {
    val lines = file.readLines()
    totalLines += lines.size

    lines.filter { it.trim().startsWith("import ") }
        .forEach { import ->
            val key = import.trim()
            importCounts[key] = importCounts.getOrDefault(key, 0) + 1
        }
}

println("📁 Found ${sourceFiles.size} Kotlin files.")
println("📏 Total lines: $totalLines")
println("📊 Average lines per file: ${totalLines / sourceFiles.size}")

println("\n🔥 Top 5 imports:")
importCounts.entries
    .sortedByDescending { it.value }
    .take(5)
    .forEach { (import, count) ->
 
    val task = Task(nextId++, desc, LocalDateTime.now())
    taskList += task
    println("✅ Added task #${task.id}")
}

fun markDone(idStr: String) {
    val id = idStr.toIntOrNull()
    if (id == null) {
        println("❌ Invalid ID: $idStr")
        return
    }

    val task = taskList.find { it.id == id }
    if (task == null) {
        println("❌ No task found with ID $id")
        return
    }

    task.done = true
    println("🎉 Task #$id marked as done.")
}

fun listTasks() {
    if (taskList.isEmpty()) {
        println("📭 No tasks yet.")
        return
    }

    println("🗂 Your Tasks:")
    for (task in taskList) {
        val status = if (task.done) "\u001B[32m✔\u001B[0m" else "\u001B[31m✘\u001B[0m"
        val desc = if (task.done) "\u001B[90m${task.description}\u001B[0m" else task.description
        println(" #${task.id.toString().padEnd(3)} $status  $desc (${task.createdAt.format(dateFormatter)})")
    }
}

fun handleCommand(input: String): Boolean {
    val parts = input.trim().split(" ", limit = 2)
    val command = parts[0]
    val arg = parts.getOrNull(1) ?: ""

    return when (command.lowercase(Locale.getDefault())) {
        "add" -> {
            addTask(arg)
            true
        }

        "done" -> {
            markDone(arg)
            true
        }

        "list" -> {
            listTasks()
            true
        }

        "help" -> {
            printHelp()
            true
        }

        "exit" -> {
            println("👋 Goodbye!")
            false
        }

        else -> {
            println("❓ Unknown command: $command")
            true
        }
    }
}

// Main loop
fun main() {
    printWelcome()
    val scanner = Scanner(System.`in
    while (true) {
        print("> ")
        if (!scanner.hasNextLine()) break
        val line = scanner.nextLine()
        if (!handleCommand(line)) break
    }
}

main()
