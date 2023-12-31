package com.unitvectory.auth.sign.service;

import java.util.List;

import com.unitvectory.auth.sign.model.JsonWebKey;

public interface SignService {

	String getActiveKid(long now);

	String sign(String kid, String unsignedToken, long now);

	List<JsonWebKey> getAll();
}
