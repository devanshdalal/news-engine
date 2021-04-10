package com.document.feed;

import com.document.feed.model.ArticleReactiveRepository;
import com.document.feed.service.NewsService;
import java.time.Duration;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableReactiveElasticsearchRepositories;
import reactor.core.publisher.Mono;

@SpringBootApplication
@RequiredArgsConstructor
@EnableReactiveElasticsearchRepositories
public class FeedApplication implements ApplicationRunner {
  private final NewsService newsService;
  private final ArticleReactiveRepository articleReactiveRepository;

  public static void main(String[] args) {
    SpringApplication.run(FeedApplication.class, args);
  }

  @Override
  public void run(ApplicationArguments args) {
    var allIds =
        articleReactiveRepository
            .findAll()
            .flatMap(article -> Mono.just(article.getId()))
            .collect(Collectors.toSet())
            .cache();
    var a = newsService.downloadHeadlines();
    a.filterWhen(article -> allIds.map(set -> !set.contains(article.getId())))
        .bufferTimeout(100, Duration.ofSeconds(1))
        .delaySequence(Duration.ofSeconds(3))
        .flatMap(
            articleList -> {
              return articleReactiveRepository.saveMany(articleList);
            })
        .subscribe(x -> System.out.println(x.getArticle().getTitle()));
    //    a.subscribe(System.out::println);
  }
}
