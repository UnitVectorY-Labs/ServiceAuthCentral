package com.unitvectory.auth.datamodel.gcp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

@Configuration
@Profile("datamodel-gcp")
public class DatamodelGcpFirestoreConfig {

	@Value("${google.cloud.project}")
	private String projectId;

	@Bean
	public Firestore firestore() {
		FirestoreOptions firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder()
				.setProjectId(this.projectId).build();
		return firestoreOptions.getService();
	}
}
