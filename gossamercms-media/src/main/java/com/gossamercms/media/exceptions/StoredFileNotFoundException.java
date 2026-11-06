package com.gossamercms.media.exceptions;

import com.gossamercms.media.config.ExceptionCodes;
import com.gossamercms.mvc.exceptions.ApiException;
import org.springframework.http.HttpStatus;

public class StoredFileNotFoundException extends ApiException {
    public StoredFileNotFoundException(String message) {
        super(ExceptionCodes.STORED_FILE_NOT_FOUND,"No template found for: " + message, HttpStatus.NOT_FOUND.value());
    }
}
