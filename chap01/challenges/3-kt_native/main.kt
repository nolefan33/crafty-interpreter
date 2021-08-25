import kotlin.system.exitProcess

fun main() {
    val list = StringLinkedList()

    while (true) {
        print("1. Insert\n2. Find\n3. Delete Position (0-indexed)\n4. Delete Value\n5. Print List\n6. Exit\nChoose an option: ")
        val selection = readLine()?.toIntOrNull() ?: 0
        when (selection) {
            1 -> {
                print("What should be added? ")
                readLine()?.let { list.insertEnd(it) }
            }
            2 -> {
                print("What are you looking for? ")
                readLine()?.let { searchValue ->
                    val pos = list.find(searchValue)
                    if (pos < 0) {
                        println("String \"${searchValue}\" not found")
                    } else {
                        println("String \"${searchValue}\" is at position $pos")
                    }
                }
            }
            3 -> {
                print("Which position should be deleted? ")
                val deletePos = readLine()?.toIntOrNull() ?: 0
                list.delete(deletePos)
            }
            4 -> {
                print("Which item should be deleted? ")
                val deleteValue = readLine() ?: ""
                val deletePos = list.find(deleteValue)
                list.delete(deletePos)
            }
            5 -> println(list)
            6 -> {
                println("Bye!")
                exitProcess(0)
            }
            else -> println("Option not recognized.")
        }
    }
}

class StringLinkedList {
    private var size = 0
    var root: StringNode? = null
        private set

    fun find(searchValue: String): Int {
        var searchNode = root
        var pos = -1
        var count = 0

        while (searchNode != null) {
            if (searchNode.value == searchValue) {
                pos = count
                break
            }
            count++
            searchNode = searchNode.next
        }

        return pos
    }

    fun insertEnd(value: String) {
        val node = StringNode(value = value)
        lastElem()?.let { lastNode ->
            lastNode.next = node
            node.prev = lastNode
        } ?: run { root = node }
    }

    // TODO doesn't delete position 0
    fun delete(pos: Int) {
        if (pos < 0) return

        var searchNode = root
        for (i in 0 until pos) {
            if (searchNode == null) {
                return
            } else {
                searchNode = searchNode.next   
            }
        }
        
        searchNode?.next?.prev = searchNode?.prev
        searchNode?.prev?.next = searchNode?.next
    }

    override fun toString(): String {
        val items = mutableListOf<String>()
        var iterElem = root
        while (iterElem != null) {
            items.add(iterElem.value)
            iterElem = iterElem.next
        }

        return items.joinToString(", ") { "\"$it\"" }
    }

    private fun lastElem(): StringNode? {
        return if (root == null) {
            null
        } else {
            var searchElem = root!!
            while (searchElem.next != null) {
                searchElem = searchElem.next!!
            }
            searchElem
        }
    }
}

data class StringNode(
    var prev: StringNode? = null,
    var next: StringNode? = null,
    val value: String
)
