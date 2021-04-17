package com.document.feed.client;

import com.kwabenaberko.newsapilib.NewsApiClient;
import com.kwabenaberko.newsapilib.NewsApiClient.ArticlesResponseCallback;
import com.kwabenaberko.newsapilib.models.request.EverythingRequest;
import com.kwabenaberko.newsapilib.models.request.TopHeadlinesRequest;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewsClient {
  private static final String TOO_MANY_REQUESTS = "too many requests";
  private final Cache<String, ArticleResponse> cache;

  private NewsApiClient newsApiClient;

  @Value("${newsapi.key}")
  private String[] apiKey;

  @Value("${news_client.cache}")
  private boolean cacheEnabled;

  private int ind = -1;

  public void roll() {
    ind = (1 + ind) % apiKey.length;
    newsApiClient = new NewsApiClient(apiKey[ind]);
  }

  public void getTopHeadlines(TopHeadlinesRequest request, ArticlesResponseCallback callback) {
    var key =
        key(request.getCategory(), request.getCountry(), request.getPage(), request.getPageSize());
    log.info("headlines key: {}", key);
    if (cacheEnabled && cache.containsKey(key)) {
      callback.onSuccess(cache.get(key));
      return;
    }
    getNewsApiClient()
        .getTopHeadlines(
            request,
            new NewsApiClient.ArticlesResponseCallback() {
              @Override
              public void onSuccess(ArticleResponse response) {
                if (cacheEnabled) {
                  cache.put(key, response);
                }
                callback.onSuccess(response);
              }

              @Override
              public void onFailure(Throwable throwable) {
                if (canRollKey(throwable)) {
                  roll();
                  getTopHeadlines(request, callback);
                  return;
                }
                callback.onFailure(throwable);
              }
            });
  }

  public void getEverything(EverythingRequest request, ArticlesResponseCallback callback) {
    var key = key(request.getQ(), request.getPage(), request.getPageSize(), request.getSortBy());
    log.info("everything key: {}", key);
    if (cacheEnabled && cache.containsKey(key)) {
      callback.onSuccess(cache.get(key));
      return;
    }
    getNewsApiClient()
        .getEverything(
            request,
            new NewsApiClient.ArticlesResponseCallback() {
              @Override
              public void onSuccess(ArticleResponse response) {
                if (cacheEnabled) {
                  cache.put(key, response);
                }
                callback.onSuccess(response);
              }

              @Override
              public void onFailure(Throwable throwable) {
                if (canRollKey(throwable)) {
                  roll();
                  getEverything(request, callback);
                  return;
                }
                callback.onFailure(throwable);
              }
            });
  }

  private void maybeInit() {
    if (ind == -1) {
      ind = ThreadLocalRandom.current().nextInt(0, apiKey.length);
      newsApiClient = new NewsApiClient(apiKey[ind]);
    }
  }

  private NewsApiClient getNewsApiClient() {
    maybeInit();
    return newsApiClient;
  }

  private boolean canRollKey(Throwable throwable) {
    System.out.println("Throw: " + throwable.getMessage());
    if (throwable.getMessage().contains(TOO_MANY_REQUESTS)) {
      System.out.println("Http status code 429 received");
      return true;
    }
    return false;
  }

  private String key(String... token) {
    return Arrays.stream(token)
        .filter(Objects::nonNull)
        .reduce(
            "",
            (s, s1) -> {
              if (s.isEmpty()) {
                return s1;
              }
              return s + "." + s1;
            });
  }
}
