package group.msg.at.cloud.common.observability.logging.mdc;

import org.slf4j.MDC;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * {@code JAX-RS ContainerResponseFilter} which releases logging context information bound to the current thread.
 */
@Provider
@Priority(Priorities.USER)
public class LoggingMdcContainerResponseFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        MDC.clear();
    }

    private boolean shouldFilter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        return true;
    }
}
