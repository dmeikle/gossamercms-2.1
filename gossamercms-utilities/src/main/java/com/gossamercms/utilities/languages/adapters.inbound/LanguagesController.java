package com.gossamercms.utilities.languages.adapters.inbound;


import com.gossamercms.mvc.data.DtoWithId;
import com.gossamercms.utilities.languages.dtos.LanguageDto;
import com.gossamercms.utilities.languages.dtos.responses.LanguageResponse;
import com.gossamercms.utilities.languages.handlers.LanguagesHandler;
import com.gossamercms.utilities.languages.models.Language;
import com.gossamercms.mvc.controllers.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.gossamercms.utilities.languages.dtos.requests.LanguageRequest;
import com.gossamercms.utilities.languages.dtos.responses.LanguageResponse;

@RestController
@RequestMapping("/admin/languages")
public class LanguagesController extends BaseController<Language, LanguageDto, LanguageRequest, LanguageResponse, LanguageResponse> {

    public LanguagesController(LanguagesHandler handler) {
        super(handler, LanguageResponse.class);
    }

    @Override
    protected LanguageDto toDto(LanguageRequest request) {
        return LanguageDto.builder()
                .code(request.getCode())
                .name(request.getName())
                .build();
    }

    @Override
    protected LanguageResponse toResponse(LanguageDto dto) {
        return LanguageResponse.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .build();
    }


}
