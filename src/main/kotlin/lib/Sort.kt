package lib

inline fun <T, R : Comparable<R>> Iterable<T>.sortedByUpOrDown(
    reverse: Boolean,
    crossinline selector: (T) -> R?
): List<T> {
    return if (reverse) this.sortedBy(selector) else this.sortedByDescending(selector)
}