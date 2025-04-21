package com.github.ggruzdov.sqrddemo.config;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Data
@Validated
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Integer pilotesPriceInCents;
    private Duration orderUpdateLifetime;

    public AppProperties(
        @NotNull
        Float pilotesPrice,
        @NotNull
        Duration orderUpdateLifetime
    ) {
        this.pilotesPriceInCents = Math.round(pilotesPrice * 100);
        this.orderUpdateLifetime = orderUpdateLifetime;
    }
}
