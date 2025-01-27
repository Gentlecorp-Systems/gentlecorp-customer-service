package com.gentlecorp.customer.dev;

import com.mongodb.client.MongoClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.gentlecorp.customer.dev.DevConfig.DEV;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.http.ResponseEntity.ok;

@Controller
@RequestMapping("/dev")
@RequiredArgsConstructor
@Slf4j
@Profile(DEV)
public class DbPopulateController {

  private final MongoClient mongoClient;
  private final DatabaseInitializer databaseInitializer;

  @PostMapping(value = "db_populate", produces = TEXT_PLAIN_VALUE)
  public ResponseEntity<String> dbPopulate() {
    log.warn("Die MongoDB wird neu geladen");

    // Lösche die Datenbank
    String databaseName = "gentlecorp"; // Name deiner MongoDB-Datenbank
    mongoClient.getDatabase(databaseName).drop();
    log.warn("Die MongoDB-Datenbank wurde gelöscht");

    // Initialisiere die Datenbank erneut
    databaseInitializer.run();
    log.warn("Die MongoDB wurde neu geladen");

    return ok("ok");
  }
}
