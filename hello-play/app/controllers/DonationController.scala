package controllers

import play.api.mvc.{Action, Controller}
import dispatch._, Defaults._
import play.api.libs.concurrent.Execution.Implicits._

object DonationController extends Controller {
    
  //TODO baga ontologii
  //TODO baga @en
  //TODO parseaza JSON
  
  def post = Action { request =>
      val body = request.body.asFormUrlEncoded
      println("#################DONE BODY " + body)
      val donator = body.get("donator")(0)
      val resourceQuantity = body.get("resourcequantity")(0)
      val id = java.util.UUID.randomUUID.toString
      val ttl = "@prefix : <http://socialhelper.com/api/donations/> .\n" +
                ":" + id + "\n" +
                ":donator <http://socialhelper.com/api/donators/" + donator + "> ;\n" +
                ":resourcequantity <http://socialhelper.com/api/resourcequantities/" + resourceQuantity + "> ."
      
      val req = url("http://localhost:3030/ds/data?default").POST.setBody(ttl).addHeader("Content-type", "text/turtle")
      val rez = Http(req OK as.String)
      Ok("{id: " + id + "}")
  }
  
  def getAll = Action {
<<<<<<< HEAD
      val query = "SELECT ?s ?p ?o WHERE { ?s ?p ?o .\nFILTER(STRSTARTS(STR(?s), \"http://socialhelper.com/donations/\")) .\n}"
      val req = url("http://localhost:3030/ds/query?default") <<? Map("query" -> query)
=======
      val query = "SELECT ?s ?p ?o WHERE { ?s ?p ?o .\nFILTER(STRSTARTS(STR(?s), \"http://socialhelper.com/api/donations/\")) .\n}"
      val req = url("http://192.168.1.118:3030/ds/query?default") <<? Map("query" -> query)
>>>>>>> fixed links
      val rez = Http(req OK as.String)
      Ok(rez.apply())
  }
  
  def get(id: String) = Action {
      print("!!!!!!!!!!!!!!!!!!!!!###### " + id)
<<<<<<< HEAD
      val query = "SELECT ?s ?p ?o WHERE { ?s ?p ?o .\nFILTER(STRSTARTS(STR(?s), \"http://socialhelper.com/donations/" + id + "\")) .\n}"
      val req = url("http://localhost:3030/ds/query?default") <<? Map("query" -> query)
=======
      val query = "SELECT ?s ?p ?o WHERE { ?s ?p ?o .\nFILTER(STRSTARTS(STR(?s), \"http://socialhelper.com/api/donations/" + id + "\")) .\n}"
      val req = url("http://192.168.1.118:3030/ds/query?default") <<? Map("query" -> query)
>>>>>>> fixed links
      val rez = Http(req OK as.String)
      Ok(rez.apply())
  }
  
  def delete(id: String) = Action {
      val del = "DELETE {<http://socialhelper.com/api/donations/" + id + "> ?p ?o} WHERE {<http://socialhelper.com/api/donations/" + id + "> ?p ?o}" 
      println("!!!!!!!!!!!!!!!!!!!!! " + id)
      println(del)
      val req = url("http://localhost:3030/ds/update").POST << Map("update" -> del)
      val rez = Http(req OK as.String)
      Ok(rez.apply())
  }
  
  def put(id: String) = Action { request =>
<<<<<<< HEAD
      val del = "DELETE {<http://socialhelper.com/donations/" + id + "> ?p ?o} WHERE {<http://socialhelper.com/donations/" + id + "> ?p ?o}" 
      val req1 = url("http://localhost:3030/ds/update").POST << Map("update" -> del)
=======
      val del = "DELETE {<http://socialhelper.com/api/donations/" + id + "> ?p ?o} WHERE {<http://socialhelper.com/api/donations/" + id + "> ?p ?o}" 
      val req1 = url("http://192.168.1.118:3030/ds/update").POST << Map("update" -> del)
>>>>>>> fixed links
      val rez1 = Http(req1 OK as.String)
      println("#################DONE DELETE " + rez1.apply())
      
      val body = request.body.asFormUrlEncoded
      val donator = body.get("donator")(0)
      val resourceQuantity = body.get("resourcequantity")(0)
      val ttl = "@prefix : <http://socialhelper.com/api/donations/> .\n" +
                ":" + id + "\n" +
                ":donator <http://socialhelper.com/api/donators/"  + donator + "> ;\n" +
                ":resourcequantity <http://socialhelper.com/api/resourcequantities/" + resourceQuantity + "> ."
      
      val reqa = url("http://localhost:3030/ds/data?default").POST.setBody(ttl).addHeader("Content-type", "text/turtle")
      val rez = Http(reqa OK as.String)
      Ok(rez.apply())
  }
}