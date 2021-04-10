package com.document.feed;

import com.document.feed.model.Article;
import com.document.feed.service.NewsService;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kwabenaberko.newsapilib.NewsApiClient;
import com.kwabenaberko.newsapilib.models.request.SourcesRequest;
import com.kwabenaberko.newsapilib.models.response.SourcesResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class NewsServiceTest {
  @Autowired NewsService newsService;

  @Test
  public void testBasic() throws InterruptedException {
    //    var r = newsService.mimicNewsAPIOrg("v2/top-headlines",
    //        Map.of("country", "in", "category", "business"));
    //    r.block().getArticles().forEach(article -> System.out.println(article.getTitle()));
    //        var r = newsService.mimicNewsAPIOrg("v2/everything",
    //        Map.of("q", "bitcoin"));
    //    r.block().getArticles().forEach(article -> System.out.println(article.getTitle()));
    var newsApiClient = new NewsApiClient("7bca7fe0b1cf411082fc45d8d78b4dd4");

    newsApiClient.getSources(
        new SourcesRequest.Builder().category("business").language("en").build(),
        new NewsApiClient.SourcesCallback() {
          @Override
          public void onSuccess(SourcesResponse response) {
            System.out.println(response.getSources().get(0).getName());
          }

          @Override
          public void onFailure(Throwable throwable) {
            System.out.println(throwable.getMessage());
          }
        });
    Thread.sleep(10000);
  }

  @Test
  public void whenNotHidden_thenCorrect() throws JsonProcessingException {
    var article = new Article();
    article.setCountry("in");
    article.setCategory("sports");
    article.setCategory("sports");

    var mapper = new ObjectMapper();
    mapper.setSerializationInclusion(Include.NON_NULL);

    System.out.println(mapper.writeValueAsString(article));
  }
}
