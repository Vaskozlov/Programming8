package lib

import java.util.concurrent.atomic.AtomicInteger

/**
 * Creates unique ids in thread safe way
 */
class IdFactory constructor(value: Int = 0) {
    private var currentId: AtomicInteger

    init {
        this.currentId = AtomicInteger(value)
    }

    /**
     * sets base value for factory
     */
    fun setValue(value: Int) {
        currentId = AtomicInteger(value)
    }

    /**
     * @return unique id
     */
    val nextId: Int
        get() = currentId.getAndIncrement()
}
