package group.msg.at.cloud.common.observability.rest.trace;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * {@code JAX-RS ContainerRequestFilter} which traces inbound requests from upstream services or frontends.
 * <p>
 * In contrast to ClientRequestFilters both CDI injection and automatic detection works well for ContainerRequestFilters
 * on all MicroProfile capable application servers.
 * </p>
 */
@Provider
public class RestTraceContainerRequestFilter implements ContainerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestTraceConstants.REST_TRACE_LOGGER_NAME);

    @Inject
    @ConfigProperty(name = RestTraceConstants.ENABLED_CONFIG_KEY, defaultValue = RestTraceConstants.ENABLED_DEFAULT_VALUE)
    boolean enabled;

    @Inject
    RestTraceMessageBuilder messageBuilder;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (shouldFilter(requestContext)) {
            StringBuilder traceMessage = new StringBuilder();
            this.messageBuilder.build(traceMessage, requestContext);
            LOGGER.info(traceMessage.toString());
        }
    }

    private boolean shouldFilter(ContainerRequestContext requestContext) {
        return enabled && LOGGER.isInfoEnabled() && !requestContext.getUriInfo().getPath().contains("probes");
    }
}
