package se.kry.codetest.registry.model;

import java.net.URL;
import java.time.Instant;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Describes a service registered in the internal registry.
 */
public class Service {
    private String name;
    private URL url;
    private Instant addTime;
    private ServiceStatus status;

    /**
     * @return The name of the service.
     */
    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return The url of the service.
     */
    public URL getUrl() {
        return this.url;
    }

    public void setUrl(final URL url) {
        this.url = url;
    }

    /**
     * @return The time at which the service was added in the registry.
     */
    public Instant getAddTime() {
        return this.addTime;
    }
    public void setAddTime(final Instant addTime) {
        this.addTime = addTime;
    }

    /**
     * @return The status of the service
     */
    public ServiceStatus getStatus() {
        return this.status;
    }

    public void setStatus(final ServiceStatus status) {
        this.status = status;
    }

    /**
     * Serializes the given Service into a json payload.
     * @param service The service to be serialized.
     * @return The json payload.
     */
    public static String toString(Service service) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(service);
        } catch (Exception e) {
            return null;
        }
    }
}
