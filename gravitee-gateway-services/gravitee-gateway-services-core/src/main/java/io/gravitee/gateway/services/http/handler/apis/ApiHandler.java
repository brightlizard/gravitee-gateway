package io.gravitee.gateway.services.http.handler.apis;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.gravitee.common.http.HttpHeaders;
import io.gravitee.common.http.HttpStatusCode;
import io.gravitee.common.http.MediaType;
import io.gravitee.gateway.handlers.api.definition.Api;
import io.gravitee.gateway.handlers.api.manager.ApiManager;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class ApiHandler implements Handler<RoutingContext> {

    private final Logger LOGGER = LoggerFactory.getLogger(ApiHandler.class);

    @Autowired
    private ApiManager apiManager;

    @Override
    public void handle(RoutingContext ctx) {
        HttpServerResponse response = ctx.response();

        try {
            String sApi = ctx.request().getParam("apiId");
            Api api = apiManager.get(sApi);

            if (api == null) {
                response.setStatusCode(HttpStatusCode.NOT_FOUND_404);
            } else {
                response.putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
                response.setStatusCode(HttpStatusCode.OK_200);
                response.setChunked(true);

                Json.prettyMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                response.write(Json.prettyMapper.writeValueAsString(api));
            }
        } catch (JsonProcessingException jpe) {
            response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR_500);
            LOGGER.error("Unable to transform data object to JSON", jpe);
        }

        response.end();
    }
}
