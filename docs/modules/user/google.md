# User - Google

The user Google module provides the implementation for the administrative console to authenticate users using Google OAuth 2.0.

## Configuration

| Property                              | Required | Description                                                                                            |
| ------------------------------------- | -------- | ------------------------------------------------------------------------------------------------------ |
| sac.user.provider.google.clientid     | Yes      | The clientId for the Google application                                                                |
| sac.user.provider.google.clientsecret | Yes      | The clientSecret for the Google application                                                            |
| sac.token.url                         | Yes      | The base URL for the token server which will end with "/login/callback" needed for Google's OAuth flow |
