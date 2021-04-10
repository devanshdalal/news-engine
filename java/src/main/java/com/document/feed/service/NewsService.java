package com.document.feed.service;

import com.document.feed.client.NewsClient;
import com.document.feed.model.Article;
import com.document.feed.util.IdUtils;
import com.kwabenaberko.newsapilib.NewsApiClient;
import com.kwabenaberko.newsapilib.models.request.EverythingRequest;
import com.kwabenaberko.newsapilib.models.request.TopHeadlinesRequest;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsService {
  private static final String DEFAULT_LANGUAGE = "en";
  private static final int DEFAULT_PAGESIZE = 100;
  private static final List<String> CATEGORIES =
      List.of(
          "business" /*, "entertainment", "general", "health", "science", "sports", "technology"*/);
  private static final List<String> COUNTRIES = List.of("in" /*, "us", "gb", "au", "ru", "fr"*/);
  private static int PAGE_SIZE = 100;
  // "You have made too many requests recently. Developer accounts are limited to 100 requests over
  // a 24 hour period (50 requests available every 12 hours). Please upgrade to a paid plan if you
  // need more requests."
  private static String TOO_MANY_REQUESTS = "too many requests";
  private final NewsClient newsClient;
  private ExecutorService es = Executors.newSingleThreadExecutor();

  public Mono<ArticleResponse> mimicNewsAPIOrg(String path, Map<String, String> queryParams) {
    switch (path) {
      case "v2/top-headlines":
        var headlinesRequest =
            new TopHeadlinesRequest.Builder().language(DEFAULT_LANGUAGE).pageSize(DEFAULT_PAGESIZE);
        if (queryParams.containsKey("category")) {
          headlinesRequest.category(queryParams.get("category"));
        }
        if (queryParams.containsKey("country")) {
          headlinesRequest.category(queryParams.get("country"));
        }
        if (queryParams.containsKey("pageSize")) {
          headlinesRequest.pageSize(Integer.parseInt(queryParams.get("pageSize")));
        } else {
          headlinesRequest.pageSize(DEFAULT_PAGESIZE);
        }
        return Mono.create(
            sink ->
                newsClient.getTopHeadlines(
                    headlinesRequest.build(),
                    new NewsApiClient.ArticlesResponseCallback() {
                      @Override
                      public void onSuccess(ArticleResponse response) {
                        sink.success(response);
                      }

                      @Override
                      public void onFailure(Throwable throwable) {
                        System.out.println(throwable.getMessage());
                      }
                    }));
      case "v2/everything":
        var everythingRequest =
            new EverythingRequest.Builder().language(DEFAULT_LANGUAGE).pageSize(DEFAULT_PAGESIZE);
        everythingRequest.sortBy(queryParams.getOrDefault("sortBy", "popularity"));
        if (queryParams.containsKey("q")) {
          everythingRequest.q(queryParams.get("q"));
        } else {
          everythingRequest.domains("techcrunch.com");
        }
        if (queryParams.containsKey("pageSize")) {
          everythingRequest.pageSize(Integer.parseInt(queryParams.get("pageSize")));
        } else {
          everythingRequest.pageSize(DEFAULT_PAGESIZE);
        }
        return Mono.create(
            sink ->
                newsClient.getEverything(
                    everythingRequest.build(),
                    new NewsApiClient.ArticlesResponseCallback() {
                      @Override
                      public void onSuccess(ArticleResponse response) {
                        sink.success(response);
                      }

                      @Override
                      public void onFailure(Throwable throwable) {
                        log.error("failure: " + throwable);
                      }
                    }));
      default:
        throw new IllegalStateException(path + " not valid");
    }
  }

  public Flux<Article> downloadHeadlines() {
    return Flux.create(
        sink -> {
          CATEGORIES.forEach(
              category ->
                  COUNTRIES.forEach(
                      country -> {
                        var request =
                            new TopHeadlinesRequest.Builder()
                                .language(DEFAULT_LANGUAGE)
                                .pageSize(DEFAULT_PAGESIZE)
                                .country(country)
                                .category(category)
                                .build();
                        es.execute(
                            () -> {
                              newsClient.getTopHeadlines(
                                  request,
                                  new NewsApiClient.ArticlesResponseCallback() {
                                    @Override
                                    public void onSuccess(ArticleResponse response) {
                                      response.getArticles().stream()
                                          .map(
                                              a -> {
                                                var esDoc = new Article();
                                                esDoc.setArticle(a);
                                                esDoc.setId(IdUtils.id(a.getUrl()));
                                                esDoc.setCategory(category);
                                                esDoc.setCountry(country);
                                                return esDoc;
                                              })
                                          .forEach(sink::next);
                                    }

                                    @Override
                                    public void onFailure(Throwable throwable) {
                                      log.error("failure: " + throwable);
                                    }
                                  });
                            });
                      }));
        });
  }
}
