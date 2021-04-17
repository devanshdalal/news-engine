package com.document.feed.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.json.JSONArray;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ESMaintenance {
  private final RestHighLevelClient restHighLevelClient;
  private final int MAX_AGE = 2; // days

  public void deleteOldIndexes() {

    try {
      var restClient = restHighLevelClient.getLowLevelClient();

      Response response = null;
      try {
        var request = new Request("GET", "/_cat/indices?v&format=json");
        response = restClient.performRequest(request);
      } catch (IOException e) {
        log.warn(e.toString(), e);
      }

      var entity = response.getEntity();
      if (entity != null) {
        var indexes = new JSONArray(IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8));
        var indexList =
            IntStream.range(0, indexes.length())
                .mapToObj(indexes::getJSONObject)
                .collect(Collectors.toList());

        indexList.stream()
            .filter(jsonObject -> shouldBeDeleted(jsonObject.get("index").toString()))
            .map(jsonObject -> jsonObject.get("index").toString())
            .forEach(
                s -> {
                  try {
                    var r =
                        restHighLevelClient
                            .indices()
                            .delete(new DeleteIndexRequest(s), RequestOptions.DEFAULT);
                  } catch (IOException e) {
                    log.error("exception: " + e.getMessage());
                  }
                });

        //      log.info("content:" + IOUtils.toString(entity.getContent(),
        // StandardCharsets.UTF_8));
      }
    } catch (Exception e) {
      log.error("Received exception: " + e.getMessage());
    }
  }

  private boolean shouldBeDeleted(String name) {
    if (!name.contains(IndexUtils.INDEX_NAME)) {
      return false;
    }
    return IndexUtils.textToTime(StringUtils.removeStart(name, IndexUtils.INDEX_NAME + "-"))
        .plusDays(MAX_AGE)
        .isBefore(LocalDateTime.now());
  }
}
