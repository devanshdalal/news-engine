package com.document.feed.controller;

import static org.springframework.http.MediaType.ALL;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.OPTIONS;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import com.document.feed.service.FeedService;
import com.document.feed.service.NewsService;
import com.document.feed.util.IdUtils;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Configuration
@RequiredArgsConstructor
@Slf4j
public class FeedHandler {

  private final FeedService feedService;

  private final NewsService newsService;

  private Mono<ServerResponse> defaultReadResponse(
      Flux<com.kwabenaberko.newsapilib.models.Article> articlePublisher) {
    return ServerResponse.ok()
        .contentType(APPLICATION_JSON)
        .header("Access-Control-Allow-Origin", "*")
        .body(articlePublisher, com.kwabenaberko.newsapilib.models.Article.class);
  }

  private ExchangeFilterFunction perResponse() {
    return ExchangeFilterFunction.ofResponseProcessor(
        clientResponse -> {
          clientResponse.headers().asHttpHeaders().forEach((h, g) -> log.info(h + " g: " + g));
          return Mono.just(clientResponse);
        });
  }

  @Bean
  public RouterFunction<ServerResponse> routeVanillaList() {
    return RouterFunctions.route(OPTIONS("/**").and(accept(ALL)), this::HandleOptionsCall)
        .andRoute(GET("/vanillalist").and(accept(APPLICATION_JSON)), this::vanillaList)
        .andRoute(GET("/list").and(accept(APPLICATION_JSON)), this::list)
        .andRoute(GET("/liked").and(accept(APPLICATION_JSON)), this::getPreference)
        .andRoute(POST("/like").and(accept(APPLICATION_JSON)), this::savePreference)
        .andRoute(POST("/dislike").and(accept(APPLICATION_JSON)), this::deletePreference)
        .andRoute(GET("/newsapi/**").and(accept(APPLICATION_JSON)), this::newsAPI);
  }

  Mono<ServerResponse> newsAPI(ServerRequest r) {
    var cacheControl = CacheControl.maxAge(30, TimeUnit.MINUTES);
    System.out.println("r.uri(): " + r.uri() + "-" + r.path());
    // /newsapi/v2/top-headlines?page=1&pageSize=30&language=en =>
    // v2/top-headlines?page=1&pageSize=30&language=en
    String path = r.path().substring(9);
    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .header("Access-Control-Allow-Origin", "*")
        .cacheControl(cacheControl)
        .body(
            newsService.mimicNewsAPIOrg(path, r.queryParams().toSingleValueMap()),
            ArticleResponse.class);
  }

  Mono<ServerResponse> HandleOptionsCall(ServerRequest r) {
    return ServerResponse.ok().bodyValue("OK");
  }

  Mono<ServerResponse> vanillaList(ServerRequest r) {
    PageRequest pageRequest = createPageRequest(r);
    return defaultReadResponse(this.feedService.vanillaList(pageRequest));
  }

  Mono<ServerResponse> list(ServerRequest r) {
    PageRequest pageRequest = createPageRequest(r);
    return defaultReadResponse(this.feedService.list(pageRequest));
  }

  public Mono<ServerResponse> deletePreference(ServerRequest request) {
    System.out.println("Start deletePreference()");
    // Authentication authentication =
    // SecurityContextHolder.getContext().getAuthentication();
    // String username = (String) authentication.getPrincipal();
    Mono<String> item = request.bodyToMono(String.class);
    return item.flatMap(
        url -> {
          var id = IdUtils.id(url);
          log.info("objectId: " + id);
          return this.feedService
              .deletePreference(id)
              .then(ServerResponse.ok().contentType(APPLICATION_JSON).bodyValue(id));
        });
  }

  Mono<ServerResponse> getPreference(ServerRequest r) {
    r.session()
        .subscribe(webSession -> System.out.println("webSession:" + webSession.getCreationTime()));
    return defaultReadResponse(this.feedService.getPreference());
  }

  public Mono<ServerResponse> savePreference(ServerRequest request) {
    System.out.println("Start savePreference()");
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = (String) authentication.getPrincipal();
    var item = request.bodyToMono(com.kwabenaberko.newsapilib.models.Article.class);
    item.cache();
    return item.flatMap(
            article -> {
              return this.feedService
                  .savePreference(article, username)
                  .doOnSuccess(
                      preference -> {
                        System.out.println("serverResponse: " + preference);
                      });
            })
        .flatMap(x -> ServerResponse.ok().contentType(APPLICATION_JSON).bodyValue(x))
        .switchIfEmpty(ServerResponse.notFound().build());
  }

  private PageRequest createPageRequest(ServerRequest r) {
    MultiValueMap<String, String> params = r.queryParams();
    System.out.println("headers " + r.queryParams());

    int pageSize = 60; // default
    Pageable p;
    List<String> pageSizeOpt = params.get("limit");
    if (null != pageSizeOpt && !pageSizeOpt.isEmpty() && !pageSizeOpt.isEmpty()) {
      pageSize = Integer.parseInt(pageSizeOpt.get(0));
    }

    int pageIndex = 0; // default
    List<String> skipOpt = params.get("skip");
    if (null != skipOpt && !skipOpt.isEmpty() && !skipOpt.get(0).isEmpty()) {
      // TODO(devansh): client can't request page starting from offset 10 of pagesize
      // 500. Please handle that.
      pageIndex = Integer.parseInt(skipOpt.get(0)) / pageSize;
    }

    return PageRequest.of(pageIndex, pageSize);
  }
}
