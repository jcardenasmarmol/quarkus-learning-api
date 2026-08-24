package com.jcard.quarkus;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Quarkus Learning API",
                version = "1.0",
                description = "REST API for managing tasks"
        )
)
public class OpenApiConfig {
}