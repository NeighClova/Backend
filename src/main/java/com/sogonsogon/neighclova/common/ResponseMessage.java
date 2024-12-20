package com.sogonsogon.neighclova.common;

public interface ResponseMessage {
    // HTTP Status 200
    String SUCCESS = "success";

    // HTTP Status 400
    String NOT_EXISTED_USER = "This user does not exist.";
    String NOT_EXISTED_PLACE = "This place does not exist.";
    String NOT_EXISTED_EMAIL = "This email does not exist.";
    String DUPLICATE_EMAIL = "duplicate email";
    String DUPLICATE_ID = "duplicate id";

    // HTTP Status 401
    String VALIDATION_FAILED = "validation failed";
    String SIGN_IN_FAILED = "Login information mismatch.";
    String CERTIFICATE_FAIL = "certification failed";
    String AUTHORIZATION_FAIL = "Authorization Failed.";
    String INVALID_GRANT = "The provided access grant is invalid, expired, or revoked";
    String EXPIRED_TOKEN = "This token is expired";

    // HTTP Status 403
    String NO_PERMISSION = "Do not have permission.";

    // HTTP Status 500
    String MAIL_FAIL = "mail send failed";
    String DATABASE_ERROR = "database error";
}
