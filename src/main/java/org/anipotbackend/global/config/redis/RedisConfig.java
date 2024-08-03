package org.anipotbackend.global.config.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Optional;

@Configuration
public class RedisConfig {

    private static final String REDISSON_HOST_PREFIX = "redis://";
    private static final String REDISSON_HOST_PREFIX_SSL = "rediss://";

    @Bean
    public RedissonClient redissonClient(RedisProperties properties) {
        Config config = new Config();
        int timeout = Optional.ofNullable(properties.getTimeout())
                .map(x -> Long.valueOf(x.toMillis()).intValue())
                .orElse(10000);
        String prefix = properties.getSsl().isEnabled() ? REDISSON_HOST_PREFIX_SSL : REDISSON_HOST_PREFIX;

        SingleServerConfig singleServerConfig = config.useSingleServer()
                .setAddress(prefix + properties.getHost() + ":" + properties.getPort())
                .setConnectTimeout(timeout)
                .setDatabase(properties.getDatabase());

        String password = properties.getPassword();
        if (password != null && !password.isBlank()) {
            singleServerConfig.setPassword(properties.getPassword());
        }

        return Redisson.create(config);
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(RedisProperties rp) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(rp.getHost(), rp.getPort());
        String password = rp.getPassword();
        if (!password.isBlank()) {
            configuration.setPassword(RedisPassword.of(password));
        }

        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(configuration);
        lettuceConnectionFactory.afterPropertiesSet();
        return lettuceConnectionFactory;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }
}
