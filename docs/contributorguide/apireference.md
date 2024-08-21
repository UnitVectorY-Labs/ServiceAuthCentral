---
layout: default
title: API Reference
parent: Contributor Guide
nav_order: 3
---

# API Reference

The following is an API reference for the ServiceAuthCentral manage service and the user authentication used by serviceauthcentralweb for the administrative portal.  These APIs would not be used by microservices integrating with ServiceAuthCentral and represent the management plane of the system.

> {: .important }
> For the API reference on the token server and how vend and validate tokens, see the [Integration Guide - API Reference]({{ site.baseurl }}{% link integrationguide/apireference.md %}) page.

## GET /login/authorize

The GET `/login/authorize` endpoint initiates the OAuth 2.0 authorization process using the PKCE (Proof Key for Code Exchange) flow from the serviceauthcentralweb application and then proceeds a flow for the 3rd party application to authenticate the user.

**Request Parameters:**

- `response_type` (String): Must be set to code. This indicates that an authorization code is being requested.
- `client_id` (String): The client identifier issued to the client during the registration process.
- `redirect_uri` (String): The URI to which the response will be sent. This must match one of the redirect URIs registered for the client.
- `code_challenge` (String): A challenge derived from the code verifier by using one of the supported transformation methods.
- `code_challenge_method` (String): Must be set to S256. This indicates the method used to derive the code challenge.
- `state` (String): An opaque value used to maintain state between the request and the callback. This value should be used to prevent cross-site request forgery (CSRF) attacks.

**Response:**

On success, the endpoint will redirect the user to the 3rd party application for user authentication (such as Google or GitHub) using an OAuth 2.0 auth code flow.

## GET /login/callback

The `GET /login/callback` endpoint handles the callback from the third-party authentication provider after the user has authenticated. This endpoint completes the OAuth 2.0 authorization process by exchanging the authorization code for user information and generating an authorization code for the client application.

**Request Parameters:**

sessionId (Cookie): The session identifier stored in a secure, HTTP-only cookie.
code (String): The authorization code received from the third-party authentication provider.
state (String): The state parameter received from the third-party authentication provider. This should match the state parameter sent in the initial authorization request.

**Response:**

On success, the endpoint will redirect the user back to serviceauthcentralweb completing the original PKCE flow which allows the user to then request a token from the token server that can be used to authenticate to the GraphQL management API.

## POST /graphql

The `POST /graphql` endpoint is the primary endpoint for interacting with the ServiceAuthCentral management API. This endpoint is a GraphQL endpoint that allows for querying and mutating data in the ServiceAuthCentral database.

<!-- START graphql-markdown -->


## Query
<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>clients</strong></td>
<td valign="top"><a href="#clientsummaryconnection">ClientSummaryConnection</a></td>
<td>

Get a list of clients.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">first</td>
<td valign="top"><a href="#int">Int</a></td>
<td>

The number of items to return starting after the cursor specified by 'after'. Used for forward pagination.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">after</td>
<td valign="top"><a href="#string">String</a></td>
<td>

The cursor after which to return the items. Used for forward pagination.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">last</td>
<td valign="top"><a href="#int">Int</a></td>
<td>

The number of items to return ending before the cursor specified by 'before'. Used for backward pagination.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">before</td>
<td valign="top"><a href="#string">String</a></td>
<td>

The cursor before which to return the items. Used for backward pagination.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>client</strong></td>
<td valign="top"><a href="#client">Client</a></td>
<td>

Get a client by the clientId.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">clientId</td>
<td valign="top"><a href="#id">ID</a>!</td>
<td>

The clientId of the client to retrieve.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>authorization</strong></td>
<td valign="top"><a href="#authorization">Authorization</a></td>
<td>

Get an authorization by the id.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">id</td>
<td valign="top"><a href="#id">ID</a>!</td>
<td>

The id of the authorization to retrieve.

</td>
</tr>
</tbody>
</table>

## Mutation
<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>addClient</strong></td>
<td valign="top"><a href="#client">Client</a>!</td>
<td>

