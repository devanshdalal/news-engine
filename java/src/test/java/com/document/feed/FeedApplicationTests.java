// package com.document.feed;
//
// import static org.apache.commons.lang3.StringUtils.reverse;
// import static org.apache.commons.lang3.StringUtils.strip;
//
// import com.document.feed.model.Article;
// import com.document.feed.model.ArticleReactiveRepository;
// import com.document.feed.model.ArticleRepositoryMultiIndexImpl;
// import com.document.feed.util.ESMaintenance;
// import com.kwabenaberko.newsapilib.NewsApiClient;
// import com.kwabenaberko.newsapilib.models.request.TopHeadlinesRequest;
// import com.kwabenaberko.newsapilib.models.response.ArticleResponse;
// import java.io.IOException;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
//
// @SpringBootTest
// class FeedApplicationTests {
//
//  @Autowired ArticleReactiveRepository repository;
//
//  @Autowired ArticleRepositoryMultiIndexImpl articleRepositoryCustom;
//
//  @Autowired ESMaintenance esMaintenance;
//
//  @Test
//  public void testListAll() throws InterruptedException {
//    var a = repository.findAll().buffer().blockLast();
//    Thread.sleep(10000);
//  }
//
//  @Test
//  public void testNewsAPI() throws InterruptedException {
//    var newsApiClient = new NewsApiClient("7bca7fe0b1cf411082fc45d8d78b4dd4");
//
//    newsApiClient.getTopHeadlines(
//        new
// TopHeadlinesRequest.Builder().country("in").category("business").language("en").build(),
//        new NewsApiClient.ArticlesResponseCallback() {
//          @Override
//          public void onSuccess(ArticleResponse response) {
//            response
//                .getArticles()
//                .forEach(
//                    article -> {
//                      var v = article;
//                      var a = new Article();
//                      a.setArticle(article);
//                      a.setId(reverse(strip(article.getUrl(), "/")));
//                      var x = repository.save(a).block();
//                      System.out.println(article.getTitle());
//                    });
//          }
//
//          @Override
//          public void onFailure(Throwable throwable) {
//            System.out.println(throwable.getMessage());
//          }
//        });
//    Thread.sleep(10000);
//  }
//
//  // tomicI@Test
//  ////  public void testFindAll() throws InterruptedException {
//  ////    AtomicInteger found = new AtomicInteger();
//  //    articleRepositoryCustom
//  //        .findAll(Pageable.unpaged())
//  //        .subscribe(
//  //            searchHits -> {
//  //              searchHits.forEach(
//  //                  articleSearchHit -> {
//  //                    System.out.println("found:" + found.incrementAndGet());
//  //                    System.out.println(articleSearchHit.getContent().getArticle().getTitle());
//  //                  });
//  //            });
//  //    Thread.sleep(5000);
//  //  }
//  @Test
//  public void testDelete() throws IOException {
//    esMaintenance.deleteOldIndexes();
//  }
// }
