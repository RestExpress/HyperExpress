# HyperExpress Roadmap

This roadmap prioritizes completing and validating existing behavior before expanding the library’s scope. Check items off as they are completed.

## 1. Correctness and Format Completeness

- [ ] Define and test the expected behavior of `ReferenceDefinition.clone()`; implement it rather than returning `null` if cloning is supported.
- [ ] Investigate `DefaultUrlBuilder`’s `return null` path and add tests documenting the intended edge-case behavior.
- [ ] Complete Siren deserialization for classes, titles, and actions, or document any intentionally unsupported fields.
- [ ] Preserve JSON property value types during Siren deserialization instead of converting every value to text.
- [ ] Add tests for malformed and partial HAL and Siren documents, including nested entities and links.

## 2. RestExpress Integration Confidence

- [ ] Add tests under `restexpress/src/test` for media-type negotiation and response postprocessing.
- [ ] Test expansion callback registration and invocation through the plugin.
- [ ] Test request-header token binding for absent, malformed, and valid header values.
- [ ] Verify unsupported media types and resource-factory lookup failures have clear, tested behavior.
- [ ] Ensure integration tests run in the root Maven reactor.

## 3. Documentation and Examples

- [ ] Update the root README to describe HAL, Siren, and the RestExpress integration accurately.
- [ ] Reconcile module documentation with implemented serialization/deserialization and media-type support; state limitations explicitly.
- [ ] Refresh dependency coordinates, versions, repository instructions, and examples for current releases.
- [ ] Add a minimal end-to-end example for each supported representation: factory registration, resource creation, Jackson configuration, and round-trip use.
- [ ] Verify documentation snippets compile or clearly label pseudocode.

## 4. Build and Release Quality

- [ ] Add continuous integration that builds and tests all Maven modules on the supported JDK (currently Java 17).
- [ ] Add a coverage report and identify critical paths for token resolution, serialization, and integration tests.
- [ ] Define and document public API and dependency compatibility expectations.
- [ ] Review dependencies and Maven plugins regularly, and record supported Java and dependency versions.

## 5. Future Scope

- [ ] Collect user needs before adding another representation or framework integration.
- [ ] Prioritize interoperability and completeness of the existing formats over adding new formats prematurely.