Add a new client.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">clientId</td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The clientId of the new client. Must be globally unique.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">description</td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The description of the client.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">availableScopes</td>
<td valign="top">[<a href="#clientscopeinput">ClientScopeInput</a>]</td>
<td>

The list of scopes that this client is aware of and are available to be assigned to an authorized client.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>addClientAvailableScope</strong></td>
<td valign="top"><a href="#response">Response</a>!</td>
<td>

Add an additional available scope to an existing client that can be assigned to an authorized client.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">clientId</td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The clientId of the client to add the available scope to.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">availableScope</td>
<td valign="top"><a href="#clientscopeinput">ClientScopeInput</a>!</td>
<td>

The available scope to add to the client.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>deleteClient</strong></td>
<td valign="top"><a href="#response">Response</a>!</td>
<td>

Delete a client.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">clientId</td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The clientId of the client to delete.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>generateClientSecret1</strong></td>
<td valign="top"><a href="#clientsecret">ClientSecret</a>!</td>
<td>

Generates a new client secret for a client. This is one of two methods to generate a new client secret, differentiated by their names.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">clientId</td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The clientId of the client to generate a new secret for.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>generateClientSecret2</strong></td>
<td valign="top"><a href="#clientsecret">ClientSecret</a>!</td>
<td>

Generates a new client secret for a client. This is one of two methods to generate a new client secret, differentiated by their names.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">clientId</td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The clientId of the client to generate a new secret for.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>clearClientSecret1</strong></td>
<td valign="top"><a href="#clientsecret">ClientSecret</a>!</td>
<td>

Clears the client secret for a client. This is one of two methods to clear the client secret, differentiated by their names.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">clientId</td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The clientId of the client to clear the secret for.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>clearClientSecret2</strong></td>
<td valign="top"><a href="#clientsecret">ClientSecret</a>!</td>
<td>

Clears the client secret for a client. This is one of two methods to clear the client secret, differentiated by their names.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">clientId</td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The clientId of the client to clear the secret for.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>authorizeJwtBearer</strong></td>
<td valign="top"><a href="#response">Response</a>!</td>
<td>

Add a jwt-bearer configuration to a client to use as an authorized user for the client.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">clientId</td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The clientId of the client to authorize for use with jwt-bearer.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">jwksUrl</td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The jwksUrl of the JWT bearer used to validate the JWT.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">iss</td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The `iss` (issuer) claim that must be present in the JWT.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">sub</td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The `sub` (subject) claim that must be present in the JWT.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">aud</td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The `aud` (audience) claim that must be present in the JWT.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>deauthorizeJwtBearer</strong></td>
<td valign="top"><a href="#response">Response</a>!</td>
<td>

Deauthorize a jwt-bearer configuration from a client.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">clientId</td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The clientId of the client to deauthorize for use with jwt-bearer.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">id</td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The id of the JWT bearer configuration to deauthorize.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>authorize</strong></td>
<td valign="top"><a href="#response">Response</a>!</td>
<td>

Authorize a client to access another client with the authorization.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">subject</td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The clientId of the service that will be granted access to the specified audience.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">audience</td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The clientId of the service that is being accessed by the specified subject.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">authorizedScopes</td>
<td valign="top">[<a href="#string">String</a>]</td>
<td>

The scopes that the subject is authorized to access on the audience. The scopes must be a subset of the audience's available scopes.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>deauthorize</strong></td>
<td valign="top"><a href="#response">Response</a>!</td>
<td>

Deauthorize a client from accessing another client.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">subject</td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The clientId of the subject that will be deauthorized from the specified audience.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">audience</td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The clientId of the audience that the subject will be deauthorized from.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>authorizeAddScope</strong></td>
<td valign="top"><a href="#response">Response</a>!</td>
<td>

