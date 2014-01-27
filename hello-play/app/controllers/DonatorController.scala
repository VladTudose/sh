package controllers

import play.api.mvc.{Action, Controller}
import dispatch._, Defaults._
import play.api.libs.concurrent.Execution.Implicits._

object DonatorController extends Controller {
    
  //TODO baga ontologii
  //TODO baga @en
  //TODO parseaza JSON
  
  def post = Action { request =>
      val body = request.body.asFormUrlEncoded
      println("#################DONE BODY " + body)
      val firstName = body.get("firstname")(0)
      val lastName = body.get("lastname")(0)
      val email = body.get("email")(0)
      val id = java.util.UUID.randomUUID.toString
      val ttl = "@prefix : <http://socialhelper.com/donators/> .\n" +
                ":" + id + "\n" +
                ":email \"" + email + "\" ;\n" +
                ":firstName \"" + firstName + "\" ;\n" +
                ":lastName \"" + lastName + "\" ."
      
      val req = url("http://localhost:3030/ds/data?default").POST.setBody(ttl).addHeader("Content-type", "text/turtle")
      val rez = Http(req OK as.String)
      Ok("{id: " + id + "}")
  }
  
  def getAll = Action {
      val query = "SELECT ?s ?p ?o WHERE { ?s ?p ?o .\nFILTER(STRSTARTS(STR(?s), \"http://socialhelper.com/donators/\")) .\n}"
      val req = url("http://localhost:3030/ds/query?default") <<? Map("query" -> query)
      val rez = Http(req OK as.String)
      Ok(rez.apply())
  }
  
  def get(id: String) = Action {
      print("!!!!!!!!!!!!!!!!!!!!!###### " + id)
      val query = "SELECT ?s ?p ?o WHERE { ?s ?p ?o .\nFILTER(STRSTARTS(STR(?s), \"http://socialhelper.com/donators/" + id + "\")) .\n}"
      val req = url("http://localhost:3030/ds/query?default") <<? Map("query" -> query)
      val rez = Http(req OK as.String)
      Ok(rez.apply())
  }
  
  def delete(id: String) = Action {
      val del = "DELETE {<http://socialhelper.com/donators/" + id + "> ?p ?o} WHERE {<http://socialhelper.com/donators/" + id + "> ?p ?o}" 
      println("!!!!!!!!!!!!!!!!!!!!! " + id)
      println(del)
      val req = url("http://localhost:3030/ds/update").POST << Map("update" -> del)
      val rez = Http(req OK as.String)
      Ok(rez.apply())
  }
  
  def put(id: String) = Action { request =>
      println("#################PUT " + id)
      val del = "DELETE {<http://socialhelper.com/donators/" + id + "> ?p ?o} WHERE {<http://socialhelper.com/donators/" + id + "> ?p ?o}" 
      val req = url("http://localhost:3030/ds/update").POST << Map("update" -> del)
      val rez1 = Http(req OK as.String)
      println("#################DONE DELETE " + rez1.apply())
      val body = request.body.asFormUrlEncoded
      println(request.headers)
      println("#################DONE BODY " + body)
      val firstName = body.get("firstname")(0)
      println("#################DONE fistname")
      val lastName = body.get("lastname")(0)
      println("#################DONE lastname")
      val email = body.get("email")(0)
      println("#################DONE email")
      
      val ttl = "@prefix : <http://socialhelper.com/donators/> .\n" +
                ":" + id + "\n" +
                ":email \"" + email + "\" ;\n" +
                ":firstName \"" + firstName + "\" ;\n" +
                ":lastName \"" + lastName + "\" ."
      
      val req2 = url("http://localhost:3030/ds/data?default").POST.setBody(ttl).addHeader("Content-type", "text/turtle")
      val rez = Http(req2 OK as.String)
      Ok(rez.apply())
  }
}