package ai

import play.api.Configuration

final case class ClaudeSettings(apiKey: String, model: String, maxTokens: Long)

object ClaudeSettings {
  def fromConfig(config: Configuration): ClaudeSettings = {
    val c = config.get[Configuration]("ai.anthropic")
    val apiKey = c.getOptional[String]("api-key").filter(_.trim.nonEmpty).getOrElse(
      throw c.reportError("api-key", "Missing API key: Set the ANTHROPIC_API_KEY environment variable"))
    ClaudeSettings(apiKey = apiKey, model = c.get[String]("model"), maxTokens = c.get[Long]("max-tokens"))
  }
}
