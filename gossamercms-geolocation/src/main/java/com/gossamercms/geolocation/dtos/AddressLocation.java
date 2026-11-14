package com.gossamercms.geolocation.dtos;


import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class AddressLocation {

    private Double latitude;
    private Double longitude;

}