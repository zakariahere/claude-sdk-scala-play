package ai

import org.scalatestplus.play.PlaySpec
import play.api.{Configuration, PlayException}

class ClaudeSettingsSpec extends PlaySpec {

  "ClaudeSettings.fromConfig" should {
    "Read the anthropic block" in {
      val configuration = Configuration(
        "ai.anthropic.api-key" -> "test-key",
        "ai.anthropic.model" -> "claude-haiku-5-5",
        "ai.anthropic.max-tokens" -> 512
      )

      ClaudeSettings.fromConfig(configuration) mustBe ClaudeSettings("test-key", "claude-haiku-5-5", 512)
    }

    "Fail when the api key is missing" in {
      val configuration = Configuration(
        "ai.anthropic.model" -> "claude-haiku-5-5",
        "ai.anthropic.max-tokens" -> 512
      )

      val error : PlayException = the[PlayException]  thrownBy {
        ClaudeSettings.fromConfig(configuration)
      }

      error.getMessage must include("ANTHROPIC_API_KEY")
    }

    "Fail when the api key is blank" in {
      val configuration = Configuration(
        "ai.anthropic.api-key" -> "          ",
        "ai.anthropic.model" -> "claude-haiku-5-5",
        "ai.anthropic.max-tokens" -> 512
      )

      an[PlayException] must be thrownBy(ClaudeSettings.fromConfig(configuration))
    }
  }
}

