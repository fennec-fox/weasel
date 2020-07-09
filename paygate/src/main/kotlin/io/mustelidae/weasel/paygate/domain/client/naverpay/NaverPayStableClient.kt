package io.mustelidae.weasel.paygate.domain.client.naverpay

import com.github.kittinunf.fuel.Fuel
import io.mustelidae.weasel.paygate.config.PayGateClientException
import io.mustelidae.weasel.paygate.config.PayGateEnvironment
import io.mustelidae.weasel.paygate.domain.paygate.PayGate
import io.mustelidae.weasel.paygate.utils.ClientSupport
import org.slf4j.LoggerFactory

/**
 * 네이버 페이
 * - naver pay의 경우 결제 or 취소의 실패가 나더라도 이를 http method 2xx를 return 함.
 */
internal class NaverPayStableClient(
    private val environment: PayGateEnvironment.NaverPay
) : NaverPayClient, ClientSupport(
    environment.writeLog,
    LoggerFactory.getLogger(NaverPayStableClient::class.java)
) {
    private val company = PayGate.Company.NAVER_PAY

    private val urlPrefix = "${environment.host}/${environment.partnerId}"

    @Throws(PayGateClientException::class)
    override fun payment(request: NaverPayResources.Request.Pay): NaverPayResources.Reply.Paid {
        val params = listOf(Pair("paymentId", request.paymentId))

        val result = Fuel.post("$urlPrefix/naverpay/payments/v2.2/apply/payment", params)
            .header(getHeader(request.storeId, request.storeKey))
            .timeout(environment.timeout)
            .responseString()
            .writeLog()
            .orElseThrow { PayGateClientException(company, "네이버 페이 점검으로 인해 결제를 진행 할 수 없습니다. 다른 결제 수단을 선택해 주세요.", false) }

        val reply = NaverPayResources.Reply(result).apply {
            makeExceptionIfNotSuccess()
        }

        return reply.getBody()!!
    }

    @Throws(PayGateClientException::class)
    override fun cancel(request: NaverPayResources.Request.Cancel): NaverPayResources.Reply.Canceled {
        return this.cancelOfPartial(request, request.cancelAmount)
    }

    @Throws(PayGateClientException::class)
    override fun cancelOfPartial(request: NaverPayResources.Request.Cancel, cancellableAmount: Long): NaverPayResources.Reply.Canceled {
        val cancelRequester = if (request.isUserRequired) "1" else "2"

        val params = listOf(
            Pair("paymentId", request.paymentId),
            Pair("cancelAmount", request.cancelAmount),
            Pair("taxScopeAmount", request.cancelAmount),
            Pair("taxExScopeAmount", "0"),
            Pair("cancelRequester", cancelRequester),
            Pair("cancelReason", request.memo)
        )

        val result = Fuel.post("$urlPrefix/naverpay/payments/v1/cancel", params)
            .header(getHeader(request.storeId, request.storeKey))
            .timeout(environment.timeout)
            .responseString()
            .writeLog()
            .orElseThrow { PayGateClientException(company, "네이버 페이 점검으로 인해 취소가 현재 불가능합니다. 잠시 후 재시도를 부탁 드리며 문제 발생 시 고객센터로 연락 부탁드립니다.", false) }

        val reply = NaverPayResources.Reply(result).apply {
            makeExceptionIfNotSuccess()
        }

        val canceled = reply.getBody<NaverPayResources.Reply.Canceled>()!!

        // 취소 잔액 비교
        run {
            val remainAmt = cancellableAmount - request.cancelAmount

            if (canceled.detail.totalRestAmount != remainAmt)
                log.error("Mismatch naver pay totalRestAmount(${canceled.detail.totalRestAmount}), remainAmount($remainAmt)")
        }

        return canceled
    }

    private fun getHeader(storeId: String, storeKey: String): Map<String, Any> {
        return mapOf(
            "Content-Type" to "application/x-www-form-urlencoded; charset=utf-8",
            "X-Naver-Client-Id" to storeId,
            "X-Naver-Client-Secret" to environment.clientSecret,
            "X-NaverPay-Chain-Id" to storeKey,
            "X-NaverPay-Authorization" to "GRP"
        )
    }
}
