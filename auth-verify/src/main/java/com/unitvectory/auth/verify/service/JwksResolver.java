package com.unitvectory.auth.verify.service;

import com.unitvectory.auth.verify.model.VerifyJwks;

public interface JwksResolver {

	VerifyJwks getJwks(String url);
}
