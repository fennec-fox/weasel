package io.mustelidae.weasel.paygate.domain.client.inicis

import org.slf4j.Logger

/**
 * FIXME:  이니시스의 결제 통신 라이브러리는 비공개이기 때문에 스펙을 따라 만듦.
 * 이니시스 모듈을 본떠서 만든 라이브러리
 */
@Suppress("FunctionName", "SpellCheckingInspection")
class MockInicisModule {

    private var log: Logger? = null

    private val mutableMap = mutableMapOf<String, String>()

    fun SetField(pname: String, pvalue: String) {
        mutableMap[pname] = pvalue
    }

    fun GetResult(pname: String): String {
        return mutableMap[pname]!!
    }

    fun startAction() {
    }
}
