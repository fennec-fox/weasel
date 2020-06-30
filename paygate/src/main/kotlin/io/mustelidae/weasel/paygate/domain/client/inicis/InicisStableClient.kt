package io.mustelidae.weasel.paygate.domain.client.inicis

import com.github.kittinunf.fuel.Fuel
import com.google.common.base.Splitter
import io.mustelidae.weasel.paygate.config.PayGateClientException
import io.mustelidae.weasel.paygate.config.PayGateEnvironment
import io.mustelidae.weasel.paygate.utils.ClientSupport
import io.mustelidae.weasel.paygate.utils.invokeValue
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.slf4j.LoggerFactory

internal class InicisStableClient(
    private val environment: PayGateEnvironment.Inicis
) : InicisClient, ClientSupport(
    environment.writeLog,
    LoggerFactory.getLogger(InicisStableClient::class.java)
) {

    @Throws(PayGateClientException::class)
    override fun payment(request: InicisResources.Request.Pay): InicisResources.Reply.Paid {
        val params = listOf(
            Pair("P_TID", request.tid),
            Pair("P_MID", request.storeId)
        )
        val result = Fuel.post(request.requestUrl, params)
            .timeout(environment.timeout)
            .responseString()
            .writeLog()
            .orElseThrow("이니시스 통신 문제로 인해 결제를 진행 할 수 없습니다. 다른 결제 수단을 선택해 주세요")

        @Suppress("UnstableApiUsage")
        val responseMap = Splitter.on("&")
            .withKeyValueSeparator("=")
            .split(result)

        if (responseMap.getValue("P_STATUS") != "00") {
            val message = responseMap.getValue("P_RMESG1")
            throw PayGateClientException(message, true)
        }

        return InicisResources.Reply.Paid.from(responseMap)
    }

    override fun cancel(request: InicisResources.Request.Cancel): InicisResources.Reply.Canceled {
        val iniPay = MockInicisModule() // FIXME: 이니시스 라이브러리로 교체해야 한다.

        invokeValue(iniPay, "log", log) // change default logger
        iniPay.SetField("inipayhome", environment.libPath)
        iniPay.SetField("type", "cancel")
        iniPay.SetField("debug", "true")
        iniPay.SetField("mid", request.storeId)
        iniPay.SetField("admin", "1111")
        iniPay.SetField("tid", request.tid)
        iniPay.SetField("cancelmsg", request.memo ?: "")
        iniPay.SetField("crypto", "execure")

        iniPay.startAction()
        val date = iniPay.GetResult("CancelDate")
        val time = iniPay.GetResult("CancelTime")

        if (iniPay.GetResult("ResultCode") != "00") {
            val message = iniPay.GetResult("ResultMsg")
            throw PayGateClientException(message, false)
        }

        return InicisResources.Reply.Canceled(
            request.tid,
            LocalDateTime.parse(date + time, DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
            request.cancelAmount,
            0
        )
    }

    override fun cancelOfPartial(
        request: InicisResources.Request.Cancel,
        cancellableAmount: Long
    ): InicisResources.Reply.Canceled {
        val iniPay = MockInicisModule()
        invokeValue(iniPay, "log", log) // change default logger

        iniPay.SetField("inipayhome", environment.libPath)
        iniPay.SetField("type", "repay")
        iniPay.SetField("debug", "true")
        iniPay.SetField("mid", request.storeId)
        iniPay.SetField("admin", "1111")
        iniPay.SetField("oldtid", request.tid)
        iniPay.SetField("currency", "WON")
        iniPay.SetField("price", request.cancelAmount.toString())
        iniPay.SetField("confirm_price", (cancellableAmount - request.cancelAmount).toString())
        iniPay.SetField("crypto", "execure") // 익스트러스 암복호화 모듈 설정

        iniPay.startAction()
        val newTxNo = iniPay.GetResult("TID")
        val remainAmt = iniPay.GetResult("PRTC_Remains").toLong()
        val canceledAmt = iniPay.GetResult("PRTC_Price").toLong()
        val time = iniPay.GetResult("CancelTime") // FIXME: 동작 확인 필요
        val date = iniPay.GetResult("CancelDate")

        if (iniPay.GetResult("ResultCode") != "00") {
            val message = iniPay.GetResult("ResultMsg")
            throw PayGateClientException(message, false)
        }

        if (request.cancelAmount != canceledAmt || remainAmt != cancellableAmount - request.cancelAmount) {
            log.error("${request.cancelAmount} != $canceledAmt or $remainAmt != $cancellableAmount - ${request.cancelAmount}")
        }

        return InicisResources.Reply.Canceled(
            newTxNo,
            LocalDateTime.parse(date + time, DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
            canceledAmt,
            remainAmt
        )
    }
}
