package se.kry.codetest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import se.kry.codetest.registry.model.Service;
import se.kry.codetest.registry.ServiceRegistry;

@ExtendWith(VertxExtension.class)
public class TestBackgroundPoller {
    BackgroundPoller backgroundPoller;
    ServiceRegistry serviceRegistryMock;
    @BeforeEach
    void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
        vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
        serviceRegistryMock = BDDMockito.mock(ServiceRegistry.class);
        backgroundPoller = new BackgroundPoller(vertx, serviceRegistryMock);
    }

    @Test
    @DisplayName("Test poll with empty registry")
    @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
    void testPollWithEmptyRegistry(VertxTestContext testContext) {
        Future<List<Service>> future = Future.future();
        future.complete(Collections.emptyList());
        Mockito.when(serviceRegistryMock.getServices()).thenReturn(future);
        backgroundPoller.pollServices();

        testContext.verify(() -> {
            Mockito.verify(serviceRegistryMock).getServices();
            Mockito.verifyNoMoreInteractions(serviceRegistryMock);
            testContext.completeNow();
        });
    }

    @Test
    @DisplayName("Test poll with a single element in the registry")
    @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
    void testPollWithSingleElementInRegistry(VertxTestContext testContext)
            throws MalformedURLException {
        Future<List<Service>> future = Future.future();
        Service service = new Service();
        service.setName("test");
        service.setUrl(new URL("http://www.google.com"));
        future.complete(Collections.singletonList(service));
        Mockito.when(serviceRegistryMock.getServices()).thenReturn(future);
        backgroundPoller.pollServices();

        testContext.verify(() -> {
            Mockito.verify(serviceRegistryMock).getServices();
            try {
                // TODO: find a way to retrieve the result of the poll.
                Thread.sleep(5000);
            } catch (Exception e) {

            }
            Mockito.verify(serviceRegistryMock).updateServiceStatus(Mockito.eq("test"), Mockito.any());
            testContext.completeNow();
        });
    }

    @Test
    @DisplayName("Test poll with a single element in the registry")
    @Timeout(value = 20, timeUnit = TimeUnit.SECONDS)
    void testPollWith2ElementInRegistry(VertxTestContext testContext)
            throws MalformedURLException {
        Future<List<Service>> future = Future.future();
        Service service = new Service();
        service.setName("test");
        service.setUrl(new URL("http://www.google.com"));
        Service service2 = new Service();
        service2.setName("test2");
        service2.setUrl(new URL("http://www.google.fr"));
        future.complete(Arrays.asList(service, service2));
        Mockito.when(serviceRegistryMock.getServices()).thenReturn(future);
        backgroundPoller.pollServices();

        testContext.verify(() -> {
            Mockito.verify(serviceRegistryMock).getServices();
            try {
                // TODO: find a way to retrieve the result of the poll.
                Thread.sleep(15000);
            } catch (Exception e) {

            }
            Mockito.verify(serviceRegistryMock, Mockito.times(2)).updateServiceStatus(Mockito.anyString(), Mockito.any());
            testContext.completeNow();
        });
    }
}
