package com.document.feed.model;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PreferenceReactiveRepository extends ReactiveCrudRepository<Preference, String> {
  Flux<Preference> findByUsername(String username);
}
