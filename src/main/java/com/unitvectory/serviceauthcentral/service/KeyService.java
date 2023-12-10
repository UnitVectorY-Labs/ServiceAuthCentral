package com.unitvectory.serviceauthcentral.service;

import java.util.List;

import com.unitvectory.serviceauthcentral.dto.JwksKey;

public interface KeyService {

	String getActiveKeyName();

	String signJwt(String keyName, String unsignedToken);

	List<JwksKey> getAllKeys();

}
