package com.document.feed.config;

import java.net.InetSocketAddress;
import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.client.reactive.ReactiveRestClients;
import org.springframework.data.elasticsearch.config.AbstractReactiveElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableReactiveElasticsearchRepositories;

@Configuration
@EnableReactiveElasticsearchRepositories
public class ElasticSearchConfig extends AbstractReactiveElasticsearchConfiguration {

  @Value("${server.elasticsearch.url}")
  private String esClusterUrl;

  @Bean
  @Override
  public ReactiveElasticsearchClient reactiveElasticsearchClient() {
    var connUri = URI.create(esClusterUrl);
    String[] auth = connUri.getUserInfo().split(":");
    var clientConfiguration =
        ClientConfiguration.builder()
            .connectedTo(new InetSocketAddress(connUri.getHost(), connUri.getPort()))
            .usingSsl()
            .withBasicAuth(auth[0], auth[1])
            .build();
    return ReactiveRestClients.create(clientConfiguration);
  }

  @Bean
  public ReactiveElasticsearchTemplate reactiveElasticsearchTemplate() {
    return new ReactiveElasticsearchTemplate(reactiveElasticsearchClient());
  }
}
