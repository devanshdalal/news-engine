package com.document.feed.model;

import com.kwabenaberko.newsapilib.models.Article;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "preference")
@Data
public class Preference {

  @Id private String id;

  private Article article;

  private String username;
}
