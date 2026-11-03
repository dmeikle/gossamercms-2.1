package com.gossamercms.utilities.languages.dtos.requests;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LanguageRequest {
    private String code;
    private String name;

}
