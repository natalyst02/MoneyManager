package vn.com.mbbank.adminportal.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;

@Configuration
public class KafkaConfiguration {
  @Bean
  @ConfigurationProperties(prefix = "spring.kafka")
  public KafkaProperties kafkaProperties() {
    return new KafkaProperties();
  }

  @Bean
  @Primary
  @ConfigurationProperties(prefix = "spring.kafka9093")
  public KafkaProperties kafka9093Properties() {
    return new KafkaProperties();
  }

  @Bean
  ProducerFactory<String, byte[]> kafkaContainerFactory(@Qualifier("kafkaProperties") @Autowired KafkaProperties kafkaProperties) {
    var config = new HashMap<>(kafkaProperties.buildProducerProperties());
    return new DefaultKafkaProducerFactory<>(config);
  }

  @Bean
  ProducerFactory<String, byte[]> kafka9093ContainerFactory(@Qualifier("kafka9093Properties") @Autowired KafkaProperties kafkaProperties) {
    var config = new HashMap<>(kafkaProperties.buildProducerProperties());
    return new DefaultKafkaProducerFactory<>(config);
  }

  @Bean
  public KafkaTemplate<String, byte[]> kafkaTemplate(@Qualifier("kafkaContainerFactory") ProducerFactory<String, byte[]> kafkaProducerFactory) {
    return new KafkaTemplate<>(kafkaProducerFactory);
  }

  @Bean
  public KafkaTemplate<String, byte[]> kafka9093Template(@Qualifier("kafka9093ContainerFactory") ProducerFactory<String, byte[]> kafka9093ProducerFactory) {
    return new KafkaTemplate<>(kafka9093ProducerFactory);
  }
  @Bean
  public DefaultErrorHandler errorHandler() {
    return new DefaultErrorHandler((discoverRecord, exception) -> {}, new FixedBackOff(5000L, FixedBackOff.UNLIMITED_ATTEMPTS));
  }
}
