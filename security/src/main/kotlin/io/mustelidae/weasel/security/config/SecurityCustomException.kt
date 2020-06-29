package io.mustelidae.weasel.security.config

import java.lang.RuntimeException


class UserLockException(message: String, val causeBy: Map<String, Any>? = null) : RuntimeException(message)
