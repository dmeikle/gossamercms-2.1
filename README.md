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

## Message recipients

The emails module registers `DefaultMessageRecipientResolver` through
`@ModuleService`. It uses the `postgres` datasource to resolve `USER` audiences
by user ID and `EVENT_SUBSCRIBERS` through
`subscribers -> SubscriptionKeys -> templates`, matching the request's event key.
Subscribers reach users through `account_mappings -> user_contexts`; deleted
subscriptions, deleted keys/templates, and expired account mappings are excluded.
Apply the emails schema migrations before using subscriber audiences.

Audience filters match scalar values in user-context metadata using parameterized
equality predicates. Direct users use their default context (or the earliest
context when none is default); subscriber audiences use the subscribed context.
Unknown users and unmatched subscriptions produce no recipients. Invalid
audiences and database errors fail explicitly.

Recipients are deduplicated by user ID in audience order. An active primary email
is preferred, falling back to another active email. Phone resolution requires a
verified, SMS-opted-in number and prefers the preferred number. Locale comes from
context metadata `locale`, then `language`; absent contact details remain null.
This resolver does not replace the separate `MessagePreferencesService` contract.

Run the resolver tests with:

```shell
mvn -pl gossamercms-emails -am -Dtest=DefaultMessageRecipientResolverTest,MessageRecipientResolverPostgresTest -Dsurefire.failIfNoSpecifiedTests=false test
```

The PostgreSQL test is enabled only with `RECIPIENT_TEST_JDBC_URL` pointing to an
empty disposable PostgreSQL database using user `postgres` and password
`recipient_test`. It creates fixture tables and must not target an application database.

## Typed controllers

`TypedBaseController<EntityType, DtoType, RequestType, ResponseType, ResponseListType>` provides
protected operations without HTTP mappings or parameter-binding annotations.
Concrete controllers choose which operations to expose through public annotated
methods, and implement `toDto(RequestType)` and `toResponse(DtoType)`.
Supply `ResponseListType.class` alongside the handler to the base constructor.
Handlers continue to use `DtoType`; list operations delegate to
`handler.getAll(options, responseListClass)` without applying `toResponse`.
Bulk responses map each item to `ResponseType`. Both retain pagination metadata.
The `_getAll` overload accepting a result DTO class overrides the configured list
class for that call.

For example, a concrete controller extending
`TypedBaseController<Product, ProductDto, ProductRequest, ProductResponse, ProductSummary>` can
expose only creation:

```java
@PostMapping
public ApiResponse<ProductResponse> createProduct(
        @CurrentUser CurrentJwtUser user,
        @RequestBody ProductRequest request
) {
    return ApiResponse.ok(super._create(user, request));
}
```

Declare mappings, binding annotations, pagination defaults, and any explicit
response status on the concrete endpoint. ID-based operations accept a preloaded
`DtoType`, so concrete endpoints can continue using DTO path-variable converters.
`BaseController<EntityType, DtoType, RequestType, ResponseType, ResponseListType>`
uses `ResponseListType` for the inherited `getAll` endpoint. Supply its class
to the base constructor so the database service can construct list results:

```java
public class ProductsController
        extends BaseController<Product, ProductDto, ProductRequest, ProductResponse, ProductSummary> {
    public ProductsController(ProductsHandler handler) {
        super(handler, ProductSummary.class);
    }

    // Implement toDto and toResponse for requests and single-item responses.
}
```

`getAll` forwards the list DTO class through the handler to `BaseDbService`,
which maps database rows directly into that class. It bypasses `toResponse` and
preserves pagination metadata; the DTO must support the database service's
no-argument construction and field mapping. Single-item and bulk operations
continue to use `ResponseType` through `toResponse`. Existing subclasses must
add the fifth type parameter and pass its class to `super`; use `ResponseType`
for both type parameters when the list and single-item shapes are the same.

Converters extending `BaseConverter` resolve UUID strings by ID and other strings
by the `slug` column. Slug lookup requires `slug` in the model's column metadata.
A missing UUID does not fall back to slug lookup. Missing records produce a 404;
other lookup errors propagate rather than being converted to a 404.

## Address autocomplete

The `geolocation` module provides `GeoapifyAddressCompletionProvider`.
Set `GEOAPIFY_API_KEY` before using its no-argument constructor, or pass the
API key to `new GeoapifyAddressCompletionProvider(apiKey)`.
`complete(query)` sends a URL-encoded query to Geoapify's autocomplete endpoint,
requests up to five JSON results, and maps them to address suggestions.
HTTP and invalid-response errors propagate to the caller.

## Email template lookup

`MessagesService.loadTemplate(eventKey, locale)` returns a `LoadedTemplate`
containing the template selected by `eventKey` and its `template_messages`
record selected by `templatesId` and the exact requested `locale`. Missing
templates or translations raise a 404; there is no locale fallback. This step
does not render, persist, or send a message.

The `Template` model uses the module's PostgreSQL `templates` table and audit
column convention (`createdAt`, `updatedAt`, `deletedAt`), rather than the
legacy MySQL `notifications.Templates` names (`createdOn`, etc.). Its metadata
is available to the migration generator described above.

## Notification template files

`NotificationsAutoConfig` imports `MessageTemplateConfiguration`, and the
notification renderer explicitly selects `messageTemplateEngine`. Its
`StringTemplateResolver` processes loaded content rather than treating it as
a resource name, independently of the consuming application's template engine.

Notification `subject`, `textBody`, `htmlBody`, `sms`, `pushTitle`, and `pushBody`
values are storage keys, not inline message text. `NotificationTemplateService`
loads their UTF-8 files through the `MediaStorageProvider` bean configured by
the consuming project. Local storage resolves keys such as `articles/sms.txt`
under `media.storage.path`; remote/S3 support belongs to the configured provider.
The notification renderer does not select or fall back between providers.

Loaded files are rendered with Thymeleaf using the recipient's locale and
message parameters (TEXT mode for text fields, HTML mode for `htmlBody`).
Use Thymeleaf text inlining such as `New article: [(${articleTitle})]` for
plain-text messages without HTML entity escaping.
Null or blank keys mean the corresponding content is absent; failure to load
a specified file propagates instead of sending its path as message content.

`templateUrl` is an optional storage key for the HTML email layout. The renderer
first renders the `htmlBody` file, then exposes that result to the layout as
`htmlBody`. For example, the layout can contain:

```html
<html xmlns:th="http://www.thymeleaf.org">
  <body><main th:utext="${htmlBody}"></main></body>
</html>
```

Only trusted template HTML should be inserted with `th:utext`; escape untrusted
parameters in the content template using `th:text`. Without `templateUrl`, the
rendered HTML body is used directly. A layout can also render a complete email
without an `htmlBody` file.

## Notification preferences

`MessagePreferencesService.getEnabledChannels(userContextId, subscriptionKey)`
returns distinct enabled `MessageChannel` values from non-deleted preferences,
reading every result page. No matching preferences means no enabled channels;
invalid stored channel names and database failures propagate.

`USER` message audiences supply `userContextId`. The recipient resolver carries
that context into the notification workflow, which loads the localized template
before looking up preferences using its `subscriptionKey`. Recipients are
deduplicated by context so different contexts retain their own preferences.

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