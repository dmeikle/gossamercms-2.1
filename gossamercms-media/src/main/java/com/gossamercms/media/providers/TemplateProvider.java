package com.gossamercms.media.providers;


import com.gossamercms.media.records.TemplateContent;
import com.gossamercms.media.records.TemplateReference;

public interface TemplateProvider {
    TemplateContent loadTemplate(TemplateReference reference);

    boolean templateExists(TemplateReference reference);
}
