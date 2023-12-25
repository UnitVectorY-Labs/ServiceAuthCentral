package com.unitvectory.serviceauthcentral.manage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

import lombok.Getter;

@Configuration
@Profile("!test")
public class GcpConfig {

	@Getter
	@Value("${google.cloud.project}")
	private String projectId;

	@Bean
	public Firestore firestore() {
		FirestoreOptions firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder()
				.setProjectId(this.getProjectId()).build();
		return firestoreOptions.getService();
	}
}
