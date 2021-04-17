package com.document.feed.config;

import com.document.feed.model.Article;
import com.document.feed.util.IndexUtils;
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

  private final ReactiveElasticsearchOperations operations;

  @Autowired
  public void setup() {
    var indexOps = operations.indexOps(Article.class);
    indexOps
        .existsTemplate(IndexUtils.TEMPLATE_NAME)
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
                          PutTemplateRequest.builder(
                                  IndexUtils.TEMPLATE_NAME, IndexUtils.TEMPLATE_PATTERN)
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
