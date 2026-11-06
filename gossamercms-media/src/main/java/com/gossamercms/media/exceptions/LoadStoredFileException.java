package com.gossamercms.media.exceptions;

import com.gossamercms.media.config.ExceptionCodes;
import com.gossamercms.mvc.exceptions.ApiException;

public class LoadStoredFileException extends ApiException {
    public LoadStoredFileException(String message) {
        super(ExceptionCodes.STORED_FILE_LOAD_EXCEPTION,"Unable to load file: " + message, 404);
    }

}
