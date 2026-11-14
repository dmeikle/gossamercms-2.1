package com.gossamercms.geolocation.dtos;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class AddressSuggestion {

    private String formatted;

    private String addressLine1;
    private String addressLine2;

    private String houseNumber;
    private String street;

    private String neighbourhood;
    private String district;
    private String city;
    private String county;

    private String state;
    private String stateCode;

    private String postcode;

    private String country;
    private String countryCode;

    private AddressLocation location;

}