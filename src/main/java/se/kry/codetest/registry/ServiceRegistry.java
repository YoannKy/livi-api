package se.kry.codetest.registry;

import java.util.List;

import io.vertx.core.Future;
import se.kry.codetest.registry.model.Service;
import se.kry.codetest.registry.model.ServiceStatus;

/**
 * Abstraction of the service registry.
 */
public interface ServiceRegistry {
    /**
     * Adds a service to registry.
     * @param serviceName The name of the service.
     * @param serviceUrl The url of the service.
     * @return The future to retrieve the result.
     * @throws IllegalArgumentException if the service name is not valid or if the url is not valid.
     */
    Future<Boolean> addService(final String serviceName, final String serviceUrl)
            throws IllegalArgumentException;

    /**
     * Gets the list of services in the registry. An empty list is returned if the registry is empty.
     * @return The future to retrieve the list of services.
     */
    Future<List<Service>> getServices();

    /**
     * Updates the status of a service in the registry.
     * @param serviceName The name of the service.
     * @param status The new status
     * @return The future to retrieve the result.
     * @throws IllegalArgumentException if the service is not present in the registry.
     */
    Future<Boolean> updateServiceStatus(final String serviceName, final ServiceStatus status) throws IllegalArgumentException;

    /**
     * Removes a service from the registry.
     * @param serviceName The name of the service to be removed.
     * @return The future to retrieve the result.
     * @throws IllegalArgumentException if the service does not exists in the registry.
     */
    Future<Boolean> removeService(final String serviceName) throws IllegalArgumentException;
}
