package io.mustelidae.weasel.security.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class SecurityEnvironment {

    val userLock = UserLock()

    class UserLock {
        @Value(value = "\${app.security.userLock.ttl}")
        var ttl: Long = 300
    }
}
