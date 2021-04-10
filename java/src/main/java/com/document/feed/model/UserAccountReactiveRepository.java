package com.document.feed.model;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserAccountReactiveRepository extends ReactiveCrudRepository<UserAccount, String> {
  Mono<UserAccount> findByUsername(String userName);

  Mono<UserAccount> save(UserAccount entity);
}
