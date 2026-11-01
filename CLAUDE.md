# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Purpose & working mode (read first)

This is a **learning repo**. Zakaria is learning Scala *and* how to call Claude from Scala via the official
**Java SDK (`com.anthropic:anthropic-java`)** inside a Play app.

- **Do not write or edit application code, build files or config.** Guide step by step: explain the next small step,
  show the snippet to type, explain the *why* (Scala idiom, SDK concept, Play concept), then let Zakaria implement it.
  Review what he wrote when asked. Only edit files when he explicitly asks you to.
- Keep steps small enough that `sbt compile` / `sbt test` stays green after each one.
- Prefer teaching the Scala ⇄ Java interop that the SDK forces on you (see below) over hiding it.

## Commands

```powershell
$env:ANTHROPIC_API_KEY='sk-ant-...'; sbt run   # POST http://localhost:9000/ask {"prompt": "..."}
sbt compile
sbt test                                         # no API key needed
sbt "testOnly ai.ClaudeSettingsSpec"             # single spec
sbt "testOnly ai.ClaudeSettingsSpec -- -z blank" # single test by name substring
```

If sbt fails with `Could not create lock for \\.\pipe\sbt-load...`, another sbt (usually Zakaria's `sbt run`) holds
the lock — don't kill it; ask.

CI (`.github/workflows/scala.yml`) runs `sbt test` on JDK 25 (Temurin).

## Stack & architecture

- Play 3.0 (`sbt-plugin` 3.0.12), sbt 1.12.15, Scala 2.13.18, ScalaTest via `scalatestplus-play`, scala-logging,
  cats-core.
- **DI is runtime Guice** (`javax.inject`). A controller needs only an `@Inject` constructor + a route.
- Flow: `conf/routes` → `AskController` (`Action.async(parse.json[AskRequest])`) → `ai.ClaudeClient` trait →
  `ai.AnthropicClaudeClient` (the only class touching the SDK client; bound in `ai.ClaudeModule`, registered via
  `play.modules.enabled`). Settings come from `ai.anthropic { api-key = ${?ANTHROPIC_API_KEY}, model, max-tokens }`
  through `ai.ClaudeSettings.fromConfig`, which throws `PlayException` on a missing/blank key.
- `play.http.errorHandler = JsonHttpErrorHandler` — unhandled errors are JSON. Claude API errors are recovered in the
  controller into `502`.
- Controller tests construct controllers directly with `stubControllerComponents()` and an anonymous-class
  `ClaudeClient` fake — they test behaviour, not routing. `AskControllerSpec` is still a TODO Zakaria owns.
- `README.md` doubles as the public tutorial; keep its roadmap checklist in sync as steps get done.

## Claude SDK in Scala — reference implementation

`C:\workspace\playground\cvnext\backend` (added as an extra working dir) already uses `anthropic-java` from Scala in
production. Use it as the worked example when teaching, especially:

- `build.sbt` — `"com.anthropic" % "anthropic-java" % "<version>"` (single `%`: it's a Java artifact, no Scala suffix),
  plus `dependencyOverrides` pinning **one Jackson version set and one Kotlin runtime**. The SDK is Kotlin-based and
  loads `jackson-module-kotlin` reflectively; Play also brings Jackson, so mismatched versions fail late and oddly.
  If adding the SDK here breaks at runtime with Jackson/Kotlin errors, this is why.
- `app/com/elzakaria/cvnext/buildcv/assist/ClaudeAssistProvider.scala` — `AnthropicOkHttpClientAsync.builder()`,
  `MessageCreateParams.builder()`, reading `message.content()` text blocks, `stopReason`, usage, error classification
  (`AnthropicServiceException`, `CompletionException` unwrapping), client lifecycle/`close()`.
- `test/.../assist/AnthropicClasspathSpec.scala` — guards the Jackson/Kotlin pin.

Interop patterns the SDK forces (teach these explicitly):
- Java builders, not case classes: `X.builder().field(v).build()`.
- `CompletableFuture` → Scala `Future`: `import scala.jdk.FutureConverters._` then `.asScala`.
- `java.util.Optional` → `Option`: `scala.jdk.OptionConverters._` / `.toScala`.
- `java.util.List` → Scala collections: `scala.jdk.CollectionConverters._` / `.asScala`.
- Primitive `Long` params (e.g. `maxTokens(1024L)`).
- The client owns threads/connection pools: create it once (Guice `@Singleton` or provider) and close it via
  `ApplicationLifecycle.addStopHook`.

Before stating SDK versions, model IDs, or method names, verify against current docs (the `claude-api` skill or
Context7) — don't answer from memory.
