package com.document.feed.util;

import static org.apache.commons.lang3.StringUtils.reverse;
import static org.apache.commons.lang3.StringUtils.strip;

public class IdUtils {
  private IdUtils() {}

  public static String id(String url) {
    return reverse(strip(url, "/"));
  }
}
