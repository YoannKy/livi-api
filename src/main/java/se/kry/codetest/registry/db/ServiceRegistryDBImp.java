package se.kry.codetest.registry.db;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import se.kry.codetest.registry.model.Service;
import se.kry.codetest.registry.ServiceRegistry;
import se.kry.codetest.registry.model.ServiceStatus;

/**
 * A concrete implementation of the service registry backed by a SQL DB.
 */
public class ServiceRegistryDBImp implements ServiceRegistry {
    private final DBConnector dbConnector;
    private final String SQL_QUERY_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS Services (" +
            "name VARCHAR(128) NOT NULL, " +
            "url VARCHAR(128) NOT NULL, " +
            "status VARCHAR(128) NOT NULL, " +
            "dateAdded DATE NOT NULL," +
            "PRIMARY KEY (name));";
    private final String SQL_QUERY_GET_ALL = "SELECT * FROM Services;";
    private final String SQL_QUERY_ADD_SERVICE = "INSERT INTO Services VALUES (?, ?, ?, ?);";
    private final String SQL_QUERY_UPDATE_STATUS = "UPDATE Services SET status = ? WHERE name = ?;";
    private final String SQL_QUERY_DELETE_STATUS = "DELETE FROM Services WHERE name = ?;";

    /**
     * Constructor.
     * @param vertx The vertx.
     */
    public ServiceRegistryDBImp(Vertx vertx) {
        this.dbConnector = new DBConnector(vertx);
    }

    /**
     * Initializes the DB by creating the table if needed.
     * @return The future to retrieve the result.
     */
    public Future<ServiceRegistry> initializeDb() {
        final Future future = Future.future();
        this.dbConnector.query(SQL_QUERY_CREATE_TABLE).setHandler(done -> {
            if (done.succeeded()) {
                System.out.println("completed db migrations");
                future.complete(this);
            } else {
                done.cause().printStackTrace();
                future.fail(done.cause());
            }
        });
        return future;
    }

    @Override
    public Future<Boolean> addService(String serviceName,
                           String serviceUrl) throws IllegalArgumentException{
        validateServiceName(serviceName);
        validateServiceUrl(serviceUrl);
        final Future future = Future.future();
        final JsonArray params = new JsonArray()
                .add(serviceName)
                .add(serviceUrl)
                .add(ServiceStatus.UNKNOWN)
                .add(Instant.now());
        dbConnector.query(SQL_QUERY_ADD_SERVICE, params).setHandler(res -> {
            if (res.succeeded()) {
                future.complete(Boolean.TRUE);
            } else {
                future.fail(res.cause());
            }
        });
        return future;
    }

    @Override
    public Future<List<Service>> getServices() {
        final Future future = Future.future();
        final List<Service> services = new ArrayList();
        System.out.println("GET Services");
        dbConnector.query(SQL_QUERY_GET_ALL).setHandler(event -> {
            if (event.succeeded()) {
                final List<JsonArray> results = event.result().getResults();
                for (JsonArray res:results) {
                    try {
                        final Service service = new Service();
                        service.setName(res.getString(0));
                        service.setUrl(new URL(res.getString(1)));
                        service.setStatus(ServiceStatus.valueOf(res.getString(2)));
                        service.setAddTime(Instant.ofEpochMilli(res.getLong(3)));
                        services.add(service);
                    } catch (MalformedURLException e) {

                    }
                }
                future.complete(services);
            } else {
                future.fail(event.cause());
            }
        });
        return future;
    }

    @Override
    public Future<Boolean> updateServiceStatus(String serviceName,
                                    ServiceStatus status) throws IllegalArgumentException {
        validateServiceName(serviceName);
        final Future future = Future.future();
        final JsonArray params = new JsonArray().add(status).add(serviceName);
        dbConnector.query(SQL_QUERY_UPDATE_STATUS, params).setHandler(res -> {
            if (res.succeeded()) {
                future.complete(Boolean.TRUE);
            } else {
                future.fail(res.cause());
            }
        });
        return future;
    }

    @Override
    public Future removeService(String serviceName) throws IllegalArgumentException {
        validateServiceName(serviceName);
        final Future future = Future.future();
        final JsonArray params = new JsonArray().add(serviceName);
        dbConnector.query(SQL_QUERY_DELETE_STATUS, params).setHandler(res -> {
            if (res.succeeded()) {
                future.complete(Boolean.TRUE);
            } else {
                System.out.println("Service could not be removed. name: " + serviceName + "  cause:" +res.cause());
                future.fail(res.cause());
            }
        });
        return future;
    }

    private void validateServiceName(final String serviceName) throws IllegalArgumentException {
        if (serviceName == null || serviceName.isEmpty()) {
            throw new IllegalArgumentException("Invalid Service name.");
        }
    }

    private void validateServiceUrl(String serviceUrl) throws IllegalArgumentException {
        try {
            new URL(serviceUrl);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed url", e);
        }
    }
}
