package group.msg.at.cloud.common.observability.logging.mdc;

import org.slf4j.MDC;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;

/**
 * {@code JAX-RS ContainerRequestFilter} which adds context information to the underlying logging framework.
 * <p>
 * Adds the following context information to the logging context of the current thread (if available):
 * <ul>
 * <li>Trace-ID of the current {@code OpenTracing} or {@code OpenTelemetry} trace</li>
 * <li>Principal name of the currently authenticated user</li>
 * </ul>
 * </p>
 */
@Provider
@PreMatching
@Priority(Priorities.USER)
public class LoggingMdcContainerRequestFilter implements ContainerRequestFilter {

    private static final String MDC_PROPERTY_NAME_TRACE_ID = "traceId";
    private static final String MDC_PROPERTY_NAME_USER_ID = "userId";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (MDC.get(MDC_PROPERTY_NAME_TRACE_ID) == null) {
            String jaegerTraceHeader = requestContext.getHeaderString("uber-trace-id");
            if (jaegerTraceHeader != null) {
                String traceId = jaegerTraceHeader.split(":")[0];
                MDC.put("traceId", traceId);
            }
            String w3cTraceHeader = requestContext.getHeaderString("traceparent");
            if (w3cTraceHeader != null) {
                String traceId = w3cTraceHeader.split("-")[1];
                MDC.put(MDC_PROPERTY_NAME_TRACE_ID, traceId);
            }
        }
        Principal authenticatedUser = requestContext.getSecurityContext().getUserPrincipal();
        if (authenticatedUser != null) {
            MDC.put(MDC_PROPERTY_NAME_USER_ID, authenticatedUser.getName());
        }
    }

    private boolean shouldFilter(ContainerRequestContext requestContext) {
        return true;
    }
}
