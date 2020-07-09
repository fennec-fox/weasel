package io.mustelidae.weasel.paygate.config

import io.mustelidae.weasel.paygate.domain.paygate.PayGate
import java.lang.RuntimeException

open class PayGateException(message: String, open val causeBy: Map<String, Any?>? = null) : RuntimeException(message)

class PayGateClientException(company: PayGate.Company, message: String, val canRetry: Boolean, var pgCode: String? = null) : PayGateException(message, mapOf("paygate" to company, "pgCode" to pgCode))

class NotSupportPayMethodException(message: String) : RuntimeException(message)

class PayGateCertifyException(message: String) : RuntimeException(message)
