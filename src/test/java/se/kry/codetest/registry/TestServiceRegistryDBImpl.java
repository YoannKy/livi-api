package se.kry.codetest.registry;

import java.net.MalformedURLException;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import se.kry.codetest.MainVerticle;
import se.kry.codetest.registry.db.ServiceRegistryDBImp;
import se.kry.codetest.registry.model.Service;
import se.kry.codetest.registry.model.ServiceStatus;

@ExtendWith(VertxExtension.class)
public class TestServiceRegistryDBImpl {
    ServiceRegistryDBImp serviceRegistryDBImp;
    @BeforeEach
    void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
        vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
        serviceRegistryDBImp = new ServiceRegistryDBImp(vertx);
    }

    @Test
    public void testAddService_withInvalidName(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            serviceRegistryDBImp.addService(null, "http://www.google.com");
        });
    }

    @Test
    public void testAddService_withInvalidUrl() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            serviceRegistryDBImp.addService("test", "aa");
        });
    }

    @Test
    public void testAddDeleteService(VertxTestContext testContext) throws MalformedURLException {
        final String serviceName = "test-" + UUID.randomUUID().toString();
        Future<Boolean> futureAdd = serviceRegistryDBImp.addService(serviceName,
                                                        "http://www.google.fr");
        futureAdd.setHandler(res -> testContext.verify(() -> {
            Assertions.assertTrue(res.succeeded());
        }));

        Future<List<Service>> futureGet = serviceRegistryDBImp.getServices();
        futureGet.setHandler(res -> {
                testContext.verify(() -> {
                    Assertions.assertTrue(res.succeeded());
                    Assertions.assertTrue(res.result().stream().anyMatch(service -> service.getName().equals(serviceName)));
                });
        });

        Future<Boolean> futureDelete = serviceRegistryDBImp.removeService(serviceName);
        futureDelete.setHandler(res -> testContext.verify(() -> {
            Assertions.assertTrue(res.succeeded());
        }));

        futureGet = serviceRegistryDBImp.getServices();
        futureGet.setHandler(res -> {
            testContext.verify(() -> {
                Assertions.assertTrue(res.succeeded());
                Assertions.assertFalse(res.result().stream().anyMatch(service -> service.getName().equals(serviceName)));
            });
            testContext.completeNow();
        });
    }

    @Test
    public void testAddTwice(VertxTestContext testContext) throws MalformedURLException {
        final String serviceName = "test-" + UUID.randomUUID().toString();
        Future<Boolean> futureAdd = serviceRegistryDBImp.addService(serviceName,
                                                                    "http://www.google.fr");
        futureAdd.setHandler(res -> testContext.verify(() -> {
            Assertions.assertTrue(res.succeeded());
        }));

        futureAdd = serviceRegistryDBImp.addService(serviceName,
                                                    "http://www.google.fr");
        futureAdd.setHandler(res -> testContext.verify(() -> {
            Assertions.assertTrue(res.failed());
            testContext.completeNow();
        }));
    }

    @Test
    public void testAddUpdateService(VertxTestContext testContext) throws MalformedURLException {
        final String serviceName = "test-" + UUID.randomUUID().toString();
        Future<Boolean> futureAdd = serviceRegistryDBImp.addService(serviceName,
                                                                    "http://www.google.fr");
        futureAdd.setHandler(res -> testContext.verify(() -> {
            Assertions.assertTrue(res.succeeded());
        }));

        Future<Boolean> futureUpdate = serviceRegistryDBImp.updateServiceStatus(serviceName, ServiceStatus.OK);
        futureUpdate.setHandler(res -> {
            testContext.verify(() -> {
                Assertions.assertTrue(res.succeeded());
            });
        });

        Future<List<Service>> futureGet = serviceRegistryDBImp.getServices();
        futureGet.setHandler(res -> {
            testContext.verify(() -> {
                Assertions.assertTrue(res.succeeded());
                Assertions.assertTrue(res.result().stream().anyMatch(service -> service.getName().equals(serviceName)
                && service.getStatus().equals(ServiceStatus.OK) ));
            });
        });

        futureUpdate = serviceRegistryDBImp.updateServiceStatus(serviceName, ServiceStatus.FAIL);
        futureUpdate.setHandler(res -> {
            testContext.verify(() -> {
                Assertions.assertTrue(res.succeeded());
            });
        });

        futureGet = serviceRegistryDBImp.getServices();
        futureGet.setHandler(res -> {
            testContext.verify(() -> {
                Assertions.assertTrue(res.succeeded());
                Assertions.assertTrue(res.result().stream().anyMatch(service -> service.getName().equals(serviceName)
                        && service.getStatus().equals(ServiceStatus.FAIL) ));
            });
        });

        Future<Boolean> futureDelete = serviceRegistryDBImp.removeService(serviceName);
        futureDelete.setHandler(res -> testContext.verify(() -> {
            Assertions.assertTrue(res.succeeded());
        }));

        futureGet = serviceRegistryDBImp.getServices();
        futureGet.setHandler(res -> {
            testContext.verify(() -> {
                Assertions.assertTrue(res.succeeded());
                Assertions.assertFalse(res.result().stream().anyMatch(service -> service.getName().equals(serviceName)));
            });
            testContext.completeNow();
        });
    }
}
