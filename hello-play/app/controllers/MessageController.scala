package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.Json
import play.api.Routes
import org.scardf._
import org.joda.time.LocalDate


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
    print(rez)
    Ok(Json.toJson(Message("Ok!")))
  }
}