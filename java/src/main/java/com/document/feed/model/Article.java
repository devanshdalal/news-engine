package com.document.feed.model;

import com.document.feed.config.ArticleTemplateInitializer;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = ArticleTemplateInitializer.INDEX_NAME, createIndex = false)
@RequiredArgsConstructor
@Data
// @JsonInclude(JsonInclude.Include.NON_NULL)
// @JsonIgnoreProperties("nullField")
public class Article {

  @Id private String id;

  @Field(type = FieldType.Object, includeInParent = true, ignoreMalformed = true)
  private com.kwabenaberko.newsapilib.models.Article article;

  @Field(type = FieldType.Text)
  private String country;

  @Field(type = FieldType.Text)
  private String category;

  private String nullField;
}
