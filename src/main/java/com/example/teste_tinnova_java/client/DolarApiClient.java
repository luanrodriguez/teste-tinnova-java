package com.example.teste_tinnova_java.client;

import com.example.teste_tinnova_java.dto.EconomiaDolarApiClientResponseDTO;
import com.example.teste_tinnova_java.dto.FrankfurterDolarApiClientDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Component
public class DolarApiClient {
    private final RestClient restClient;
    private final StringRedisTemplate redisTemplate;

    public DolarApiClient(StringRedisTemplate redisTemplate) {
        this.restClient = RestClient.create();
        this.redisTemplate = redisTemplate;
    }

    public Double convertBrlToUsd(Double brl) {
        String key = "brl_usd";
        String dolarValue = this.redisTemplate.opsForValue().get(key);

        if(dolarValue == null) {
            try {
                dolarValue = economiaGetDolarValue();
            } catch(Exception ex){
                dolarValue = frankfurterGetDolarValue();
            }
            redisTemplate.opsForValue().set(key, dolarValue, Duration.ofMinutes(10));
        }

        return brl / Double.parseDouble(dolarValue);
    }

    private String economiaGetDolarValue() {
        EconomiaDolarApiClientResponseDTO response = restClient.get()
                .uri("https://economia.awesomeapi.com.br/json/last/USD-BRL")
                .retrieve()
                .body(EconomiaDolarApiClientResponseDTO.class);

        return response.USDBRL().bid();
    }

    private String frankfurterGetDolarValue() {
        FrankfurterDolarApiClientDTO response = restClient.get()
                .uri("https://api.frankfurter.app/latest?from=USD&to=BRL")
                .retrieve()
                .body(FrankfurterDolarApiClientDTO.class);

        return response.rates().BRL().toString();
    }
}
