package io.mustelidae.weasel.paygate.utils

import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.ResponseResultOf
import io.mustelidae.weasel.paygate.config.PayGateClientException
import java.lang.invoke.MethodHandles.throwException
import org.slf4j.Logger

internal open class ClientSupport(
    private val writeLog: Boolean,
    val log: Logger
) {

    fun ResponseResultOf<String>.orElseThrow(throwException: () -> PayGateClientException): String {
        val (_, res, result) = this

        if (res.isOk().not()) {
            val error = String(result.component2()!!.response.data)
            throw throwException().apply {
                pgCode = error
            }
        }

        return result.get()
    }

    fun ResponseResultOf<String>.writeLog(): ResponseResultOf<String> {
        val (req, res, result) = this

        if (writeLog && res.isOk())
            log.info("$req\n-----------\n$res")

        if (res.isOk().not()) {
            val msg = StringBuilder("$req\n-----------\n$res")
            result.component2()?.let {
                msg.append("\n-- error --\n${String(it.response.data)}")
            }
            log.error(msg.toString())
        }
        return this
    }

    fun Response.isOk(): Boolean {
        if (this.statusCode == -1)
            return false

        return (this.statusCode in 200..299)
    }
}
