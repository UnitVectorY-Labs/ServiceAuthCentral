package com.unitvectory.auth.server.token.service.signkey;

import java.util.List;

import com.unitvectory.auth.server.token.dto.JwksKey;

public interface SignKeyService {

	String getActiveKeyName();

	String signJwt(String keyName, String unsignedToken);

	List<JwksKey> getAllKeys();

}
