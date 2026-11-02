package controllers

import ai.ClaudeClient
import org.scalatestplus.play.PlaySpec
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.{ExecutionContext, Future}

class AskControllerSpec extends PlaySpec {

  private final class RecordingClaude extends ClaudeClient {
    var prompts: Vector[String] = Vector.empty

    override def ask(prompt: String): Future[String] = {
      prompts = prompts :+ prompt
      Future.successful(s"echo: $prompt")
    }
  }

  private def controllerWith(claude: ClaudeClient): AskController = new AskController(stubControllerComponents(), claude)(ExecutionContext.global)

  "AskController#ask" should {
    "return Claude's answer as json" in {
      val claude = new RecordingClaude
      val result = controllerWith(claude).ask().apply(FakeRequest(POST, "/ask").withBody(AskRequest("hi")))
      status(result) mustBe 200
      (contentAsJson(result) \ "answer").as[String] mustBe "echo: hi"
      claude.prompts mustBe Vector("hi")
    }

    "return 400 without calling Claude when the prompt is blank" in {
      val claude = new RecordingClaude
      val result = controllerWith(claude).ask().apply(FakeRequest(POST, "/ask").withBody(AskRequest("          ")))
      status(result) mustBe 400
      (contentAsJson(result) \ "error").isDefined mustBe true
      claude.prompts mustBe empty
    }
  }
}