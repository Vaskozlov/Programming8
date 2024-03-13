package lib

import kotlin.enums.enumEntries

@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T : Enum<T>> containsKey(key: String) =
    enumEntries<T>().map { it.name }.contains(key)

inline fun <reified T : Enum<T>> valueOrNull(key: String) =
    if (containsKey<T>(key)) enumValueOf<T>(key) else null
