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
package com.unitvectory.auth.server.token.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.unitvectory.auth.common.service.entropy.EntropyService;
import com.unitvectory.auth.common.service.entropy.SystemEntropyService;
import com.unitvectory.auth.common.service.time.SystemTimeService;
import com.unitvectory.auth.common.service.time.TimeService;

/**
 * The configuration
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Configuration
@Profile("!test")
public class ServiceAuthCentralConfig {

	@Bean
	public TimeService timeService() {
		return new SystemTimeService();
	}

	@Bean
	public EntropyService entropyService() {
		return new SystemEntropyService();
	}
}
