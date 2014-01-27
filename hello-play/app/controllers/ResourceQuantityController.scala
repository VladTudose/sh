package controllers

import play.api.mvc.{Action, Controller}
import dispatch._, Defaults._
import play.api.libs.concurrent.Execution.Implicits._

object ResourceQuantityController extends Controller {
    
  //TODO baga ontologii
  //TODO baga @en
  //TODO parseaza JSON
  
  def post = Action { request =>
      val body = request.body.asFormUrlEncoded
      println("#################DONE BODY " + body)
      val quantity = body.get("quantity")(0)
      val resource = body.get("resource")(0)
      val id = java.util.UUID.randomUUID.toString
      val ttl = "@prefix : <http://socialhelper.com/resourcequantities#> .\n" +
                ":" + id + "\n" +
                ":quantity "  + quantity + " ;\n" +
                ":resource <http://socialhelper.com/resources#" + resource + "> ."
      
      val req = url("http://192.168.1.118:3030/ds/data?default").POST.setBody(ttl).addHeader("Content-type", "text/turtle")
      val rez = Http(req OK as.String)
      Ok("{id: " + id + "}")
  }
  
  def getAll = Action {
      val query = "SELECT ?s ?p ?o WHERE { ?s ?p ?o .\nFILTER(STRSTARTS(STR(?s), \"http://socialhelper.com/resourcequantities#\")) .\n}"
      val req = url("http://192.168.1.118:3030/ds/query?default") <<? Map("query" -> query)
      val rez = Http(req OK as.String)
      Ok(rez.apply())
  }
  
  def get(id: String) = Action {
      print("!!!!!!!!!!!!!!!!!!!!!###### " + id)
      val query = "SELECT ?s ?p ?o WHERE { ?s ?p ?o .\nFILTER(STRSTARTS(STR(?s), \"http://socialhelper.com/resourcequantities#" + id + "\")) .\n}"
      val req = url("http://192.168.1.118:3030/ds/query?default") <<? Map("query" -> query)
      val rez = Http(req OK as.String)
      Ok(rez.apply())
  }
  
  def delete(id: String) = Action {
      val del = "DELETE {<http://socialhelper.com/resourcequantities#" + id + "> ?p ?o} WHERE {<http://socialhelper.com/resourcequantities#" + id + "> ?p ?o}" 
      println("!!!!!!!!!!!!!!!!!!!!! " + id)
      println(del)
      val req = url("http://192.168.1.118:3030/ds/update").POST << Map("update" -> del)
      val rez = Http(req OK as.String)
      Ok(rez.apply())
  }
  
  def put(id: String) = Action { request =>
      println("#################PUT " + id)
      val del = "DELETE {<http://socialhelper.com/resourcequantities#" + id + "> ?p ?o} WHERE {<http://socialhelper.com/resourcequantities#" + id + "> ?p ?o}" 
      val req1 = url("http://192.168.1.118:3030/ds/update").POST << Map("update" -> del)
      val rez1 = Http(req1 OK as.String)
      println("#################DONE DELETE " + rez1.apply())
      
      val body = request.body.asFormUrlEncoded
      val quantity = body.get("quantity")(0)
      val resource = body.get("resource")(0)
      val ttl = "@prefix : <http://socialhelper.com/resourcequantities#> .\n" +
                ":" + id + "\n" +
                ":quantity "  + quantity + " ;\n" +
                ":resource <http://socialhelper.com/resources#" + resource + "> ."
      
      val reqa = url("http://192.168.1.118:3030/ds/data?default").POST.setBody(ttl).addHeader("Content-type", "text/turtle")
      val rez = Http(reqa OK as.String)
      Ok(rez.apply())
  }
}