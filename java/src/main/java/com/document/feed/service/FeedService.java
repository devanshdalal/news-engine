package com.document.feed.service;

import com.document.feed.model.ArticleReactiveRepository;
import com.document.feed.model.Preference;
import com.document.feed.model.PreferenceReactiveRepository;
import com.document.feed.util.IdUtils;
import com.kwabenaberko.newsapilib.models.Article;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder.Type;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {

  private final ArticleReactiveRepository articleReactiveRepository;

  private final PreferenceReactiveRepository preferenceRepository;

  public Flux<com.kwabenaberko.newsapilib.models.Article> vanillaList(PageRequest pageRequest) {
    return articleReactiveRepository
        .findAllBQ(new NativeSearchQueryBuilder().withPageable(pageRequest).build())
        .flatMap(articleSearchHit -> Mono.just(articleSearchHit.getContent().getArticle()));
  }

  public Flux<com.kwabenaberko.newsapilib.models.Article> list(PageRequest pageRequest) {
    log.info("/list called");
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = (String) authentication.getPrincipal();
    log.info("username:" + username);
    log.info("sc:" + SecurityContextHolder.getContext().getAuthentication());
    var queryList = new String[] {"", "", ""};
    var a =
        preferenceRepository
            .findByUsername(username)
            .flatMap(preference -> Flux.just(preference.getArticle()))
            .reduce(
                queryList,
                (l, article) -> {
                  l[0] += " " + article.getTitle() + " " + article.getDescription();
                  l[1] += " " + article.getAuthor();
                  l[2] += " " + article.getSource().getName();
                  log.info(l[0] + ", " + l[1] + ", " + l[2]);
                  return l;
                })
            .map(
                qList -> {
                  return new NativeSearchQueryBuilder()
                      .withPageable(pageRequest)
                      .withQuery(
                          new BoolQueryBuilder()
                              .should(
                                  QueryBuilders.multiMatchQuery(qList[0])
                                      .field("article.description", 3)
                                      .lenient(true)
                                      .field("article.title", 3)
                                      .type(Type.MOST_FIELDS)
                                      .fuzziness(Fuzziness.TWO))
                              .should(
                                  QueryBuilders.matchQuery("article.author", qList[1])
                                      .boost(StringUtils.isBlank(qList[1]) ? 0 : 1))
                              .should(
                                  QueryBuilders.matchQuery("article.source.name", qList[2])
                                      .boost(2)))
                      .build();
                })
            .cache();
    a.subscribe(
        nativeSearchQuery -> {
          log.info("query:" + nativeSearchQuery.toString());
        });
    return a.flatMapMany(articleReactiveRepository::findAllBQ)
        .flatMap(articleSearchHit -> Mono.just(articleSearchHit.getContent().getArticle()))
        .switchIfEmpty(vanillaList(pageRequest));
  }

  public Flux<com.kwabenaberko.newsapilib.models.Article> getPreference() {
    log.info("/preference called");
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = (String) authentication.getPrincipal();
    log.info("username:" + username);
    log.info("sc:" + SecurityContextHolder.getContext().getAuthentication());
    return preferenceRepository.findByUsername(username).flatMap(s -> Flux.just(s.getArticle()));
  }

  public Mono<Preference> savePreference(Article a, String username) {
    System.out.println("FeedService.savePreference called");
    var preference = new Preference();
    preference.setArticle(a);
    preference.setUsername(username);
    preference.setId(IdUtils.id(a.getUrl()));
    return preferenceRepository.save(preference).flatMap(Mono::just);
  }

  public Mono<Void> deletePreference(String id) {
    System.out.println("FeedService.deletePreference called");
    return preferenceRepository.deleteById(id);
  }
}
