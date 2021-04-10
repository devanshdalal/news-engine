package com.document.feed.config;

import com.document.feed.model.Article;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.index.AliasAction;
import org.springframework.data.elasticsearch.core.index.AliasActionParameters;
import org.springframework.data.elasticsearch.core.index.AliasActions;
import org.springframework.data.elasticsearch.core.index.PutTemplateRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ArticleTemplateInitializer {
  public static final String TEMPLATE_NAME = "article-template";
  public static final String INDEX_NAME = "article";
  public static final String TEMPLATE_PATTERN = INDEX_NAME + "-*";

  private final ReactiveElasticsearchOperations operations;

  @Autowired
  public void setup() {
    var indexOps = operations.indexOps(Article.class);
    indexOps
        .existsTemplate(TEMPLATE_NAME)
        .subscribe(
            exists -> {
              if (!exists) {
                var mapping = indexOps.createMapping();
                var aliasActions =
                    new AliasActions()
                        .add(
                            new AliasAction.Add(
                                AliasActionParameters.builderForTemplate()
                                    .withAliases(indexOps.getIndexCoordinates().getIndexNames())
                                    .build()));
                mapping.subscribe(
                    document -> {
                      var request =
                          PutTemplateRequest.builder(TEMPLATE_NAME, TEMPLATE_PATTERN)
                              .withMappings(document)
                              .withAliasActions(aliasActions)
                              .build();
                      indexOps
                          .putTemplate(request)
                          .subscribe(
                              aBoolean -> {
                                log.info("PutTemplate successful: " + aBoolean);
                              });
                    });
              }
            });
  }
}
