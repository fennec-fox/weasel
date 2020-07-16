package io.mustelidae.weasel.paygate.domain.client.naverpay

import io.mustelidae.weasel.common.code.BankCode
import io.mustelidae.weasel.common.code.CreditCode
import io.mustelidae.weasel.paygate.config.PayGateEnvironment
import io.mustelidae.weasel.paygate.domain.client.AbstractDummySupport
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

internal class NaverPayDummyClient(
    private val dummy: PayGateEnvironment.Dummy
) : NaverPayClient, AbstractDummySupport() {
    private val datetimeFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")

    override fun payment(request: NaverPayResources.Request.Pay): NaverPayResources.Reply.Paid {
        val result = if (dummy.forcePayFail)
            getBodyByFailedPay(request.paymentId)
        else
            getBodyByPaid(request)

        val reply = NaverPayResources.Reply(result).apply {
            makeExceptionIfNotSuccess()
        }

        return reply.getBody()!!
    }

    override fun cancel(request: NaverPayResources.Request.Cancel): NaverPayResources.Reply.Canceled {
        val result = if (dummy.forceCancelFail)
            getBodyByFailedCancel(request)
        else
            getBodyByCanceled(request)

        val reply = NaverPayResources.Reply(result).apply {
            makeExceptionIfNotSuccess()
        }

        return reply.getBody()!!
    }

    override fun cancelOfPartial(
        request: NaverPayResources.Request.Cancel,
        cancellableAmount: Long
    ): NaverPayResources.Reply.Canceled {
        val result = if (dummy.forceCancelFail)
            getBodyByFailedCancel(request)
        else
            getBodyByPartialCanceled(request, cancellableAmount)

        val reply = NaverPayResources.Reply(result).apply {
            makeExceptionIfNotSuccess()
        }

        return reply.getBody()!!
    }

    private fun getBodyByPaid(request: NaverPayResources.Request.Pay): String {
        val primaryPayMeans = listOf("CARD", "BANK", "").random()
        val dateTime = LocalDateTime.now().format(datetimeFormat)
        val rewardDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))

        var bankCode = ""
        var accountNo = ""
        var cardNo = ""
        var cardCorp = ""

        when (primaryPayMeans) {
            "BANK" -> {
                bankCode = BankCode.values().map { it.naverPay }.random()
                accountNo = super.numberOfAccount()
            }
            "CARD" -> {
                cardNo = super.numberOfCard()
                cardCorp = CreditCode.values().map { it.naverPay }.random()
            }
        }

        return """
        {
            "code": "Success",
            "message": "",
            "body": {
                "paymentId": "${request.paymentId}",
                "detail": {
                    "paymentId": "${request.paymentId}",
                    "payHistId": "${request.paymentId}${Random.nextInt(100, 999)}",
                    "merchantName": "테스트",
                    "merchantId": "${request.storeId}",
                    "merchantPayKey": "${request.token.orderId}",
                    "merchantUserKey": "${request.token.userId}",
                    "admissionTypeCode": "01",
                    "admissionYmdt": "$dateTime",
                    "tradeConfirmYmdt": "$dateTime",
                    "admissionState": "SUCCESS",
                    "totalPayAmount": ${request.token.paymentAmount},
                    "primaryPayAmount": ${request.token.paymentAmount},
                    "npointPayAmount": 0,
                    "primaryPayMeans": "$primaryPayMeans",
                    "cardCorpCode": "$cardCorp",
                    "cardNo": "$cardNo",
                    "cardAuthNo": "",
                    "cardInstCount": 0,
                    "bankCorpCode": "$bankCode",
                    "bankAccountNo": "$accountNo",
                    "productName": "아이템",
                    "settleExpected": false,
                    "settleExpectAmount": 0,
                    "payCommissionAmount": 0,
                    "extraDeduction": false,
                    "useCfmYmdt": "$rewardDate"
                }
            }
        }
    """.trimIndent()
    }

    private fun getBodyByFailedPay(paymentId: String): String {
        return """
            {
                "code": "Fail",
                "message": "4002/CCLG0031한도초과",
                "body": {
                    "paymentId": "$paymentId",
                    "detail": {
                        "paymentId": null,
                        "payHistId": null,
                        "merchantName": null,
                        "merchantId": null,
                        "merchantPayKey": "",
                        "merchantUserKey": "",
                        "admissionTypeCode": "",
                        "admissionYmdt": "",
                        "tradeConfirmYmdt": "",
                        "admissionState": "FAIL",
                        "totalPayAmount": 0,
                        "primaryPayAmount": 0,
                        "npointPayAmount": 0,
                        "primaryPayMeans": "",
                        "cardCorpCode": "",
                        "cardNo": "",
                        "cardAuthNo": "",
                        "cardInstCount": 0,
                        "bankCorpCode": "",
                        "bankAccountNo": "",
                        "productName": "",
                        "settleExpected": false,
                        "settleExpectAmount": 0,
                        "payCommissionAmount": 0,
                        "extraDeduction": false,
                        "useCfmYmdt": null
                    }
                }
            }
        """.trimIndent()
    }

    private fun getBodyByCanceled(request: NaverPayResources.Request.Cancel): String {
        val primaryPayMeans = listOf("CARD", "BANK", "").random()
        return """
            {
            "code": "Success",
            "message": "",
            "body": {
                "paymentId": "${request.paymentId}",
                "detail": {
                    "paymentId": "${request.paymentId}",
                    "payHistId": "${request.paymentId}${Random.nextInt(100, 999)}",
                    "primaryPayMeans": "$primaryPayMeans",
                    "primaryPayCancelAmount": "${request.cancelAmount}",
                    "primaryPayRestAmount": 0,
                    "npointCancelAmount": 0,
                    "npointRestAmount": 0,
                    "cancelYmdt": "${LocalDateTime.now().format(datetimeFormat)}",
                    "totalRestAmount": 0
                }
            }
        }
        """.trimIndent()
    }

    private fun getBodyByPartialCanceled(request: NaverPayResources.Request.Cancel, cancellableAmount: Long): String {
        val primaryPayMeans = listOf("CARD", "BANK", "").random()
        return """
            {
            "code": "Success",
            "message": "",
            "body": {
                "paymentId": "${request.paymentId}",
                "detail": {
                    "paymentId": "${request.paymentId}",
                    "payHistId": "${request.paymentId}${Random.nextInt(100, 999)}",
                    "primaryPayMeans": "$primaryPayMeans",
                    "primaryPayCancelAmount": ${cancellableAmount - request.cancelAmount},
                    "primaryPayRestAmount": ${cancellableAmount - (cancellableAmount - request.cancelAmount)},
                    "npointCancelAmount": 0,
                    "npointRestAmount": 0,
                    "cancelYmdt": "${LocalDateTime.now().format(datetimeFormat)}",
                    "totalRestAmount": ${cancellableAmount - request.cancelAmount}
                }
            }
        }
        """.trimIndent()
    }

    private fun getBodyByFailedCancel(request: NaverPayResources.Request.Cancel): String {
        return """
            {
                "code": "Fail",
                "message": "4002/Fail",
                "body": {
                    "paymentId": "${request.paymentId}",
                    "detail": {
                        "paymentId": "${request.paymentId}", 
                        "primaryPayMeans": null,
                        "primaryPayCancelAmount": null,
                        "primaryPayRestAmount": null,
                        "npointCancelAmount": null,
                        "npointRestAmount": null,
                        "cancelYmdt": null,
                        "totalRestAmount": null
                    }
                }
            }
        """.trimIndent()
    }
}
