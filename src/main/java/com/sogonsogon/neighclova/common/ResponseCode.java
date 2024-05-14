package com.sogonsogon.neighclova.common;

public interface ResponseCode {
    // HTTP Status 200
    String SUCCESS = "SU";

    // HTTP Status 400
    String NOT_EXISTED_USER = "NEU";
    String NOT_EXISTED_PLACE = "NEP";
    String DUPLICATE_EMAIL = "DE";

    // HTTP Status 401
    String VALIDATION_FAILED = "VF";
    String SIGN_IN_FAILED = "SF";
    String CERTIFICATE_FAIL = "CF";
    String AUTHORIZATION_FAIL = "AF";

    // HTTP Status 403
    String NO_PERMISSION = "NP";

    // HTTP Status 500
    String MAIL_FAIL = "MF";
    String DATABASE_ERROR = "DBE";
}
