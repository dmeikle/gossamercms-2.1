## Runtime compatibility

The shared modules target Java 21, Spring Boot 4.1.2, and Jackson 3.
Consumers should align their Spring Boot BOM to the same version and use
`tools.jackson.databind` types for mapper injection and JSON DTO fields.
Java time support is built into Jackson 3; no separate JavaTimeModule is needed.
The database JSON helpers retain their Jackson 2 serialization defaults,
including ISO-8601 dates in JSONB.

After changing shared modules, rebuild and install them before restarting
consuming applications such as Engagement or Quantum:

```shell
mvn clean install
```

## Generating migrations for Quantum

With `gossamercms-2.1`, `engagement`, and `quantum` in sibling directories,
run `sh ./run-migrations.sh --dry-run` from Quantum to build the projects
and preview new create-table migrations. Without `--dry-run`, the script
writes migrations into each model's owning module, rebuilds the dependency
JARs, and applies Flyway migrations using Quantum's `pom.xml` connection
settings. Maven `-Dflyway.*` overrides can be supplied through `MAVEN_OPTS`.
The command does not start the application.

The generator runs on Quantum's dependency classpath. Existing create-table
migrations are preserved and skipped, with new versions allocated globally
across both projects. Models without a table mapping are reported and skipped.
This is a create-table scaffold, not a schema-diff tool:
write changes to existing tables as explicit versioned migrations. Review
dry-run SQL before applying it, including constraints and column types.

## Typed controllers

`TypedBaseController<EntityType, DtoType, RequestType, ResponseType>` provides
protected operations without HTTP mappings or parameter-binding annotations.
Concrete controllers choose which operations to expose through public annotated
methods, and implement `toDto(RequestType)` and `toResponse(DtoType)`.
Handlers continue to use `DtoType`; list and bulk responses map each item to
`ResponseType` while retaining pagination metadata.

For example, a concrete controller extending
`TypedBaseController<Product, ProductDto, ProductRequest, ProductResponse>` can
expose only creation:

```java
@PostMapping
public ApiResponse<ProductResponse> createProduct(
        @CurrentUser CurrentJwtUser user,
        @RequestBody ProductRequest request
) {
    return super.create(user, request);
}
```

Declare mappings, binding annotations, pagination defaults, and any explicit
response status on the concrete endpoint. ID-based operations accept a preloaded
`DtoType`, so concrete endpoints can continue using DTO path-variable converters.
Existing `BaseController` subclasses are unchanged.

Converters extending `BaseConverter` resolve UUID strings by ID and other strings
by the `slug` column. Slug lookup requires `slug` in the model's column metadata.
A missing UUID does not fall back to slug lookup. Missing records produce a 404;
other lookup errors propagate rather than being converted to a 404.

## Testing 
First, run the server. You can either run the test-server in the root api:
```aiignore
./run-server.sh
```
that will run the development database to check values against.
or you can run the test server:
```aiignore
./run-test-server.sh
```
Once you have an api server running, from the root module (gossamercms-2.0) run this following command.
Specify which individual module you want to run tests on:
```aiignore
./run-tests.sh users  
```
or auth|users|mvc||firebase|media|notifications|rbac