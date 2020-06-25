package se.kry.codetest.registry.model;

/**
 * Describes the status of a registered service.
 */
public enum ServiceStatus {

    /**
     * The service status has not been checked yet.
     */
    UNKNOWN,

    /**
     * The service returned a 200 for the latest request.
     */
    OK,

    /**
     * The call to the service either failed or the service did not return 200 for the last request.
     */
    FAIL
}
