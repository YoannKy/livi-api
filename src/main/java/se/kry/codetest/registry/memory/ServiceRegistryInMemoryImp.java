package se.kry.codetest.registry.memory;

import io.vertx.core.Future;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;
import se.kry.codetest.registry.model.Service;
import se.kry.codetest.registry.ServiceRegistry;
import se.kry.codetest.registry.model.ServiceStatus;

/**
 * An in-memory implementation of the service registry.
 */
public class ServiceRegistryInMemoryImp implements ServiceRegistry {
  private final Hashtable<String, Service> registry;

  public ServiceRegistryInMemoryImp() {
    this.registry = new Hashtable<>();
  }

  @Override
  public Future<Boolean> addService(String serviceName, String serviceUrl) throws IllegalArgumentException {
    validateServiceName(serviceName);
    final URL url = validateServiceUrl(serviceUrl);

    final Service service = new Service();
    service.setName(serviceName);
    service.setUrl(url);
    service.setStatus(ServiceStatus.UNKNOWN);
    service.setAddTime(Instant.now());
    this.registry.putIfAbsent(serviceName, service);
    return Future.succeededFuture(true);
  }

  @Override
  public Future<List<Service>> getServices() {
    return Future.succeededFuture(this.registry.values().stream().collect(Collectors.toList()));
  }

  @Override
  public Future<Boolean> updateServiceStatus(String serviceName, ServiceStatus status) throws IllegalArgumentException {
    if (!this.registry.containsKey(serviceName)) {
      throw new IllegalArgumentException("Service does not exist in the registry");
    }
    final Service service = this.registry.get(serviceName);
    service.setStatus(status);
    this.registry.put(serviceName, service);
    return Future.succeededFuture(true);
  }

  @Override
  public Future<Boolean> removeService(String serviceName) throws IllegalArgumentException {
    if (!this.registry.containsKey(serviceName)) {
      throw new IllegalArgumentException("Service does not exist in the registry");
    }
    this.registry.remove(serviceName);
    return Future.succeededFuture(true);
  }

  private void validateServiceName(final String serviceName) throws IllegalArgumentException {
    if (serviceName == null || serviceName.isEmpty()) {
      throw new IllegalArgumentException("Invalid Service name.");
    }
  }

  private URL validateServiceUrl(String serviceUrl) throws IllegalArgumentException {
    try {
      return new URL(serviceUrl);
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("Malformed url", e);
    }
  }
}
