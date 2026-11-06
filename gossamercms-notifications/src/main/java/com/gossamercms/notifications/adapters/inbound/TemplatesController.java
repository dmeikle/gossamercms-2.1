package com.gossamercms.notifications.adapters.inbound;


import com.gossamercms.notifications.dtos.TemplateDto;
import com.gossamercms.notifications.handlers.TemplatesHandler;
import com.gossamercms.notifications.models.Template;
import com.gossamercms.mvc.controllers.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/admin/Templates")
public class TemplatesController extends BaseController<Template, TemplateDto, TemplateDto, TemplateDto, TemplateDto> {

    public TemplatesController(TemplatesHandler handler) {
        super(handler, TemplateDto.class);
    }

    @Override
    protected TemplateDto toDto(TemplateDto request) {
        return null;
    }

    @Override
    protected TemplateDto toResponse(TemplateDto dto) {
        return null;
    }
}
