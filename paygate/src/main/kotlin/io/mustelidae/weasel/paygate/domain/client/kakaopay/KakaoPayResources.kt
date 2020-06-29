package io.mustelidae.weasel.paygate.domain.client.kakaopay

import com.fasterxml.jackson.annotation.JsonProperty
import io.mustelidae.weasel.security.domain.token.PayToken
import java.time.LocalDateTime

class KakaoPayResources {

    class Request {

        data class Prepare(
            val token: PayToken,
            val storeId: String,
            val itemName: String,
            val itemId: String,
            val itemCount: Int,
            val approvalUrl: String,
            val cancelUrl: String?,
            val failUrl: String?
        )

        data class Pay(
            val token: PayToken,
            val tid: String,
            val storeId: String,
            val storeKey: String,
            val kakaoPayToken: String
        )

        data class Cancel(
            val storeId: String,
            val tid: String,
            val cancelAmount: Long
        )
    }

    class Reply {

        data class Prepared(
            /* 결제 고유 번호. 20자 */
            val tid: String,
            /* 요청한 클라이언트가 모바일 앱일 경우 해당 url을 통해 카카오톡 결제페이지를 띄움 */
            @JsonProperty("next_redirect_app_url")
            val nextRedirectAppUrl: String,
            /* 요청한 클라이언트가 모바일 웹일 경우 해당 url을 통해 카카오톡 결제페이지를 띄움*/
            @JsonProperty("next_redirect_mobile_url")
            val nextRedirectMobileUrl: String,
            /* 요청한 클라이언트가 pc 웹일 경우 redirect. 카카오톡으로 TMS를 보내기 위한 사용자입력화면이으로 redirect */
            @JsonProperty("next_redirect_pc_url")
            val nextRedirectPcUrl: String,
            /* 카카오페이 결제화면으로 이동하는 안드로이드 앱스킴 */
            @JsonProperty("android_app_scheme")
            val androidAppScheme: String,
            /* 카카오페이 결제화면으로 이동하는 iOS 앱스킴 */
            @JsonProperty("ios_app_scheme")
            val iosAppScheme: String,
            /* 	결제 준비 요청 시간 */
            @JsonProperty("created_at")
            val createdAt: LocalDateTime
        )

        data class Paid(
            /* 	Request 고유 번호 */
            val aid: String,
            /* 	결제 고유 번호*/
            val tid: String,
            /* 	가맹점 코드 */
            val cid: String,
            /* 	가맹점 주문번호*/
            @JsonProperty("partner_order_id")
            val partnerOrderId: String,
            /* 가맹점 회원 id*/
            @JsonProperty("partner_user_id")
            val partnerUserId: String,
            /* 결제 수단. CARD, MONEY 중 하나*/
            @JsonProperty("payment_method_type")
            val paymentMethodType: String,
            /* 결제 금액 정보 */
            val amount: Amount,
            /* 결제 상세 정보(결제수단이 카드일 경우만 포함) */
            @JsonProperty("card_info")
            val cardInfo: CardInfo?,
            /* 상품 이름. 최대 100자 */
            @JsonProperty("item_name")
            val itemName: String,
            /* 	상품 코드. 최대 100자 */
            @JsonProperty("item_code")
            val itemCode: String?,
            /* 상품 수량 */
            val quantity: Long,
            /* 결제 준비 요청 시각 */
            @JsonProperty("created_at")
            val createdAt: LocalDateTime,
            /* 	결제 승인 시각 */
            @JsonProperty("approved_at")
            val approvedAt: LocalDateTime,
            /* 	Request로 전달한 값 */
            val payload: String?
        ) {

            data class CardInfo(
                /* 매입카드사 한글명 */
                @JsonProperty("purchase_corp")
                val purchaseCorp: String,
                /* 매입카드사 코드 */
                @JsonProperty("purchase_corp_code")
                val purchaseCorpCode: String,
                /* 카드발급사 한글명 */
                @JsonProperty("issuer_corp")
                val issuerCorp: String,
                /* 	카드발급사 코드 */
                @JsonProperty("issuer_corp_code")
                val issuerCorpCode: String,
                /* 	카드 BIN */
                val bin: String,
                /* 카드타입 */
                @JsonProperty("card_type")
                val cardType: String,
                /* 할부개월수 */
                @JsonProperty("install_month")
                val installMonth: String,
                /* 	카드사 승인번호 */
                @JsonProperty("approved_id")
                val approvedId: String,
                /* 	카드사 가맹점번호 */
                @JsonProperty("card_mid")
                val cardMid: String,
                /* 	무이자할부 여부(Y/N) */
                @JsonProperty("interest_free_install")
                val interestFreeInstall: String,
                /* 카드 상품 코드 */
                @JsonProperty("card_item_code")
                val cardItemCode: String
            )
        }

