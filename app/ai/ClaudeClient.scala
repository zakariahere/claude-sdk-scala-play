package ai

import scala.concurrent.Future

trait ClaudeClient {
  def ask(prompt: String) :  Future[String]
}
