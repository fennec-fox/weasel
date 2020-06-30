package io.mustelidae.weasel.security.domain.lock

import io.mustelidae.weasel.security.config.SecurityEnvironment
import io.mustelidae.weasel.security.config.UserLockException
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class UserLock
@Autowired constructor(
    private val lockRedisTemplate: RedisTemplate<String, String>,
    securityEnvironment: SecurityEnvironment
) {
    private val timeFormatter = DateTimeFormatter.ISO_DATE_TIME!!
    private val secondOfTTL = securityEnvironment.userLock.ttl
    private val operations = lockRedisTemplate.opsForValue()

    fun lock(userId: String) {
        if (this.isAvailable(userId).not())
            throw UserLockException("기 결제건이 수행중에 있습니다. 잠시 후 다시 시도해주세요")

        operations.set(userId, LocalDateTime.now().format(timeFormatter), Duration.ofSeconds(secondOfTTL))
    }

    fun unlock(userId: String) {
        lockRedisTemplate.delete(userId)
    }

    fun isAvailable(userId: String): Boolean {
        val time = operations.get(userId)

        if (time.isNullOrBlank())
            return true

        val existedTime = LocalDateTime.parse(time, timeFormatter)

        if (ChronoUnit.SECONDS.between(existedTime, LocalDateTime.now()) < secondOfTTL) {
            return false
        }

        return true
    }
}
