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