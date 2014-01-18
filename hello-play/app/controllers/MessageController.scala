package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.Json
import play.api.Routes

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

}