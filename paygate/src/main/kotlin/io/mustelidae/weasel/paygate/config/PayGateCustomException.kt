package io.mustelidae.weasel.paygate.config

import java.lang.RuntimeException

class PayGateClientException(message: String, val canRetry: Boolean, val causeBy: Map<String, Any>? = null) : RuntimeException(message)

class NotSupportPayMethodException(message: String) : RuntimeException(message)

class PayGateException(message: String) : RuntimeException(message)
