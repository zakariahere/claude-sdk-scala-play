package ai

import com.google.inject.AbstractModule

/**
 * Module GUICE
 */
class ClaudeModule extends AbstractModule {
  override def configure(): Unit = bind(classOf[ClaudeClient]).to(classOf[AnthropicClaudeClient])
}
