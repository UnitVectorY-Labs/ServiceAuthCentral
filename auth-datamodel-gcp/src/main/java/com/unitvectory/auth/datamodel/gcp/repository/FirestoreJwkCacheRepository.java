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
package com.unitvectory.auth.datamodel.gcp.repository;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nonnull;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.common.hash.Hashing;
import com.unitvectory.auth.datamodel.gcp.mapper.CachedJwkRecordMapper;
import com.unitvectory.auth.datamodel.gcp.model.CachedJwkRecord;
import com.unitvectory.auth.datamodel.model.CachedJwk;
import com.unitvectory.auth.datamodel.repository.JwkCacheRepository;
import com.unitvectory.auth.util.exception.InternalServerErrorException;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * The Firestore JWK Cache Repository
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@SuppressWarnings("null")
@AllArgsConstructor
public class FirestoreJwkCacheRepository implements JwkCacheRepository {

	private static final String URL = "url";

	private Firestore firestore;

	private String collectionKeys;

	private String getId(@NonNull String url, @NonNull String id) {
		String urlHash = Hashing.sha256().hashString(url, StandardCharsets.UTF_8).toString();
		String idHash = Hashing.sha256().hashString(id, StandardCharsets.UTF_8).toString();
		return Hashing.sha256().hashString(urlHash + idHash, StandardCharsets.UTF_8).toString();
	}

	@Override
	public void cacheJwk(@NonNull String url, @NonNull CachedJwk jwk, long ttl) {
		CachedJwkRecord cachedJwk =
				CachedJwkRecordMapper.INSTANCE.cachedJwkToCachedJwkRecord(url, ttl, jwk);
		String documentId = getId(url, jwk.getKid());
		cachedJwk.setDocumentId(documentId);

		try {
			firestore.collection(this.collectionKeys).document(documentId).set(cachedJwk).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public void cacheJwkAbsent(@NonNull String url, @NonNull String kid, long ttl) {
		@Nonnull
		String documentId = getId(url, kid);

		CachedJwkRecord cachedJwk = CachedJwkRecord.builder().documentId(documentId).url(url)
				.kid(kid).ttl(Timestamp.ofTimeSecondsAndNanos(ttl, 0)).build();

		try {
			firestore.collection(this.collectionKeys).document(documentId).set(cachedJwk).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public List<CachedJwk> getJwks(@NonNull String url) {
		try {
			QuerySnapshot query =
					firestore.collection(this.collectionKeys).whereEqualTo(URL, url).get().get();

			List<CachedJwk> list = new ArrayList<>();
			for (DocumentSnapshot document : query.getDocuments()) {
				CachedJwkRecord cachedJwk = document.toObject(CachedJwkRecord.class);
				list.add(cachedJwk);
			}

			return Collections.unmodifiableList(list);
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public CachedJwk getJwk(String url, String kid) {
		String id = getId(url, kid);
		try {
			DocumentSnapshot document =
					firestore.collection(this.collectionKeys).document(id).get().get();
			if (document.exists()) {
				CachedJwkRecord cachedJwk = document.toObject(CachedJwkRecord.class);
				return cachedJwk;
			} else {
				return null;
			}
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

}
