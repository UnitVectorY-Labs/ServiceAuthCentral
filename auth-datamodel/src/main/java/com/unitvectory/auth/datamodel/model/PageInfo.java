package com.unitvectory.auth.datamodel.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PageInfo {

	private final boolean hasNextPage;

	private final boolean hasPreviousPage;

	private final String startCursor;

	private final String endCursor;
}
