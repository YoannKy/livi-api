package se.kry.codetest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import se.kry.codetest.registry.model.Service;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  @DisplayName("Start a web server on localhost responding to GET /service on port 8080")
  @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
  void testGetService(Vertx vertx, VertxTestContext testContext) {
    WebClient.create(vertx)
        .get(8080, "::1", "/service")
        .send(response -> testContext.verify(() -> {
          assertEquals(200, response.result().statusCode());
          testContext.completeNow();
        }));
  }

    @Test
    @DisplayName("Start a web server on localhost responding to POST /service on port 8080")
    @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
    void testPostService(Vertx vertx, VertxTestContext testContext) throws MalformedURLException {
        final Service service = new Service();
        service.setName("test-" + UUID.randomUUID().toString());
        service.setUrl(new URL("http://test.com"));
        WebClient.create(vertx)
                .post(8080, "::1", "/service")
                .sendJson(service, response -> testContext.verify(() -> {
                    assertEquals(204, response.result().statusCode());
                    testContext.completeNow();}));

        WebClient.create(vertx)
                .get(8080, "::1", "/service")
                .send(response -> testContext.verify(() -> {
                    assertEquals(200, response.result().statusCode());
                    testContext.completeNow();
                }));

        WebClient.create(vertx)
                .delete(8080, "::1", "/service")
                .sendJson(service, response -> testContext.verify(() -> {
                    assertEquals(204, response.result().statusCode());
                    testContext.completeNow();}));
    }

    @Test
    @DisplayName("Test Post /service twice fails")
    @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
    void testPostServiceTwice(Vertx vertx, VertxTestContext testContext) throws MalformedURLException {
        final Service service = new Service();
        service.setName("test-twice-" + UUID.randomUUID().toString());
        service.setUrl(new URL("http://test.com"));
        WebClient.create(vertx)
                .post(8080, "::1", "/service")
                .sendJson(service, response -> testContext.verify(() -> {
                    assertEquals(204, response.result().statusCode());
                    testContext.completeNow();
                }));
        WebClient.create(vertx)
                .post(8080, "::1", "/service")
                .sendJson(service, response -> testContext.verify(() -> {
                    assertNull(response.cause());
                    testContext.completeNow();
                }));
    }
}
