package io.mustelidae.weasel.paygate.domain.client.inicis

import io.mustelidae.weasel.paygate.common.PayMethod
import io.mustelidae.weasel.paygate.config.NotSupportPayMethodException
import io.mustelidae.weasel.paygate.domain.client.inicis.InicisPayType.CARD
import io.mustelidae.weasel.security.domain.token.PayToken
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal class InicisResources {

    class Request {
        data class Pay(
            val token: PayToken,
            val tid: String,
            val requestUrl: String,
            val storeId: String,
            val payMethod: PayMethod
        )

        data class Cancel(
            val storeId: String,
            val tid: String,
            val cancelAmount: Long,
            val memo: String?
        )
    }

    class Reply {
        data class Paid(
            val userId: String,
            val orderId: String,
            val tid: String,
            val payMethod: PayMethod,
            val payedDate: LocalDateTime,
            val storeId: String,
            val storeName: String,
            val paymentAmount: Long
        ) {
            var credit: Credit? = null

            data class Credit(
                val number: String,
                val canPartialCancel: Boolean,
                val isInterestFree: Boolean,
                val isCheckCard: Boolean,
                val applyPrice: Long,
                val name: String? = null,
                val issuer: String? = null,
                val code: String? = null,
                val purchaseCode: String? = null,
                val interestMonth: String? = null,
                val cardPoint: Long? = null
            )

            companion object {
                /**
                 * inicis response map description
                P_STATUS: 인증 상태 00 외 모두 실패
                P_TID: 거래번호 (성공시에만 반환)
                P_TYPE: CARD(ISP,안심클릭,케이페이) HPMN(해피머니) CULTURE(문화상품권) MOBILE(휴대폰) VBANK(가상계좌) EWALLET(전자지갑) ETC_(기타)
                P_AUTH_DT: 승인일자(YYYYmmddHHmmss)
                P_MID: 상점 아이디
                P_OID: 상점 주문번호
                P_AMT: 금액
                P_MNAME: 주문자 명
                P_UNAME: 가맹점 이름
                P_RMESG1: 지불 결과 메시지

                -- CARD --
                P_CARD_NUM: 카드번호
                P_CARD_ISSUER_CODE: 발급사 코드
                P_CARD_PRTC_CODE: 부분취소 가능여부(부분취소가능 : 1 , 부분취소불가능 : 0)
                P_CARD_INTEREST: 무이자 할부여부(0: 일반,1: 무이자)
                P_CARD_CHECKFLAG: 체크카드 여부(0 : 신용카드, 1 : 체크카드 2 : 기프트카드)
                P_RMESG2: 신용카드 할부 개월 수
                P_FN_CD1: 카드코드
                P_AUTH_NO: 승인번호
                P_FN_NM: 결제카드한글명
                P_EVENT_CODE: 이벤트코드
                P_ISP_CARDCODE: VP 카드코드
                P_CARD_MEMBER_NUM: 가맹점번호
                P_CARD_PURCHASE_CODE: 매입사 코드
                P_CARD_APPLPRICE: 신용카드 실 결제 금액
                P_CARD_USEPOINT: 신용카드 포인트 사용 금액
                 */
                fun from(responseMap: Map<String, String>): Paid {
                    val inicisPayType = InicisPayType.valueOf(responseMap.getValue("P_TYPE"))
                    val payedDate = LocalDateTime.parse(responseMap.getValue("P_AUTH_DT"), DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))

                    val payMethod = when (inicisPayType) {
                        CARD -> PayMethod.CREDIT
                        else -> {
                            // TODO: 다른 결제 수단에 대한 결과를 처리해야 함.
                            throw NotSupportPayMethodException("현재 신용카드 이외 결제는 지원하지 않습니다.")
                        }
                    }

                    val paid = Paid(
                        responseMap.getValue("P_UNAME"),
                        responseMap.getValue("P_OID"),
                        responseMap.getValue("P_TID"),
                        payMethod,
                        payedDate,
                        responseMap.getValue("P_MID"),
                        responseMap.getValue("P_MNAME"),
                        responseMap.getValue("P_AMT").toLong()
                    )

                    when (inicisPayType) {
                        CARD -> {
                            val canPartialCancel = (responseMap.getValue("P_CARD_PRTC_CODE") == "1")
                            val isInterestFree = (responseMap.getValue("P_CARD_INTEREST") == "1")
                            val isCheckCard = (responseMap.getValue("P_CARD_CHECKFLAG") == "1")

                            paid.credit = Credit(
                                responseMap.getValue("P_CARD_NUM"),
                                canPartialCancel,
                                isInterestFree,
                                isCheckCard,
                                responseMap.getValue("P_CARD_APPLPRICE").toLong(),
                                responseMap["P_FN_NM"],
                                responseMap["P_CARD_ISSUER_CODE"],
                                responseMap["P_CARD_PRTC_CODE"],
                                responseMap["P_CARD_PURCHASE_CODE"],
                                responseMap["P_RMESG2"],
                                responseMap["P_CARD_USEPOINT"]?.toLong()
                            )
                        }
                        else -> {
                            // TODO: 다른 결제 수단에 대한 결과를 처리해야 함.
                            throw NotSupportPayMethodException("현재 신용카드 이외 결제는 지원하지 않습니다.")
                        }
                    }

                    return paid
                }
            }
        }

        data class Canceled(
            val updatedTid: String,
            val canceledDate: LocalDateTime,
            val canceledAmount: Long,
            val remainAmount: Long
        )
    }
}