Add an authorized scope to an existing authorization.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">subject</td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The clientId of the subject that will be authorized to access the scope.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">audience</td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The clientId of the audience that the subject will be authorized to access the scope on. The scope must be a subset of the audience's available scopes.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">authorizedScope</td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The scope that the subject will be authorized to access on the audience.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>authorizeRemoveScope</strong></td>
<td valign="top"><a href="#response">Response</a>!</td>
<td>

Remove an authorized scope from an existing authorization.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">subject</td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The clientId of the subject that will be deauthorized from accessing the scope.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">audience</td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The clientId of the audience that the subject will be deauthorized from accessing the scope on.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">authorizedScope</td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The scope that the subject will be deauthorized from accessing on the audience.

</td>
</tr>
</tbody>
</table>

## Objects

### Authorization

The authorization that allows a client to access another client.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>id</strong></td>
<td valign="top"><a href="#id">ID</a>!</td>
<td>

The id of the authorization.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>authorizationCreated</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The date the authorization was created.
Returned in ISO-8601 format (yyyy-MM-dd’T’HH:mm:ssZ). The ‘Z’ indicates UTC, which is always used.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>subject</strong></td>
<td valign="top"><a href="#client">Client</a></td>
<td>

The clientId of the subject that is authorized to access the audience.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>audience</strong></td>
<td valign="top"><a href="#client">Client</a></td>
<td>

The clientId of the audience that the subject is authorized to access.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>authorizedScopes</strong></td>
<td valign="top">[<a href="#string">String</a>]</td>
<td>

The scopes that the subject is authorized to access on the audience.

</td>
</tr>
</tbody>
</table>

### Client

The full representation of a client.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>clientId</strong></td>
<td valign="top"><a href="#id">ID</a>!</td>
<td>

The clientId of the client.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>clientCreated</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The date the client was created.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>description</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The description of the client.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>clientType</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The type of client.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>clientSecret1Set</strong></td>
<td valign="top"><a href="#boolean">Boolean</a></td>
<td>

The indicator that the first client secret has been set for the client.
The client secret is used to authenticate the client to the authorization server but is stored as a hashed value and cannot be retrieved after it is set.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>clientSecret1Updated</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The time the first client secret was modified.
Returned in ISO-8601 format (yyyy-MM-dd'T'HH:mm:ssZ). The 'Z' indicates UTC, which is always used.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>clientSecret2Set</strong></td>
<td valign="top"><a href="#boolean">Boolean</a></td>
<td>

The indicator that the second client secret has been set for the client.
The client secret is used to authenticate the client to the authorization server but is stored as a hashed value and cannot be retrieved after it is set.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>clientSecret2Updated</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The time the second client secret was modified.
Returned in ISO-8601 format (yyyy-MM-dd'T'HH:mm:ssZ). The 'Z' indicates UTC, which is always used.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>authorizationsAsSubject</strong></td>
<td valign="top">[<a href="#authorization">Authorization</a>]</td>
<td>

The list of authorizations where the client is the subject.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>authorizationsAsAudience</strong></td>
<td valign="top">[<a href="#authorization">Authorization</a>]</td>
<td>

The list of authorizations where the client is the audience.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>availableScopes</strong></td>
<td valign="top">[<a href="#clientscope">ClientScope</a>]</td>
<td>

The list of available scopes that can be assigned to an authorized client.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>jwtBearer</strong></td>
<td valign="top">[<a href="#jwtbearer">JwtBearer</a>]</td>
<td>

The list of jwt-bearer configurations that can be used to authorize the client.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>managementPermissions</strong></td>
<td valign="top"><a href="#clientmanagementcapabilities">ClientManagementCapabilities</a></td>
<td>

The client management permissions for the client. These permissions are used to determine what actions can be taken on the client

</td>
</tr>
</tbody>
</table>

### ClientManagementCapabilities

The client management permissions for a client.
These permissions are used to determine what actions can be taken on the client.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>canDeleteClient</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>

Indicates if the client can be deleted.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>canAddClientSecret</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>

Indicates if the client secret can be added.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>canDeleteClientSecret</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>

Indicates if the client secret can be deleted.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>canAddClientAuthorization</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>

