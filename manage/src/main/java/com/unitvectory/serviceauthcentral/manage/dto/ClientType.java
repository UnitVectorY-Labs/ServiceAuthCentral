package com.unitvectory.serviceauthcentral.manage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientType {

	private String clientId;

	private boolean clientSecret1Set;

	private boolean clientSecret2Set;

}
