package io.mustelidae.weasel.paygate.domain.client.kakaopay

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result
import io.mustelidae.weasel.paygate.config.PayGateClientException
import io.mustelidae.weasel.paygate.config.PayGateEnvironment
import io.mustelidae.weasel.paygate.domain.paygate.PayGate
import io.mustelidae.weasel.paygate.utils.ClientSupport
import io.mustelidae.weasel.paygate.utils.Jackson
import org.slf4j.LoggerFactory

/**
 * 카카오 페이
 * - kakao pay의 경우 결제 or 취소 실패가 나는 경우 이를 http method 5xx를 return 함.
 * - 실제 PG의 결제 실패 메시지를 알고 싶은 경우 5xx 에러 시 response body를 이용해야 한다.
 */
internal class KakaoPayStableClient(
    private val environment: PayGateEnvironment.KakaoPay
) : KakaoPayClient, ClientSupport(
    environment.writeLog,
    LoggerFactory.getLogger(KakaoPayStableClient::class.java)
) {

    private val headers = mapOf<String, Any>(
        "Content-Type" to "application/x-www-form-urlencoded; charset=utf-8",
        "Authorization" to "KakaoAK ${environment.apiKey}"
    )

    private val mapper: ObjectMapper = Jackson.getMapper()

    @Throws(PayGateClientException::class)
    override fun prepare(request: KakaoPayResources.Request.Prepare): KakaoPayResources.Reply.Prepared {
        val params = listOf(
            Pair("cid", request.storeId),
            Pair("partner_order_id", request.token.orderId),
            Pair("partner_user_id", request.token.userId),
            Pair("item_name", request.itemName),
            Pair("item_code", request.itemId),
            Pair("quantity", request.itemCount),
            Pair("total_amount", request.token.paymentAmount),
            Pair("tax_free_amount", 0),
            Pair("approval_url", request.approvalUrl),
            Pair("cancel_url", request.cancelUrl),
            Pair("fail_url", request.failUrl)
        )

        val (_, res, result) = Fuel.post("${environment.host}/v1/payment/ready", params)
            .header(headers)
            .timeout(environment.timeout)
            .responseString()
            .writeLog()

        makeExceptionIfNotSuccess(res, result)

        return mapper.readValue(result.component1()!!)
    }

    @Throws(PayGateClientException::class)
    override fun pay(request: KakaoPayResources.Request.Pay): KakaoPayResources.Reply.Paid {
        val params = listOf(
            Pair("cid", request.storeId),
            Pair("tid", request.tid),
            Pair("partner_order_id", request.token.orderId),
            Pair("partner_user_id", request.token.userId),
            Pair("pg_token", request.kakaoPayToken)
        )

        val (_, res, result) = Fuel.post("${environment.host}/v1/payment/approve", params)
            .header(headers)
            .timeout(environment.timeout)
            .responseString()
            .writeLog()

        makeExceptionIfNotSuccess(res, result)

        return mapper.readValue(result.component1()!!)
    }

    @Throws(PayGateClientException::class)
    override fun cancel(request: KakaoPayResources.Request.Cancel): KakaoPayResources.Reply.Canceled {
        return cancelOfPartial(request, request.cancelAmount)
    }

    @Throws(PayGateClientException::class)
    override fun cancelOfPartial(
        request: KakaoPayResources.Request.Cancel,
        cancellableAmount: Long
    ): KakaoPayResources.Reply.Canceled {

        val params = listOf(
            Pair("cid", request.storeId),
            Pair("tid", request.tid),
            Pair("cancel_amount", request.cancelAmount),
            Pair("cancel_tax_free_amount", 0)
        )

        val (_, res, result) = Fuel.post("${environment.host}/v1/payment/cancel", params)
            .header(headers)
            .timeout(environment.timeout)
            .responseString()
            .writeLog()

        makeExceptionIfNotSuccess(res, result)

        val canceled = mapper.readValue<KakaoPayResources.Reply.Canceled>(result.component1()!!)

        // 취소 잔액 비교
        run {
            val remainAmt = cancellableAmount - request.cancelAmount

            if (canceled.cancelAvailableAmount.total != remainAmt)
                log.error("Mismatch kakao pay totalRestAmount(${canceled.cancelAvailableAmount}), remainAmount($remainAmt)")
        }

        return canceled
    }

    private fun makeExceptionIfNotSuccess(
        res: Response,
        result: Result<String, FuelError>
    ) {
        if (res.isOk().not()) {
            val json = String(result.component2()!!.response.data, Charsets.UTF_8)
            val error = Jackson.getMapper().readValue<KakaoPayResources.Error>(json)
            throw PayGateClientException(
                PayGate.Company.KAKAO_PAY,
                error.getErrorMessage(),
                true,
                error.code
            )
        }
    }
}
