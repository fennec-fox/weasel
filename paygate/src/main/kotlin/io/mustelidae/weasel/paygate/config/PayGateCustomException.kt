package io.mustelidae.weasel.paygate.config

import java.lang.RuntimeException

open class PayGateException(message: String, open val causeBy: Map<String, Any>? = null) : RuntimeException(message)

class PayGateClientException(message: String, val canRetry: Boolean, override val causeBy: Map<String, Any>? = null) : PayGateException(message, causeBy)

class NotSupportPayMethodException(message: String) : RuntimeException(message)

class PayGateCertifyException(message: String) : RuntimeException(message)
