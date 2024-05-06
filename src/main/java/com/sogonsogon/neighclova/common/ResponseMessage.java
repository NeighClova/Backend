package com.sogonsogon.neighclova.common;

public interface ResponseMessage {
    String SUCCESS = "success";

    String VALIDATION_FAILED = "validation failed";
    String DUPLICATE_EMAIL = "duplicate email";

    String SIGN_IN_FAILED = "Login information mismatch.";
    String CERTIFICATE_FAIL = "certification failed";

    String MAIL_FAIL = "mail send failed";
    String DATABASE_ERROR = "database error";
}
