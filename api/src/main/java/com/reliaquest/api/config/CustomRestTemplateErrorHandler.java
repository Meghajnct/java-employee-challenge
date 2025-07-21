package com.reliaquest.api.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;

public class CustomRestTemplateErrorHandler extends DefaultResponseErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomRestTemplateErrorHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handleError(@NonNull ClientHttpResponse response) throws IOException {
        HttpStatusCode statusCode = response.getStatusCode();

        if (statusCode.value() == 429) {
            // Handle Too Many Requests (429) specifically
            byte[] blankBytesBody = new byte[0];

            String exceptionMessage = "Rate limit exceeded (HTTP 429). For Employees Server";
            // Get retry-after header if available
            if (response.getHeaders().containsKey("Retry-After")) {
                exceptionMessage += " Please try again after " + response.getHeaders().getFirst("Retry-After") + " seconds.";
            }
            logger.warn(exceptionMessage);
            throw new HttpClientErrorException(statusCode, exceptionMessage,
                    response.getHeaders(), blankBytesBody, StandardCharsets.UTF_8);
        } else {
            // For other errors, call the parent class to handle them properly
            super.handleError(response);
        }
    }
}