Indicates a jwt-bearer configuration can be added to the client.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>canDeleteClientAuthorization</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>

Indicates a jwt-bearer configuration can be removed from the client.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>canAddAvailableScope</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>

Indicates an available scope can be added to the client.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>canAddAuthorization</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>

Indicates a client can be authorized to access this client.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>canDeleteAuthorization</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>

Indicates a client can be deauthorized from accessing this client.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>canAuthorizeAddScope</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>

Indicates a scope can be added.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>canAuthorizeRemoveScope</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>

Indicates a scope can be removed.

</td>
</tr>
</tbody>
</table>

### ClientScope

The scope that can be assigned to an authorized client.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>scope</strong></td>
<td valign="top"><a href="#id">ID</a>!</td>
<td>

The value for the scope.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>description</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The description of the scope.

</td>
</tr>
</tbody>
</table>

### ClientSecret

The client secret.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>clientSecret</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The generated client secret. This value is only returned once and cannot be retrieved again.

</td>
</tr>
</tbody>
</table>

### ClientSummary

The abbreviated representation of a client.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>clientId</strong></td>
<td valign="top"><a href="#id">ID</a>!</td>
<td>

The clientId of the client.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>description</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The description of the client.

</td>
</tr>
</tbody>
</table>

### ClientSummaryConnection

A connection object for paginated ClientSummary results.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>edges</strong></td>
<td valign="top">[<a href="#clientsummaryedge">ClientSummaryEdge</a>]</td>
<td>

The list of edges, each containing a ClientSummary.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>pageInfo</strong></td>
<td valign="top"><a href="#pageinfo">PageInfo</a>!</td>
<td>

The page information used for pagination.

</td>
</tr>
</tbody>
</table>

### ClientSummaryEdge

An edge in a paginated list of ClientSummary objects.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>node</strong></td>
<td valign="top"><a href="#clientsummary">ClientSummary</a></td>
<td>

The ClientSummary node.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>cursor</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The cursor for use in pagination.

</td>
</tr>
</tbody>
</table>

### JwtBearer

The jwt-bearer configuration that can be used.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>id</strong></td>
<td valign="top"><a href="#id">ID</a>!</td>
<td>

The id of the jwt-bearer configuration.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>jwksUrl</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The jwksUrl of the JWT bearer used to validate the JWT.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>iss</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The iss (issuer) claim that must be present in the JWT.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>sub</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The sub (subject) claim that must be present in the JWT.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>aud</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The aud (audience) claim that must be present in the JWT.

</td>
</tr>
</tbody>
</table>

### PageInfo

Information about pagination in a connection.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>hasNextPage</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>

Indicates if there are more pages to fetch after the current one.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>hasPreviousPage</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>

Indicates if there are more pages to fetch before the current one.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>startCursor</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The cursor corresponding to the first node in the current page of results.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>endCursor</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The cursor corresponding to the last node in the current page of results.

</td>
</tr>
</tbody>
</table>

### Response

The response object for mutations.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>success</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>

The flag indicating if the operation was successful.

</td>
</tr>
</tbody>
</table>

## Inputs

### ClientScopeInput

The input object for adding a new available scope to a client.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>scope</strong></td>
<td valign="top"><a href="#id">ID</a>!</td>
<td>

The value for the scope.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>description</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The description of the scope.

</td>
</tr>
</tbody>
</table>

## Scalars

### Boolean

The `Boolean` scalar type represents `true` or `false`.

### ID

The `ID` scalar type represents a unique identifier, often used to refetch an object or as key for a cache. The ID type appears in a JSON response as a String; however, it is not intended to be human-readable. When expected as an input type, any string (such as `"4"`) or integer (such as `4`) input value will be accepted as an ID.

### Int

The `Int` scalar type represents non-fractional signed whole numeric values. Int can represent values between -(2^31) and 2^31 - 1.

### String

The `String` scalar type represents textual data, represented as UTF-8 character sequences. The String type is most often used by GraphQL to represent free-form human-readable text.


<!-- END graphql-markdown -->
