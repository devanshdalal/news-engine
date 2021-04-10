package com.document.feed.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@NoArgsConstructor
@Document(indexName = "user-account")
public class UserAccount {

  @Id private String id;

  @NonNull
  @Field(type = FieldType.Text)
  private String username;

  @NonNull
  @Field(type = FieldType.Text)
  private String password;
}
