package io.mustelidae.weasel.security.domain.lock

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.mustelidae.weasel.security.config.LockRedisConfiguration
import io.mustelidae.weasel.security.config.SecurityEnvironment
import io.mustelidae.weasel.security.config.UserLockException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Suppress("UsePropertyAccessSyntax")
@Testcontainers
internal class UserLockTest {
    private val log = LoggerFactory.getLogger(this::class.java)
    private lateinit var userLockRedisTemplate:RedisTemplate<String,String>
    private val securityEnvironment = SecurityEnvironment().apply {
        userLock.ttl = 5
    }

    @Container
    private val redis: GenericContainer<*> = GenericContainer<Nothing>("redis:5.0.3-alpine").withExposedPorts(6379)

    @BeforeEach
    fun beforeEach(){
        redis.start()
        log.info("BeforeAll start redis ${redis.getHost()} ${redis.getFirstMappedPort()}")

        val redisProperties = RedisProperties().apply {
            host = redis.getHost()
            port = redis.getFirstMappedPort()
        }
        val configuration = LockRedisConfiguration(redisProperties)
        val connectionFactory = configuration.userLockRedisConnectionFactory() as LettuceConnectionFactory
        connectionFactory.afterPropertiesSet()

        this.userLockRedisTemplate = configuration.lockRedisTemplate(connectionFactory).apply {
            afterPropertiesSet()
        }
    }

    @AfterEach
    fun afterEach() {
        redis.stop()
    }

    @Test
    fun lock() {
        //Given
        val userId = "fennec-fox"
        val userLock = UserLock(userLockRedisTemplate, securityEnvironment)
        //When
        userLock.lock(userId)
        //Then
        val value = this.userLockRedisTemplate.opsForValue().get(userId)

        value shouldNotBe null
    }

    @Test
    fun unlock() {
        //Given
        val userId = "fennec-fox-delete"
        val userLock = UserLock(userLockRedisTemplate, securityEnvironment)
        //When
        userLock.lock(userId)
        userLock.unlock(userId)
        //Then
        val value = this.userLockRedisTemplate.opsForValue().get(userId)
        value shouldBe null
    }

    @Test
    fun isAvailable() {
        //Given
        val userId = "fennec-fox-avail"
        val userLock = UserLock(userLockRedisTemplate, securityEnvironment)
        //When
        userLock.lock(userId)

        //Then
        val isAvailable = userLock.isAvailable(userId)
        //Then
        isAvailable shouldBe false
    }

    @Test
    fun duplicatedError() {
        //Given
        val userId = "fennec-fox-duplicate"
        val userLock = UserLock(userLockRedisTemplate, securityEnvironment)
        //When
        userLock.lock(userId)
        // Then
        Assertions.assertThrows(UserLockException::class.java) {
            userLock.lock(userId)
        }
    }

    @Test
    fun ttlCheck() {
        //Given
        val userId = "fennec-fox-ttl"
        securityEnvironment.userLock.ttl = 1
        val userLock = UserLock(userLockRedisTemplate, securityEnvironment)
        //When
        userLock.lock(userId)
        Thread.sleep(2000)
        //Then
        val isAvailable = userLock.isAvailable(userId)
        isAvailable shouldBe true
    }
}
