package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.ws._
import play.api.Play.current

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait Application { this: Controller =>

  var state = Map(
    "code" -> "a collection of laws or rules",
    "emotion" -> "a feeling of any kind")

   // TODO: web service invocation
  def ws(word: String): Future[Option[String]] = Future {
    val wsState = Map(
      "programming" -> "the process of writing computer programs",
      "subroutine" -> "a set of instructions designed to perform a frequently used operation within a program")
    wsState.get(word)
  }

  class WordRequest[A](
    val word: String, 
    val definition: String, 
    request: Request[A]) extends WrappedRequest[A](request)

  object WordRequest {
    def apply[A](word: String, definition: String, request: Request[A]) =
      new WordRequest(word, definition, request)
  }

  def WordTransducer(word: String) = new ActionRefiner[Request, WordRequest] {
    def refine[A](request: Request[A]) = for {
      owr <- Future(state.get(word).map(WordRequest(word, _, request)))
      owr2 <- owr match {
	case None => ws(word).map(_.map(WordRequest(word, _, request)))
	case _ => Future.successful(owr)
      }
    } yield owr2.toRight(NotFound(s"could not find '$word'"))
  }

  implicit class WordsOp(s: String) {
    def words = s.split("[ \n]+")
  }

  def naughtyList: Future[List[String]] = {
    val holder: WSRequestHolder =
      WS.url(s"http://localhost:9000/assets/bad_words.txt")
    holder.get map (_.body.words.toList)
  }

  def ParentalFilter = new ActionFilter[WordRequest] {
    def filter[A](wrequest: WordRequest[A]) = naughtyList map { list =>
      list
        .find(wrequest.definition.words contains _)
        .map(bad => Forbidden(s"parental filter blocked a request"))
    }
  }

  def UpperTransformer = new ActionTransformer[WordRequest, WordRequest] {
    def transform[A](wrequest: WordRequest[A]) = Future {
      // FIXME: change this notation
      WordRequest(wrequest.word, wrequest.definition.toUpperCase, wrequest)
    }
  }

  def search(word: String) = 
    (Action 
     andThen WordTransducer(word) 
     andThen ParentalFilter
     andThen UpperTransformer) { wrequest =>
    Ok(wrequest.definition)
  }

  def addResult(word: String, definition: String) =
    if (state.isDefinedAt(word))
      Forbidden(s"the word '$word' does already exist")
    else {
      // FIXME: non-atomic operation => concurrency issues
      state = state + (word -> definition)
      Ok
    }

  def add(word: String) = Action(parse.text) { request =>
    addResult(word, request.body)
  }

  val jsWordBodyParser: BodyParser[(String, String)] = parse.json map { jsv => 
    ((jsv \ "word").as[String] -> ((jsv \ "definition").as[String]))
  }

  def addPost = Action(jsWordBodyParser) { request =>
    val (word, definition) = request.body
    addResult(word, definition)
  }

}

object Application extends Controller with Application
