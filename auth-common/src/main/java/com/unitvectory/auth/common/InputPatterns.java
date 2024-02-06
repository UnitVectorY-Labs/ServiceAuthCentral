package com.unitvectory.auth.common;

public class InputPatterns {

        public static final String CLIENT_ID = "^(https:\\/\\/)?[a-zA-Z0-9-_:\\.]{1,255}$";

        public static final String CLIENT_SECRET = "^[0-9a-zA-Z]{12,64}$";

        public static final String JWT = "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+$";

        public static final String AUTH_CODE = "^[0-9a-zA-Z]{12,64}$";

        public static final String PKCE_CODE_VERIFIER = "^[0-9a-zA-Z]{10,200}$";

        public static final String DESCRIPTION = "^[a-zA-Z0-9-_:\\. ]{1,200}$";

        public static final String SCOPE = "^[a-zA-Z0-9-_:]{1,100}$";

        public static final String SCOPES = "^([a-zA-Z0-9-_:]{1,100})( [a-zA-Z0-9-_:]{1,100})*$";

        public static final String V4UUID =
                        "^[a-f0-9]{8}-?[a-f0-9]{4}-?4[a-f0-9]{3}-?[89ab][a-f0-9]{3}-?[a-f0-9]{12}$";

        public static final String EXTERNAL_JWKS_URL =
                        "^https?:\\/\\/(?:www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_\\+.~#?&\\/=]*)$";

        public static final String EXTERNAL_CLAIM = "^.{1,500}$";

}
