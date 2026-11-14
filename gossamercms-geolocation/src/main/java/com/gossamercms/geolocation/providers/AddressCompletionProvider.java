package com.gossamercms.geolocation.providers;

import com.gossamercms.geolocation.dtos.responses.AddressCompletionResponse;

public interface AddressCompletionProvider {

    AddressCompletionResponse complete(String query);
}
