package com.aenadgrleey.kommander.core.utils

inline fun <T> safe(
    block: () -> T,
): T? = try {
    block()
} catch (e: Exception) {
    null
}
