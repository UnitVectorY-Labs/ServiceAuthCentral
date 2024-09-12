# GCP Workload Identity

One of the benefits of using Google Cloud Platform (GCP) is the ability to use [Workload Identity Federation](https://cloud.google.com/iam/docs/workload-identity-federation) to securely access Google Cloud services with external credentials through an OIDC-compliant token service.  This allows you to use the ServiceAuthCentral token service to request access tokens for Google Cloud services using a Google Service Account.

With ServiceAuthCentral acting as an authorization server requiring each audience to be authorized, including the audience for the GCP Workload Identity Pool.

## Configuring GCP Workload Identity

The primary prerequisite for using GCP Workload Identity is to have a Google Cloud project with the Workload Identity Pool and Provider configured.  This can be done through the Google Cloud Console or gcloud, but for the example here it is assumed that [serviceauthcentral-workload-identity-gcp-tofu](https://github.com/UnitVectorY-Labs/serviceauthcentral-workload-identity-gcp-tofu) was used to configure things specifically for ServiceAuthCentral.

The configuration of the Workload Identity is based on the issuer of ServiceAuthCentral which Google will use to validate the tokens.

The key output of this step will be the principal for the Workload Identity Pool, which will include the following variables:

- `PROJECT_NUMBER`
- `POOL_NAME`
- `POOL_PROVIDER`

The key output here is what will be used as the audience within ServiceAuthCentral.

`//iam.googleapis.com/projects/{PROJECT_NUMBER}/locations/global/workloadIdentityPools/{POOL_NAME}/providers/{POOL_PROVIDER}`

## ServiceAuthCentral Client for GCP Workload Identity

With ServiceAuthCentral being used as an authorization server the client that is wanting to access GCP services will need to be authorized to access the Workload Identity Pool both within ServiceAuthCentral and within GCP.

The audience inside of ServiceAuthCentral can be automatically configured as part of the bootstrap process with [serviceauthcentral-firestore-bootstrap-gcp-tofu](https://github.com/UnitVectorY-Labs/serviceauthcentral-firestore-bootstrap-gcp-tofu). This creates a client with the audience of: `//iam.googleapis.com/projects/{PROJECT_NUMBER}/locations/global/workloadIdentityPools/{POOL_NAME}/providers/{POOL_PROVIDER}` This is the same audience that is used to request the token from the GCP Workload Identity Pool which is an intentional design decision.

Clients can then be authorized in ServiceAuthCentral to access this client which will allow for JWTs to be vended with the correct audience.  The `SAC_CLIENT_ID` is needed to request the token.  The example below is using a client_secret:

```bash
curl --request POST \
  --url 'https://issuer.example.com/v1/token' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data 'grant_type=client_credentials' \
  --data 'client_id={SAC_CLIENT_ID}' \
  --data 'client_secret={SAC_CLIENT_SECRET}' \
  --data 'audience=//iam.googleapis.com/projects/{PROJECT_NUMBER}/locations/global/workloadIdentityPools/{POOL_NAME}/providers/{POOL_PROVIDER}'
```

However, the principal behind ServiceAuthCentral is avoiding shared secrets therefore the `urn:ietf:params:oauth:grant-type:jwt-bearer` flow would be more ideal.

```bash
curl --request POST \
  --url 'https://issuer.example.com/v1/token' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data 'grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer' \
  --data 'client_id={SAC_CLIENT_ID}' \
  --data 'assertion={SAC_AUTHORIZED_JWT}' \
  --data 'audience=//iam.googleapis.com/projects/{PROJECT_NUMBER}/locations/global/workloadIdentityPools/{POOL_NAME}/providers/{POOL_PROVIDER}'
```

This will return the token that can be used to access the GCP Workload Identity Pool.

```json
{
  "access_token" : "{SERVICEAUTHCENTRAL_JWT}",
  "token_type" : "Bearer",
  "expires_in" : 3600
}
```

## Requesting GCP Access Token

To request a token, make a `POST` request to `https://sts.googleapis.com/v1/token` with the following parameters in the `application/x-www-form-urlencoded` format to request:

```bash
curl --request POST \
  --url 'https://sts.googleapis.com/v1/token' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data 'grant_type=urn:ietf:params:oauth:grant-type:token-exchange' \
  --data 'requested_token_type=urn:ietf:params:oauth:token-type:access_token' \
  --data 'scope=https://www.googleapis.com/auth/cloud-platform' \
  --data 'audience=//iam.googleapis.com/projects/{PROJECT_NUMBER}/locations/global/workloadIdentityPools/{POOL_NAME}/providers/{POOL_PROVIDER}' \
  --data 'subject_token_type=urn:ietf:params:oauth:token-type:jwt' \
  --data 'subject_token={SERVICEAUTHCENTRAL_JWT}' \
```

```json
{
  "access_token": "{GCP_WIF_ACCESS_TOKEN}",
  "issued_token_type": "urn:ietf:params:oauth:token-type:access_token",
  "token_type": "Bearer",
  "expires_in": 3599
}
```

This token can then be used to access GCP services that are authorized for the correct principal. Most Service Accounts are authorized with email addresses, but the principal for the Workload Identity Pool is in the format of:

`principal://iam.googleapis.com/projects/{PROJECT_NUMBER}/locations/global/workloadIdentityPools/{POOL_NAME}/subject/{SAC_CLIENT_ID}`

The key here is that the ServiceAuthCentral client id which was authorized is used to identify the principal allowing access to the GCP services to be properly scoped to a specific client.

Granting access to GCP services to this principal directly is generally speaking the preferred method, but it is also possible to impersonate a service account as explained in the next section.

## Impersonating a Service Account

To impersinate a GCP Service account some additional setup is required.  A service account must be created and then the `roles/iam.workloadIdentityUser` / "Workload Identity User" role must be granted to the same principal referenced before of `principal://iam.googleapis.com/projects/{PROJECT_NUMBER}/locations/global/workloadIdentityPools/{POOL_NAME}/subject/{SAC_CLIENT_ID}` which will allow the principal to impersonate the service account.

The service account must also be granted the necessary roles to access the GCP services that are needed.

Then the previous access token can be used to impersonate the service account:

```bash
curl -X POST \
  -H "Authorization: Bearer {GCP_WIF_ACCESS_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{"scope": ["https://www.googleapis.com/auth/cloud-platform"],"lifetime": "3600s"}' \
  "https://iamcredentials.googleapis.com/v1/projects/-/serviceAccounts/{SERVICE_ACCOUNT_EMAIL}:generateAccessToken"
```

This returns the access token for the service account which can then be used to access GCP services:

```json
{
  "accessToken": "{SERVICE_ACCOUNT_ACCESS_TOKEN}",
  "expireTime": "2XXX-XX-XXTXX:XX:XXZ"
}
```

Alternatively if an identity token for the GCP service account is needed it can be requested with a specific audience:

```bash
curl -X POST \
  -H "Authorization: Bearer {GCP_WIF_ACCESS_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{ "audience": "https://myaudience.example.com", "includeEmail": true}' \
  "https://iamcredentials.googleapis.com/v1/projects/-/serviceAccounts/{SERVICE_ACCOUNT_EMAIL}:generateIdToken"
```

This returns the identity token for the service account:

```json
{
  "token": "{SERVICE_ACCOUNT_ID_TOKEN}"
}
```
