package com.gossamercms.users.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.gossamercms.mvc.data.DtoWithId;
import com.gossamercms.users.domain.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class AddressDto implements DtoWithId {

    private UUID id;

    private UUID userId;

    private String type;

    private String address1;

    private String address2;

    private String city;

    private String stateProvince;

    private String postalCode;

    private String countryCode;

   // private Instant createdAt;

    @JsonProperty("isDefault")
    @JsonSetter(nulls = Nulls.SKIP)
    private boolean isDefault = false;

    @JsonSetter(nulls = Nulls.SKIP)
    private boolean isBilling = false;


    public Address toEntity() {
        return Address.builder()
                .id(id)
                .userId(userId)
                .type(type)
                .address1(address1)
                .address2(address2)
                .city(city)
                .stateProvince(stateProvince)
                .postalCode(postalCode)
                .countryCode(countryCode)
                .isDefault(isDefault)
                .isBilling(isBilling)
                .build();
    }
}