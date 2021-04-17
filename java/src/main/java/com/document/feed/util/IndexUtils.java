package com.document.feed.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class IndexUtils {
  public static final String TEMPLATE_NAME = "article-template";
  public static final String INDEX_NAME = "article";
  public static final String TEMPLATE_PATTERN = INDEX_NAME + "-*";

  public static String timeToText(LocalDateTime time) {
    return time.truncatedTo(ChronoUnit.MILLIS).toString().replace(':', '-').replace('T', '.');
  }

  public static LocalDateTime textToTime(String text) {
    return LocalDateTime.parse(text.replace('-', ':').replace('.', 'T'));
  }
}
