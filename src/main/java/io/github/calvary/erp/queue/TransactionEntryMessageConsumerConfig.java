package io.github.calvary.erp.queue;

import java.util.HashMap;
import java.util.Map;
/*-
 * Copyright © 2023 - 2024 Calvary ERP Contributors (mailnjeru@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

@Configuration
@ConfigurationProperties(prefix = "queue.kafka.consumer")
public class TransactionEntryMessageConsumerConfig {

    @Value("${queue.transaction-entry.topic}")
    private String topicName;

    @Value("${queue.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${queue.kafka.consumer.group.id}")
    private String groupId;

    // Set Kafka consumer properties
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransactionEntryMessage> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TransactionEntryMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        // Set other properties on the factory if needed
        return factory;
    }

    @Bean
    public ContainerProperties containerProperties() {
        ContainerProperties containerProperties = new ContainerProperties(topicName);
        containerProperties.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        // containerProperties.setListenerMode(ListenerMode.BATCH);
        // containerProperties.setAckOnError(false);
        containerProperties.setPollTimeout(3000);
        // containerProperties.setErrorHandler(new SeekToCurrentErrorHandler());
        return containerProperties;
    }

    // Define Kafka consumer configurations
    @SneakyThrows
    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        // properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        // Add additional properties as needed
        return properties;
    }

    // Create Kafka consumer factory
    @Bean
    public DefaultKafkaConsumerFactory<String, TransactionEntryMessage> consumerFactory() {
        DefaultKafkaConsumerFactory<String, TransactionEntryMessage> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerConfigs());

        consumerFactory.setKeyDeserializer(new StringDeserializer());
        consumerFactory.setValueDeserializer(new TransactionEntryMessageDeserializer());
        return consumerFactory;
    }
}
