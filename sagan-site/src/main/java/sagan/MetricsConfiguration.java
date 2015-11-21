/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sagan;

import org.springframework.boot.actuate.metrics.repository.MetricRepository;
import org.springframework.boot.actuate.metrics.repository.redis.RedisMetricRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * Configure metrics gathering per Spring Boot Actuator
 */
@Configuration
@ConditionalOnClass({RedisConnectionFactory.class, MetricRepository.class})
//@Profile(SaganProfiles.CLOUDFOUNDRY)
@Profile(SaganProfiles.REDISMETRICS) // TODO: After verifiyication, upgrade to SaganProfiles.CLOUDFOUNDRY
class MetricsConfiguration {

    @Bean
    public RedisMetricRepository redisMetricRepository(RedisConnectionFactory connectionFactory) {
        return new RedisMetricRepository(connectionFactory);
    }

}
