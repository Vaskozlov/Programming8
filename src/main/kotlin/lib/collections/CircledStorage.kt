package lib.collections

/**
 * Stores limited number of elements, when user attempts to add more overrides the oldest-added element
 */
class CircledStorage<T>(size: Int) {
    private val buffer: Array<T?>
    private var currentIndex = 0

    /**
     * @param size can not be less than 0
     */
    init {
        require(size >= 0) { "Size of CircleStorage can not contain less than 0 elements" }

        @Suppress("UNCHECKED_CAST")
        buffer = arrayOfNulls<Any?>(size) as Array<T?>
    }

    fun size(): Int {
        return buffer.size
    }

    fun getArray(): Array<T?> {
        return buffer
    }

    /**
     * Appends value to the CircleStorage, if storage is out of spaces overrides the oldest-added element
     */
    fun add(value: T) {
        set(value, currentIndex++)
    }

    fun applyFunctionOnAllElements(function: (T) -> Unit) {
        for (i in buffer.size downTo 1) {
            val elem: T? = get(currentIndex - i)

            if (elem != null) {
                function.invoke(elem)
            }
        }
    }

    private fun get(index: Int): T? {
        return buffer[getIndexInCircle(index)]
    }

    private fun set(value: T, index: Int) {
        buffer[getIndexInCircle(index)] = value
    }

    private fun getIndexInCircle(index: Int): Int {
        val result = index % buffer.size
        return if (result >= 0) result else result + buffer.size
    }
}
