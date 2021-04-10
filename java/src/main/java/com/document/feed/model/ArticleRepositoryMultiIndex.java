package com.document.feed.model;

import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import reactor.core.publisher.Flux;

public interface ArticleRepositoryMultiIndex<T> {
  Flux<T> saveMany(Iterable<T> entities);

  Flux<SearchHit<Article>> findAllBQ(NativeSearchQuery query);
}
