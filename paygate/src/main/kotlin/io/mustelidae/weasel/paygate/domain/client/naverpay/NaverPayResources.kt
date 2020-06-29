package io.mustelidae.weasel.paygate.domain.client.naverpay

import com.fasterxml.jackson.module.kotlin.convertValue
import io.mustelidae.weasel.paygate.config.PayGateClientException
import io.mustelidae.weasel.paygate.utils.Jackson
import io.mustelidae.weasel.security.domain.token.PayToken

internal class NaverPayResources {

    class Request {
        data class Pay(
            val token: PayToken,
            val storeId: String,
            val storeKey: String,
            val paymentId: String
        )

        data class Cancel(
            val storeId: String,
            val storeKey: String,
            val paymentId: String,
            val isUserRequired: Boolean,
            val cancelAmount: Long,
            val memo: String?
        )
    }

    class Reply(
        content: String
    ) {
        val jackson = Jackson.getMapper()

        val tree = jackson.readTree(content)!!
        private val code: String
        private val message: String
        val isSuccess: Boolean

        inline fun <reified T : Any> getBody(): T? {
            return if (isSuccess)
                jackson.convertValue(tree.get("body"))
            else
                null
        }

        fun makeExceptionIfNotSuccess() {
            if (this.isSuccess.not()) {
                val mapOfCause = mapOf(
                    "pg" to "naver pay",
                    "errorCode" to code,
                    "errorMsg" to message
                )
                throw PayGateClientException(message, true, mapOfCause)
            }
        }

        init {
            this.code = tree.get("code").textValue()
            this.isSuccess = (this.code == "Success")

            this.message = when (code) {
                "Success", "Fail" -> tree.get("message").textValue()
                "InvalidMerchant" -> "유효하지 않은 가맹점입니다."
                "TimeExpired" -> "결제 승인 가능 시간이 초과되었습니다. (네이버페이는 최대 10분 안에 결제 해야 합니다.)"
                "AlreadyOnGoing" -> "결제가 이미 진행 중입니다. 잠시 후 다시 시도해 주세요."
                "AlreadyComplete" -> "이미 해당 결제 번호로 결제가 완료되었습니다."
                "OwnerAuthFail" -> "네이버페이 인증에 실패하였습니다. 다시 시도해주세요."
                "InvalidPaymentId" -> "유효하지 않은 네이버페이 결제번호입니다."
                "AlreadyCanceled" -> "이미 전체 금액이 취소된 결제건 입니다."
                "OverRemainAmount" -> "취소 요청 금액이 잔여 결제 금액을 초과하였습니다."
                "PreCancelNotComplete" -> "취소가 진행 중입니다. 잠시 후 다시 시도해 주세요."
                "TaxScopeAmtGreaterThanRemainError" -> "취소 가능한 과면세 금액보다 큰 금액을 요청하였습니다."
                "BankCheckTime" -> "현재는 은행 점검 시간 입니다. (23시 30분~0시 30분)"
                "RestAmountDiff" -> "예상 잔여금액(expectedRestAmount)과 네이버페이의 잔여금액이 일치하지 않습니다. 고객센터에 문의해 주세요."
                "CancelNotComplete" -> "취소 처리가 정상적으로 완료되지 않았습니다. 반드시 취소를 다시 요청해주세요"
                "InvalidMerchantType" -> "네이버 페이 포인트의 리워드를 할 수 없는 가맹점입니다."
                "TimeConditionError" -> "검색 기간 오류(31일 이내로 검색 해 주세요)"
                else -> {
                    "결제 연동에 실패하였습니다."
                }
            }
        }

        data class Paid(
            val paymentId: String,
            val detail: Detail
        ) {
            data class Detail(
                /* 네이버페이 결제번호 */
                val paymentId: String,
                /* 상품명 */
                val productName: String,
                /* 결제 이력번호 */
                val payHistId: String,
                /* 가맹점 아이디 */
                val merchantId: String,
                /* 가맹점 명*/
                val merchantName: String,
                /* 신용카드 번호 */
                val cardNo: String,
                /* 결제 일시(YYYYMMDDHH24MMSS) */
                val admissionYmdt: String,
                /* 주 결제 수단 결제/취소 금액 */
                val primaryPayAmount: Long,
                /* 네이버페이 포인트 결제/취소 금액 */
                val npointPayAmount: Long,
                /* 총 결제/취소 금액 */
                val totalPayAmount: Long,
                /* CARD:신용카드, BANK:계좌이체 */
                val primaryPayMeans: String,
                /* 가맹점의 결제번호 */
                val merchantPayKey: String,
                /* 가맹점의 사용자 키 */
                val merchantUserKey: String,
                /* 주 결제 수단 카드사  */
                val cardCorpCode: String,
                /* 01:원결제 승인건, 03:전체취소 건, 04:부분취소 건 */
                val admissionTypeCode: String,
                /* 정산 예정 금액 */
                val settleExpectAmount: Long,
                /* 결제 수수료 금액  */
                val payCommissionAmount: Long,
                /* 결제/취소 시도에 대한 최종결과 SUCCESS:완료, FAIL:실패 */
                val admissionState: String,
                /* 거래완료 일시(정산기준날짜, YYYYMMDDHH24MMSS) */
                val tradeConfirmYmdt: String,
                /* 카드승인번호 */
                val cardAuthNo: String,
                /* 할부 개월 수 (일시불은 0) */
                val cardInstCount: Int,
                /* 주 결제 수단 은행  */
                val bankCorpCode: String,
                /* 일부 마스킹 된 계좌 번호 */
                val bankAccountNo: String,
                /* true/false. 정산 예정 금액과 결제 수수료 금액이 계산되었는지 여부를 나타냅니다.
                    이 값이 false이면 아래 두 필드인 settleExpectAmount,payCommissionAmount 값은 무의미합니다 */
                val settleExpected: Boolean,
                /* 도서 / 공연 소득공제 여부 */
                val extraDeduction: Boolean,
                /* 이용완료일(yyyymmdd) */
                val useCfmYmdt: String
            )
        }

        data class Canceled(
            /* 결제번호 */
            val paymentId: String,
            /* 취소 결제 번호 */
            val payHistId: String,
            /* 취소 처리된 주 결제 수단(CARD: 신용카드, BANK: 계좌 이체) */
            val primaryPayMeans: String,
            /* 주 결제 수단 취소 금액 */
            val primaryPayCancelAmount: Long,
            /* 추가로 취소 가능한 주 결제 수단 잔여 결제 금액 */
            val primaryPayRestAmount: Long,
            /* 네이버페이 포인트 취소 금액 */
            val npointCancelAmount: Long,
            /* 추가로 취소 가능한 네이버페이 포인트 잔여 결제 금액 */
            val npointRestAmount: Long,
            /* 취소 일시(YYYYMMDDHH24MMSS) */
            val cancelYmdt: String,
            /* 추가로 취소 가능한 전체 잔여 결제 금액(primaryPayRestAmount + npointRestAmount) */
            val totalRestAmount: Long
        )
    }
}
