package com.document.feed;

import com.document.feed.model.ArticleReactiveRepository;
import com.document.feed.service.NewsService;
import com.document.feed.util.ESMaintenance;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.repository.config.EnableReactiveElasticsearchRepositories;
import reactor.core.publisher.Mono;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
@EnableReactiveElasticsearchRepositories
public class FeedApplication implements ApplicationRunner {
  private final NewsService newsService;
  private final ArticleReactiveRepository articleReactiveRepository;
  private final ReactiveElasticsearchClient reactiveElasticsearchClient;
  private final ESMaintenance esMaintenance;
  private final ExecutorService executorService;

  public static void main(String[] args) {
    System.out.println("Starting ... at " + System.getenv("PORT"));
    SpringApplication.run(FeedApplication.class, args);
  }

  @Override
  public void run(ApplicationArguments args) throws IOException {
    executorService.execute(
        () -> {
          esMaintenance.deleteOldIndexes();
        });
    var headlines = newsService.downloadHeadlines();
    var allIds =
        articleReactiveRepository
            .findAll()
            .flatMap(article -> Mono.just(article.getId()))
            .collect(Collectors.toSet())
            .cache();
    headlines
        .filterWhen(article -> allIds.map(set -> !set.contains(article.getId())))
        .bufferTimeout(200, Duration.ofSeconds(1))
        .delaySequence(Duration.ofSeconds(3))
        .flatMap(articleList -> articleReactiveRepository.saveMany(articleList))
        .subscribe(x -> log.info(x.getArticle().getTitle()));
  }
}
