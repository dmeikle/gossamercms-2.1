package com.gossamercms.geolocation.dtos.responses;


import com.gossamercms.geolocation.dtos.AddressSuggestion;

import java.util.List;

public interface AddressCompletionResponse {

    List<AddressSuggestion> getResults();

}