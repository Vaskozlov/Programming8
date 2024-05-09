package lib

import java.util.concurrent.atomic.AtomicInteger

/**
 * Creates unique ids in thread safe way
 */
class IdFactory(value: Int = 0) {
    private var currentId: AtomicInteger = AtomicInteger(value)

    fun setValue(value: Int) {
        currentId = AtomicInteger(value)
    }

    val nextId: Int
        get() = currentId.getAndIncrement()
}
