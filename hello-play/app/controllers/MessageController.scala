package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.Json
import play.api.Routes
import org.scardf._
import org.joda.time.LocalDate
import dispatch._, Defaults._
import play.api.libs.concurrent.Execution.Implicits._


case class Message(value: String)

object MessageController extends Controller {

  implicit val fooWrites = Json.writes[Message]

  def getMessage = Action {
    Ok(Json.toJson(Message("Hello from Scala")))
  }
  
  def getBla(p1: Option[String]) = Action {
      p1 match {
          case None => NotFound("Page not found fasdf asdf asdfas dfas")
          case Some(bla) => Ok(Json.toJson(Message("bla bla bla fadsklfjasdlkfj " + p1.get)))
      }
  }

  def javascriptRoutes = Action { implicit request =>
    Ok(Routes.javascriptRouter("jsRoutes")(routes.javascript.MessageController.getMessage)).as(JAVASCRIPT)
  }

  def postFuseki = Action {
    val dc = Vocabulary( "http://purl.org/dc/elements/1.1/#" )
    val homepage = UriRef( "http://www.example.org/index.html" )
    val g = Graph( homepage -(  dc\"date" -> new LocalDate( 1999, 8, 16 ), dc\"language" -> "en",
    dc\"creator" -> UriRef( "http://example.org/staffid/85740" )) )
    val rez = g.rend
    println(rez)
    val req = url("http://192.168.1.118:3030/ds/data?default").POST.setBody(rez).addHeader("Content-type", "text/turtle")
    Http(req OK as.String)
    Ok(Json.toJson(Message("OK")))
  }
}