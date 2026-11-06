package com.gossamercms.notifications.handlers;

import com.gossamercms.notifications.data.TemplatesDbService;
import com.gossamercms.notifications.dtos.TemplateDto;
import com.gossamercms.notifications.models.Template;
import com.gossamercms.mvc.annotations.ModuleHandler;
import com.gossamercms.mvc.handlers.BaseHandler;

@ModuleHandler
public class TemplatesHandler extends BaseHandler<Template, TemplateDto> {

    public TemplatesHandler(TemplatesDbService db) {
        super(db);
    }
}
