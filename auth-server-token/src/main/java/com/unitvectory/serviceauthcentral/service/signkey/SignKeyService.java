package com.unitvectory.serviceauthcentral.service.signkey;

import java.util.List;

import com.unitvectory.serviceauthcentral.dto.JwksKey;

public interface SignKeyService {

	String getActiveKeyName();

	String signJwt(String keyName, String unsignedToken);

	List<JwksKey> getAllKeys();

}
