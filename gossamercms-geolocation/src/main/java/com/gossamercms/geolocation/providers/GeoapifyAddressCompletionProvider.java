package com.gossamercms.geolocation.providers;

import com.gossamercms.geolocation.dtos.AddressLocation;
import com.gossamercms.geolocation.dtos.AddressSuggestion;
import com.gossamercms.geolocation.dtos.responses.AddressCompletionResponse;
import com.gossamercms.mvc.annotations.ModuleProvider;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

@ModuleProvider
public class GeoapifyAddressCompletionProvider implements AddressCompletionProvider {
    public static final String BASE_URL = "https://api.geoapify.com/v1/geocode";

    public static final String AUTOCOMPLETE_URL = "/autocomplete";

    private final RestClient client;
    private final String apiKey;

    public GeoapifyAddressCompletionProvider() {
        this(System.getenv("GEOAPIFY_API_KEY"));
    }

    public GeoapifyAddressCompletionProvider(String apiKey) {
        this(RestClient.create(BASE_URL), apiKey);
    }

    GeoapifyAddressCompletionProvider(RestClient client, String apiKey) {
        Assert.hasText(apiKey, "Geoapify API key is required");
        this.client = client;
        this.apiKey = apiKey;
    }

    @Override
    public AddressCompletionResponse complete(String query) {
        Assert.hasText(query, "Address query is required");
        JsonNode response = client.get()
                .uri(builder -> builder.path(AUTOCOMPLETE_URL)
                        .queryParam("text", "{query}")
                        .queryParam("limit", 5)
                        .queryParam("format", "json")
                        .queryParam("apiKey", "{apiKey}")
                        .build(query, apiKey))
                .retrieve()
                .body(JsonNode.class);

        if (response == null || !response.path("results").isArray()) {
            throw new IllegalStateException("Geoapify response must contain a results array");
        }

        List<AddressSuggestion> suggestions = new ArrayList<>();
        for (JsonNode result : response.path("results")) {
            suggestions.add(AddressSuggestion.builder()
                    .formatted(text(result, "formatted"))
                    .addressLine1(text(result, "address_line1"))
                    .addressLine2(text(result, "address_line2"))
                    .houseNumber(text(result, "housenumber"))
                    .street(text(result, "street"))
                    .neighbourhood(text(result, "neighbourhood"))
                    .district(text(result, "district"))
                    .city(text(result, "city"))
                    .county(text(result, "county"))
                    .state(text(result, "state"))
                    .stateCode(text(result, "state_code"))
                    .postcode(text(result, "postcode"))
                    .country(text(result, "country"))
                    .countryCode(text(result, "country_code"))
                    .location(AddressLocation.builder()
                            .latitude(coordinate(result, "lat"))
                            .longitude(coordinate(result, "lon"))
                            .build())
                    .build());
        }
        return () -> suggestions;
    }

    private static String text(JsonNode result, String field) {
        return result.path(field).asString(null);
    }

    private static Double coordinate(JsonNode result, String field) {
        JsonNode value = result.path(field);
        return value.isNumber() ? value.doubleValue() : null;
    }
}
