package com.document.feed.model;

import com.document.feed.config.ArticleTemplateInitializer;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class ArticleRepositoryMultiIndexImpl implements ArticleRepositoryMultiIndex<Article> {
  private final ReactiveElasticsearchOperations operations;

  @Override
  public Flux<Article> saveMany(Iterable<Article> entities) {
    var indexCoordinates = indexName();
    var indexOps = operations.indexOps(indexCoordinates);
    return indexOps
        .exists()
        .flatMapMany(
            exists ->
                Boolean.TRUE.equals(exists)
                    ? operations.saveAll(entities, indexCoordinates)
                    : indexOps
                        .create()
                        .flatMapMany(aBoolean -> operations.saveAll(entities, indexCoordinates)));
  }

  @Override
  public Flux<SearchHit<Article>> findAllBQ(NativeSearchQuery query) {
    return operations.search(query, Article.class);
  }

  private IndexCoordinates indexName() {
    var indexName =
        ArticleTemplateInitializer.INDEX_NAME
            + "-"
            + LocalDateTime.now()
                .truncatedTo(ChronoUnit.HOURS)
                .toString()
                .replace(':', '-')


            
                .toLowerCase();
    return IndexCoordinates.of(indexName);
  }
}
