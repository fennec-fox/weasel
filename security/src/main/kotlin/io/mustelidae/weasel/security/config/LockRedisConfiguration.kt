package io.mustelidae.weasel.security.config

import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@EnableConfigurationProperties(value = [RedisProperties::class])
class LockRedisConfiguration(
    private val properties: RedisProperties
) {

    @Bean
    fun userLockRedisConnectionFactory(): RedisConnectionFactory {
        val configuration = RedisStandaloneConfiguration(properties.host, properties.port).apply {
            password = RedisPassword.of(properties.password)
            database = 1
        }

        return LettuceConnectionFactory(configuration)
    }

    @Bean
    fun lockRedisTemplate(
        userLockRedisConnectionFactory: RedisConnectionFactory
    ): RedisTemplate<String, String> {

        return RedisTemplate<String, String>().apply {
            setConnectionFactory(userLockRedisConnectionFactory)
            this.keySerializer = StringRedisSerializer()
            this.valueSerializer = StringRedisSerializer()
            this.hashKeySerializer = StringRedisSerializer()
            this.hashValueSerializer = StringRedisSerializer()
        }
    }
}
