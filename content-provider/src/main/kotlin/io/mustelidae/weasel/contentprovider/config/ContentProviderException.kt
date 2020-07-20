package io.mustelidae.weasel.contentprovider.config

import java.lang.RuntimeException

class ContentProviderException(message: String, val causeBy: Map<String, Any?>? = null) : RuntimeException(message)
