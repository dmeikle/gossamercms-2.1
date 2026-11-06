package com.gossamercms.notifications.dtos;

import lombok.Builder;
import lombok.Data;


public interface EmailableInterface {

     public String getSubmitterName();
        public String getSubmitterEmail();
        public String getSubject();
}
