package se.kry.codetest;

import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import se.kry.codetest.registry.model.Service;
import se.kry.codetest.registry.ServiceRegistry;
import se.kry.codetest.registry.model.ServiceStatus;

/**
 * Polls the services in the registry.
 */
public class BackgroundPoller {
    private final WebClient webclient;
    private final ServiceRegistry registry;

    /**
     * Constructor.
     *
     * @param vertx    The vertx used for http calls.
     * @param registry The registry providing the list of services.
     */
    public BackgroundPoller(Vertx vertx, ServiceRegistry registry) {
        this.registry = registry;
        this.webclient = WebClient.create(vertx);
    }

    /**
     * Polls the services in the registry and updates their status.
     */
    public void pollServices() {
        System.out.println("POLL");
        registry.getServices().setHandler(res -> {
            if (res.succeeded()) {
                for (Service service : res.result()) {
                    pollServiceAndUpdateStatus(service);
                }
            }
        });
    }

    private void pollServiceAndUpdateStatus(final Service service) {
        webclient.getAbs(service.getUrl().toString())
                .timeout(5000)
                .send(ar -> updateStatus(service, ar));
    }

    private void updateStatus(final Service service, final AsyncResult<HttpResponse<Buffer>> pollResult) {
        if (pollResult.succeeded()) {
            HttpResponse<Buffer> response = pollResult.result();
            if (response.statusCode() == 200) {
                registry.updateServiceStatus(service.getName(), ServiceStatus.OK);
            } else {
                registry.updateServiceStatus(service.getName(), ServiceStatus.FAIL);
            }
        } else {
            registry.updateServiceStatus(service.getName(), ServiceStatus.FAIL);
        }
    }
}
