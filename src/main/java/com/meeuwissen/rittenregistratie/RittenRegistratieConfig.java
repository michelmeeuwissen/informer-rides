package com.meeuwissen.rittenregistratie;

import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.HttpComponentsClientHttpConnector;
import org.springframework.web.client.RestClient;

import java.util.function.Consumer;

@Configuration
public class RittenRegistratieConfig {
    @Value("${informer.base-path}")
    String basePath;

    @Value("${informer.api-key}")
    String apiKey;

    @Value("${informer.security-key}")
    String securityKey;

    @Bean
    public RestClient informerRestclient(){
       return  RestClient.builder().baseUrl(basePath).defaultHeaders(
                        httpHeaders -> {
                            httpHeaders.set("Content-Type", "application/json");
                            httpHeaders.set("ApiKey", apiKey);
                            httpHeaders.set("SecurityCode", securityKey);
                        })
                .build();
    }

}
