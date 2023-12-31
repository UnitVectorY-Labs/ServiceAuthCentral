package com.unitvectory.auth.datamodel.gcp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

import lombok.Getter;

@Configuration
@Profile("datamodel-gcp")
public class DatamodelGcpFirestoreConfig {

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
