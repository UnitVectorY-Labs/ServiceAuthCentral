package com.unitvectory.serviceauthcentral.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwksResponse {

	private List<JwksKey> keys;
}
