package com.gossamercms.geolocation.adapters.inbound;

import com.gossamercms.geolocation.providers.GeoapifyAddressCompletionProvider;
import com.gossamercms.geolocation.dtos.responses.AddressCompletionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("geo-locations")
@RequiredArgsConstructor
public class GeoLocationsController {

    private final GeoapifyAddressCompletionProvider geoapifyAddressCompletionProvider;

    @GetMapping("/address-completion")
    public AddressCompletionResponse addressCompletion(
            @RequestParam("query") String query
    ) {
        return geoapifyAddressCompletionProvider.complete(query);
    }
}
