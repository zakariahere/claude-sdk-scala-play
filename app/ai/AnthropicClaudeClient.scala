package ai

import com.anthropic.client.AnthropicClientAsync
import com.anthropic.client.okhttp.AnthropicOkHttpClientAsync
import com.anthropic.models.messages.{Message, MessageCreateParams}
import com.google.inject.Singleton
import com.typesafe.scalalogging.LazyLogging
import play.api.Configuration
import play.api.inject.ApplicationLifecycle
import javax.inject._

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.jdk.FutureConverters.CompletionStageOps
import scala.jdk.OptionConverters.RichOptional

@Singleton
class AnthropicClaudeClient @Inject()(config: Configuration, lifecycle: ApplicationLifecycle)(implicit ec: ExecutionContext)
  extends ClaudeClient with LazyLogging {

  private val settings: ClaudeSettings = ClaudeSettings.fromConfig(config)
  private val client: AnthropicClientAsync = AnthropicOkHttpClientAsync.builder().apiKey(settings.apiKey).build()

  lifecycle.addStopHook(() => Future(client.close()))

  logger.info("[AI] ___Built Anthropic claude client.___")

  override def ask(prompt: String): Future[String] = {
    val params = MessageCreateParams.builder().model(settings.model).maxTokens(settings.maxTokens).addUserMessage(prompt).build()
    client.messages().create(params).asScala.map(textOf)
  }

  private def textOf(message: Message): String ={
    message.content().asScala.flatMap(block => block.text().toScala)
      .map(_.text())
      .mkString
  }
}
