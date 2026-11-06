package com.gossamercms.notifications.exceptions;

import com.gossamercms.mvc.exceptions.ApiException;
import com.gossamercms.notifications.config.ExceptionCodes;
import org.springframework.http.HttpStatus;

public class LoadTemplateException extends ApiException {
    public LoadTemplateException(String message) {
        super(ExceptionCodes.TEMPLATE_LOAD_EXCEPTION, "Unable to load template: " + message, HttpStatus.FAILED_DEPENDENCY.value());
    }

    public LoadTemplateException(String message, Throwable cause) {
        this(message);
        initCause(cause);
    }
}
