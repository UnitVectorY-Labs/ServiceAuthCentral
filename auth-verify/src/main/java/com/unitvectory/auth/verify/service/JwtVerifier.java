package com.unitvectory.auth.verify.service;

import com.unitvectory.auth.verify.model.VerifyJwk;
import com.unitvectory.auth.verify.model.VerifyJwt;
import com.unitvectory.auth.verify.model.VerifyParameters;

public interface JwtVerifier {

	VerifyJwt extractClaims(String token);

	boolean verifySignature(String token, VerifyJwk jwk, VerifyParameters verifyParameters);
}
