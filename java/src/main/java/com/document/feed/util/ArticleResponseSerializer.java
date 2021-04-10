package com.document.feed.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.ehcache.core.spi.service.FileBasedPersistenceContext;
import org.ehcache.spi.serialization.Serializer;
import org.ehcache.spi.serialization.SerializerException;

public class ArticleResponseSerializer implements Serializer<ArticleResponse> {
  private final ObjectMapper objectMapper = new ObjectMapper();

  public ArticleResponseSerializer(ClassLoader classLoader, FileBasedPersistenceContext context) {}

  @Override
  public ByteBuffer serialize(ArticleResponse object) throws SerializerException {
    try {
      return ByteBuffer.wrap(
          objectMapper.writeValueAsString(object).getBytes(StandardCharsets.UTF_8));
    } catch (JsonProcessingException e) {
      throw new SerializerException(e);
    }
  }

  @Override
  public ArticleResponse read(ByteBuffer binary)
      throws ClassNotFoundException, SerializerException {
    try {
      return objectMapper.readValue(new String(binary.array()), ArticleResponse.class);
    } catch (JsonProcessingException e) {
      throw new SerializerException(e);
    }
  }

  @Override
  public boolean equals(ArticleResponse object, ByteBuffer binary)
      throws ClassNotFoundException, SerializerException {
    return serialize(object).equals(binary);
  }
}
