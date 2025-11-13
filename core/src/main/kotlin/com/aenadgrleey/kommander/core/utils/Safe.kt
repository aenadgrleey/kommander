package com.aenadgrleey.kommander.core.utils

inline fun <T> safe(
    block: () -> T,
): T? = try {
    block()
} catch (e: Exception) {
    null
}

inline fun <T, R> T.logRunCatching(
    block: T.() -> R,
) = runCatching { block() }
    .onFailure { it.printStackTrace() }

inline fun <T> T?.orElse(
    block: () -> T,
): T = this ?: block()
