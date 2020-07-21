package io.mustelidae.weasel.paygate.domain.client.kakaopay

import com.fasterxml.jackson.module.kotlin.readValue
import io.mustelidae.weasel.paygate.config.PayGateClientException
import io.mustelidae.weasel.paygate.config.PayGateEnvironment
import io.mustelidae.weasel.paygate.domain.client.AbstractDummySupport
import io.mustelidae.weasel.paygate.domain.paygate.PayGate
import io.mustelidae.weasel.paygate.utils.Jackson
import java.time.LocalDateTime
import java.time.ZoneOffset

internal class KakaoPayDummyClient(
    private val dummy: PayGateEnvironment.Dummy
) : KakaoPayClient, AbstractDummySupport() {

    private val mapper = Jackson.getMapper()

    override fun prepare(request: KakaoPayResources.Request.Prepare): KakaoPayResources.Reply.Prepared {

        return mapper.readValue(getBodyByPrepared())
    }

    override fun pay(request: KakaoPayResources.Request.Pay): KakaoPayResources.Reply.Paid {
        if (dummy.forcePayFail) {
            val json = getBodyByPaidFail()
            val error = mapper.readValue<KakaoPayResources.Error>(json)
            throw PayGateClientException(
                PayGate.Company.KAKAO_PAY,
                error.getErrorMessage(),
                true,
                error.code
            )
        }
        return mapper.readValue(getBodyByPaid(request))
    }

    override fun cancel(request: KakaoPayResources.Request.Cancel): KakaoPayResources.Reply.Canceled {
        return cancelOfPartial(request, request.cancelAmount)
    }

    override fun cancelOfPartial(
        request: KakaoPayResources.Request.Cancel,
        cancellableAmount: Long
    ): KakaoPayResources.Reply.Canceled {
        if (dummy.forceCancelFail) {
            val json = getBodyByPaidFail()
            val error = mapper.readValue<KakaoPayResources.Error>(json)

            throw PayGateClientException(
                PayGate.Company.KAKAO_PAY,
                error.getErrorMessage(),
                true,
                error.code
            )
        }

        return mapper.readValue(getBodyByCanceled(request, cancellableAmount))
    }

    private fun getBodyByPrepared(): String = """
        {
         "tid": "T${keyOfVan()}",
         "next_redirect_app_url": "https://mockup-pg-web.kakao.com/v1/xxxxxxxxxx/aInfo",
         "next_redirect_mobile_url": "https://mockup-pg-web.kakao.com/v1/xxxxxxxxxx/mInfo",
         "next_redirect_pc_url": "https://mockup-pg-web.kakao.com/v1/xxxxxxxxxx/info",
         "android_app_scheme": "kakaotalk://kakaopay/pg?url=https://mockup-pg-web.kakao.com/v1/xxxxxxxxxx/order",
         "ios_app_scheme": "kakaotalk://kakaopay/pg?url=https://mockup-pg-web.kakao.com/v1/xxxxxxxxxx/order",
         "created_at": "${LocalDateTime.now()}"
        }
    """.trimIndent()

    private fun getBodyByPaid(request: KakaoPayResources.Request.Pay): String = """
        {
         "aid": "A${LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)}",
         "tid": "${request.tid}",
         "cid": "${request.storeId}",
         "partner_order_id": "${request.token.orderId}",
         "partner_user_id": "${request.token.userId}",
         "payment_method_type": "MONEY",
         "item_name": "초코파이",
         "quantity": 1,
         "amount": {
          "total": ${request.token.paymentAmount.toLong()},
          "tax_free": 0,
          "vat": ${request.token.paymentAmount / 0.1},
          "point": 0
         },
         "created_at": "${LocalDateTime.now()}",
         "approved_at": "${LocalDateTime.now()}"
        }
    """.trimIndent()

    private fun getBodyByPaidFail(): String = """
        {
         "code": -780,
         "msg": "approval failure!",
         "extras": {
          "method_result_code": "USER_LOCKED",
          "method_result_message": "진행중인 거래가 있습니다. 잠시 후 다시 시도해 주세요."
         }
        }
    """.trimIndent()

    private fun getBodyByCanceled(request: KakaoPayResources.Request.Cancel, cancellableAmount: Long): String = """
            {
             "aid": "A${LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)}",
             "tid": "${request.tid}",
             "cid": "${request.cancelAmount}",
             "status": "CANCEL_PAYMENT",
             "partner_order_id": "",
             "partner_user_id": "",
             "payment_method_type": "MONEY",
             "item_name": "초코파이",
             "quantity": 1,
             "amount": {
              "total": ${request.cancelAmount},
              "tax_free": 0,
              "vat": ${request.cancelAmount * 0.1},
              "discount": 0
             },
             "canceled_amount": {
              "total": ${request.cancelAmount},
              "tax_free": 0,
              "vat": ${request.cancelAmount * 0.1},
              "discount": 0
             },
             "cancel_available_amount": {
              "total": ${cancellableAmount - request.cancelAmount},
              "tax_free": 0,
              "vat": ${(cancellableAmount - request.cancelAmount) * 0.1},
              "discount": 0
             },
             "created_at": "${LocalDateTime.now()}",
             "approved_at": "${LocalDateTime.now()}",
             "canceled_at": "${LocalDateTime.now()}"
            }
        """.trimIndent()
}
