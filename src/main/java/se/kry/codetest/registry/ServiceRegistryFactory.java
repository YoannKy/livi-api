package se.kry.codetest.registry;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import se.kry.codetest.registry.db.ServiceRegistryDBImp;
import se.kry.codetest.registry.memory.ServiceRegistryInMemoryImp;

/**
 * The factory to create to registry
 */
public class ServiceRegistryFactory {

    /**
     * Creates a registry backed by a SQL DB.
     * @param vertx The vertx used for the DB queries.
     * @return The future to retrieve the result.
     */
    public static Future<ServiceRegistry> createDbBasedRegistry(Vertx vertx) {
        ServiceRegistryDBImp serviceRegistryDBImp = new ServiceRegistryDBImp(vertx);
        return serviceRegistryDBImp.initializeDb();
    }

    /**
     * Creates a in-memory registry.
     * @param vertx The vertx used for the DB queries.
     * @return The future to retrieve the result.
     */
    public static Future<ServiceRegistry> createInMemoryRegistry(Vertx vertx) {
        return Future.succeededFuture(new ServiceRegistryInMemoryImp());
    }
}
