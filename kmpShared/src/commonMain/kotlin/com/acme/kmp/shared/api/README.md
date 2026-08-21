# ACME Shared API Components

- [AcmeApiClient](./AcmeApiClient.kt): Defines a shared API client that can be used by any module.
- Data Models: used to serialize/deserialize API payloads:
  - [AcmeData](./AcmeData.kt): Data Model for success response. 
  - [AcmeError](./AcmeError.kt): Data Model for error response. 
  - [ModuleInfo](./ModuleInfo.kt): Data Model providing module information.
- [AcmeApiResult](./AcmeApiResult.kt): Just an enum to choose the API response.