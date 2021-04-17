package com.document.feed.config;

import java.net.InetSocketAddress;
import java.net.URI;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
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

  @Bean(destroyMethod = "close")
  public RestHighLevelClient client() {
    var connUri = URI.create(esClusterUrl);
    String[] auth = connUri.getUserInfo().split(":");

    var credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(
        AuthScope.ANY, new UsernamePasswordCredentials(auth[0], auth[1]));

    System.out.println("conn: " + connUri.toASCIIString());
    var builder =
        RestClient.builder(new HttpHost(connUri.getHost(), connUri.getPort(), "https"))
            .setHttpClientConfigCallback(
                httpClientBuilder ->
                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
    return new RestHighLevelClient(builder);
  }

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
