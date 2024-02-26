package com.virtualwallet.configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@SecurityScheme(
        name = "Authorization",
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER,
        scheme = "basic"
)
public class SwaggerConfig {
}
