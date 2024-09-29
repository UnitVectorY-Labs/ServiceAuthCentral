/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.unitvectory.serviceauthcentral.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * The InputPatterns test.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public class InputPatternsTest {

    @Test
    void testClientIdPattern() {
        assertTrue("https://example.com".matches(InputPatterns.CLIENT_ID));
        assertTrue("https://example.com/foo".matches(InputPatterns.CLIENT_ID));
        assertTrue("valid-pattern".matches(InputPatterns.CLIENT_ID));
        assertTrue("valid.pattern".matches(InputPatterns.CLIENT_ID));
        assertTrue("valid:pattern".matches(InputPatterns.CLIENT_ID));
        assertTrue("bS4G4gPTpnt4uzCjG0zkCkwuTKgTZ7cbg6e5FAxz5YBNc0BOEoOUjxbCdrwaq6odKhfypDxHvikC7gGW2WRp8rrnmLfVhMjOYqWm3Byc83fvMkm8I5ukzVCVC5Fr9zDHkekApgsKydk5Gn2hc8ShtW7OFpf5oYbNPEBboi389yratAJ46l38TQUhbG3wQMvTvrpxsYcybNPhG7cgvRNrdHdsD5gEst2jg8avzpPcy7Igoqz18ELIk5Dmk67k9oB".matches(InputPatterns.CLIENT_ID));
        assertTrue("https://Tpnt4uzCjG0zkCkwuTKgTZ7cbg6e5FAxz5YBNc0BOEoOUjxbCdrwaq6odKhfypDxHvikC7gGW2WRp8rrnmLfVhMjOYqWm3Byc83fvMkm8I5ukzVCVC5Fr9zDHkekApgsKdk5Gn2hc8ShtW7OFpf5oYbNPEBboi389yratAJ46l38TQUhbG3wQMvTvrpxsYcybNPhG7cgvRNrdHdsD5gEst2jg8avzpPcy7Igoqz18ELIk5Dmk67.com".matches(InputPatterns.CLIENT_ID));
        assertTrue("//Tpnt4uzCjG0zkCkwuTKgTZ7cbg6e5FAxz5YBNc0BOEoOUjxbCdrwaq6odKhfypDxHvikC7gGW2WRp8rrnmLfVhMjOYqWm3Byc83fvMkm8I5ukzVCVC5Fr9zDHkekApgsKdk5Gn2hc8ShtW7OFpf5oYbNPEBboi389yratAJ46l38TQUhbG3wQMvTvrpxsYcybNPhG7cgvRNrdHdsD5gEst2jg8avzpPcy7Igoqz18ELIk5Dmk67.com".matches(InputPatterns.CLIENT_ID));
        
        
        assertFalse("https:://invalid-url".matches(InputPatterns.CLIENT_ID));
        assertFalse("https:/invalid-url".matches(InputPatterns.CLIENT_ID));
        assertFalse("http://example.com".matches(InputPatterns.CLIENT_ID));
        assertFalse("bS4G4gPTpnt4uzCjG0zkCkwuTKgTZ7cbg6e5FAxz5YBNc0BOEoOUjxbCdrwaq6odKhfypDxHvikC7gGW2WRp8rrnmLfVhMjOYqWm3Byc83fvMkm8I5ukzVCVC5Fr9zDHkekApgsKydk5Gn2hc8ShtW7OFpf5oYbNPEBboi389yratAJ46l38TQUhbG3wQMvTvrpxsYcybNPhG7cgvRNrdHdsD5gEst2jg8avzpPcy7Igoqz18ELIk5Dmk67k9oBX".matches(InputPatterns.CLIENT_ID));
        assertFalse("https://Tpnt4uzCjG0zkCkwuTKgTZ7cbg6e5FAxz5YBNc0BOEoOUjxbCdrwaq6odKhfypDxHvikC7gGW2WRp8rrnmLfVhMjOYqWm3Byc83fvMkm8I5ukzVCVC5Fr9zDHkekApgsKydk5Gn2hc8ShtW7OFpf5oYbNPEBboi389yratAJ46l38TQUhbG3wQMvTvrpxsYcybNPhG7cgvRNrdHdsD5gEst2jg8avzpPcy7Igoqz18ELIk5Dm67BX.com".matches(InputPatterns.CLIENT_ID));
    }

    @Test
    void testClientSecretPattern() {
        assertTrue("aBc123Def456".matches(InputPatterns.CLIENT_SECRET));
        assertFalse("short".matches(InputPatterns.CLIENT_SECRET));
    }

    @Test
    void testJWTPattern() {
        assertTrue("header.payload.signature".matches(InputPatterns.JWT));
        assertFalse("invalidjwt".matches(InputPatterns.JWT));
    }

    @Test
    void testAuthCodePattern() {
        assertTrue("abc123XYZ789".matches(InputPatterns.AUTH_CODE));
        assertFalse("short".matches(InputPatterns.AUTH_CODE));
    }

    @Test
    void testPkceCodeVerifierPattern() {
        assertTrue("1234567890Ab".matches(InputPatterns.PKCE_CODE_VERIFIER));
        assertFalse("tooShort".matches(InputPatterns.PKCE_CODE_VERIFIER));
    }

    @Test
    void testDescriptionPattern() {
        assertTrue("Valid description 123".matches(InputPatterns.DESCRIPTION));
        assertFalse(
                "This description is way too long and should fail the test because it has more than 200 characters, which is the maximum allowed by the regex pattern."
                        .matches(InputPatterns.DESCRIPTION));
    }

    @Test
    void testScopePattern() {
        assertTrue("valid_scope".matches(InputPatterns.SCOPE));
        assertFalse(
                "This scope is invalid because it is way too long and exceeds the 100 characters limit."
                        .matches(InputPatterns.SCOPE));
    }

    @Test
    void testScopesPattern() {
        assertTrue("scope1 scope2 scope3".matches(InputPatterns.SCOPES));
        assertFalse("scope1 ^s".matches(InputPatterns.SCOPES));
    }

    @Test
    void testV4UUIDPattern() {
        assertTrue("562febdd-960b-4375-99bd-1bb3da54edb2".matches(InputPatterns.V4UUID));
        assertFalse("invalid-uuid".matches(InputPatterns.V4UUID));
    }

    @Test
    void testExternalJwksUrlPattern() {
        assertTrue("https://example.com/.well-known/jwks.json"
                .matches(InputPatterns.EXTERNAL_JWKS_URL));
        assertFalse("http://example.com/.well-known/jwks.json"
                .matches(InputPatterns.EXTERNAL_JWKS_URL));
    }

    @Test
    void testExternalClaimPattern() {
        assertTrue("Valid external claim".matches(InputPatterns.EXTERNAL_CLAIM));
        // Generating a 501 character string to test failure case
        String longString = new String(new char[501]).replace("\0", "a");
        assertFalse(longString.matches(InputPatterns.EXTERNAL_CLAIM));
    }
}