        data class Canceled(
            /* Request 고유 번호 */
            val aid: String,
            /* 결제 고유 번호. 10자. */
            val tid: String,
            /* 가맹점 코드. 20자 */
            val cid: String,
            /* 	결제상태값 */
            val status: Status,
            /* 가맹점 주문번호 */
            @JsonProperty("partner_order_id")
            val partnerOrderId: String,
            /* 가맹점 회원 id */
            @JsonProperty("partner_user_id")
            val partnerUserId: String,
            /* 결제 수단. CARD, MONEY 중 하나 */
            @JsonProperty("payment_method_type")
            val paymentMethodType: String,
            /* 결제 금액 정보 */
            val amount: Amount,
            /* 이번 요청으로 취소된 금액 정보 */
            @JsonProperty("canceled_amount")
            val canceledAmount: CanceledAmount,
            /* 해당 결제에 대해 취소 가능 금액 */
            @JsonProperty("cancel_available_amount")
            val cancelAvailableAmount: CancelAvailableAmount,
            /* 	상품 이름 */
            @JsonProperty("item_name")
            val itemName: String,
            /* 상품 코드 */
            @JsonProperty("item_code")
            val itemCode: String?,
            /* 상품 수량 */
            val quantity: Long,
            /* 결제 준비 요청 시각 */
            @JsonProperty("created_at")
            val createdAt: LocalDateTime,
            /* 결제 승인 시각 */
            @JsonProperty("approved_at")
            val approvedAt: LocalDateTime,
            /* 결제 취소 시각 */
            @JsonProperty("canceled_at")
            val canceledAt: LocalDateTime,
            /* Request로 전달한 값 */
            val payload: String?
        ) {
            data class CanceledAmount(
                /* 전체 취소 금액 */
                val total: Long,
                /* 취소된 비과세 금액 */
                @JsonProperty("tax_free")
                val taxFree: Long,
                /* 취소된 부가세 금액 */
                val vat: Long,
                /*	할인 금액*/
                val discount: Long
            )

            data class CancelAvailableAmount(
                /* 전체 취소 가능 금액*/
                val total: Long,
                /* 	취소 가능한 비과세 금액*/
                @JsonProperty("tax_free")
                val taxFree: Long,
                /* 	취소 가능한 부가세 금액 */
                val vat: Long,
                /* 할인 금액*/
                val discount: Long
            )
        }
    }

    data class Amount(
        /* 	전체 결제 금액 */
        val total: Long,
        /* 비과세 금액 */
        @JsonProperty("")
        val taxFree: Long,
        /* 부가세 금액 */
        val vat: Long,
        /* 	사용한 포인트 금액 */
        val point: Long,
        /* 	할인 금액 */
        val discount: Long
    )

    data class Error(
        val code: String,
        val msg: String,
        val extras: MethodResult?
    ) {
        fun getErrorMessage(): String {
            if (extras == null)
                return msg

            return if (extras.methodResultMessage.isNullOrEmpty()) msg else extras.methodResultMessage
        }

        data class MethodResult(
            @JsonProperty("method_result_code")
            val methodResultCode: String?,
            @JsonProperty("method_result_message")
            val methodResultMessage: String?
        )
    }

    enum class Status(val message: String) {
        READY("결제요청"),
        SEND_TMS("결제요청 TMS 발송완료"),
        OPEN_PAYMENT("사용자가 카카오페이 결제화면을 열었음"),
        SELECT_METHOD("결제수단 선택, 인증 완료"),
        ARS_WAITING("ARS인증 진행중"),
        AUTH_PASSWORD("비밀번호 인증 완료"),
        ISSUED_SID("SID 발급완료(정기결제에서 SID만 발급 한 경우)"),
        SUCCESS_PAYMENT("결제완료"),
        PART_CANCEL_PAYMENT("부분취소된 상태"),
        CANCEL_PAYMENT("결제된 금액이 모두 취소된 상태. 부분취소 여러 번해서 모두 취소된 경우도 포함"),
        FAIL_AUTH_PASSWORD("사용자 비밀번호 인증 실패"),
        QUIT_PAYMENT("사용자가 결제를 중단한 경우"),
        FAIL_PAYMENT("결제 승인 실패")
    }
}
