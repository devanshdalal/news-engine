package com.document.feed.model;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleReactiveRepository
    extends ReactiveCrudRepository<Article, String>, ArticleRepositoryMultiIndex<Article> {}
