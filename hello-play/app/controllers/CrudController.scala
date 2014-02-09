package controllers

import play.api.mvc.{Action, Controller, AnyContentAsFormUrlEncoded}
import dispatch._, Defaults._
import play.api.libs.json._
import play.api.libs.json.Json._

trait CrudController extends Controller {
    def domain: String
    def fields: Seq[(String, Option[String])]
    
    def apiPath = "http://socialhelper.com/api/"
    def fusekiUrl = "http://localhost:3030/ds/"
    def postUrl = fusekiUrl + "data?default"
    def queryUrl = fusekiUrl + "query?default"
    def updateUrl = fusekiUrl + "update"
    def domainPath = apiPath + domain + "/"
    def lg = domainPath.length()
    def query = ("SELECT ?s ?p ?o WHERE { ?s ?p ?o .\n" +
            "FILTER(STRSTARTS(STR(?s), \"%s%%s\")) .\n}").format(domainPath)
    def deleteCommand = "DELETE { <%s%%s> ?p ?o } WHERE { <%s%%s> ?p ?o }".format(
        domainPath, domainPath)
    
    def post = Action { request =>
        val body = request.body.asFormUrlEncoded
        val id = java.util.UUID.randomUUID.toString
        val ttl = "@prefix : <" + domainPath + "> .\n" +
        ":" + id + "\n" + (fields map {
            case (f, prefix) => "\t:" + f + " " + getTurtleList(body.get(f), prefix)
        } reduceLeft {
            (a, b) => a + " ;\n" + b
        }) + " ."
        val req = url(postUrl).POST.setBody(ttl).addHeader("Content-type", "text/turtle")
        Http(req OK as.String)
        Ok("{id: " + id + "}")
    }
    
    def getAll = Action {
        val (s, p, o) = getBindings("")
        val entities = (s zip (p zip o)) groupBy { case (s, _) => s } map {
            case (s, spo) => (s, spo.unzip._2)
        } map {
            case (id, po) => makeJson(id, po)
        }
        val json = Json.toJson(
            Map(
                "_links" -> toJson(
                    Map(
                        "self" -> toJson(
                            Map(
                                "href" -> toJson("/" + domain)
                            )
                        ),
                        "find" -> toJson(
                            Map(
                                "href" -> toJson("/" + domain + "/{?id}"),
                                "templated" -> toJson(true)
                            )
                        )
                    )
                ),
                "_embedded" -> toJson(
                    Map(
                        domain -> entities
                    )
                )
            )
        )
        Ok(Json.prettyPrint(Json.toJson(json))).as("application/json")
    }
    
    def get(id: String) = Action {
        val (s, p, o) = getBindings(id)
        Ok(Json.prettyPrint(makeJson(id, p zip o))).as("application/json")
    }
  
    def delete(id: String) = Action {
        val req = url (updateUrl).POST << Map("update" -> deleteCommand.format(id, id))
        val rez = Http(req OK as.String)
        Ok(rez.apply())
    }
    
    def put(id: String) = Action { request =>
        val reqd = url (updateUrl).POST << Map("update" -> deleteCommand.format(id, id))
        Http(reqd OK as.String)
        
        val body = request.body.asFormUrlEncoded
        val ttl = "@prefix : <" + domainPath + "> .\n" +
        ":" + id + "\n" + (fields map {
            case (f, prefix) => "\t:" + f + " " + getTurtleList(body.get(f), prefix)
        } reduceLeft {
            (a, b) => a + " ;\n" + b
        }) + " ."
        val req = url(postUrl).POST.setBody(ttl).addHeader("Content-type", "text/turtle")
        Http(req OK as.String)
        Ok("{id: " + id + "}")
    }
    
    def getTurtleList(vals: Seq[String], prefix: Option[String]) = {
        vals map { o => prefix match {
                case Some(pre) => "<" + pre + o + ">"
                case None => "\"" + o + "\""
            }
        } reduceLeft { (a, b) => a + ", " + b } 
    }
    
    def getBindings(id: String) = {
        val req = url(queryUrl) <<? Map("query" -> query.format(id))
        val bindings = Json.parse(Http(req OK as.String).apply()) \ "results" \ "bindings"
        val s = (bindings \\ "s") map {
            s => (s \ "value").as[String].substring(lg)
        }
        val p = (bindings \\ "p") map {
            p => (p \ "value").as[String].substring(lg)
        }
        val o = (bindings \\ "o") map {
            o => ((o \ "type").as[String], (o \ "value").as[String])
        }
        (s, p, o)
    }
    
    def makeValueFromSeq(vals: Seq[JsValue]) = {
        if(vals.tail.isEmpty)
            vals.head
        else
            toJson(vals)
    }
    
    def makeValueFromString(tv: (String, String)) = {
        val (t, v) = tv
        if(t.equals("uri")) {
            val i = v.lastIndexOf("/")
            val id = v.substring(i + 1)
            val j = v.lastIndexOf("/", i - 1)
            val d = v.substring(j + 1, i)
            val controller = d match {
                case d if d.equals(DonatorController.domain) => DonatorController
                case d if d.equals(LocationController.domain) => LocationController
                case d if d.equals(ResourceController.domain) => ResourceController
                case d if d.equals(ResourceQuantityController.domain) => ResourceQuantityController
            }
            val (s, p, o) = controller.getBindings(id)
            controller.makeJson(id, p zip o)
        }
        else
            toJson(v)
    }
    
    def makeJson(id: String, po: Seq[(String, (String, String))]): JsValue = {
        val properties = po groupBy { case (p, _) => p } map { case (p, po) =>
            (p, makeValueFromSeq(po.unzip._2 map(makeValueFromString))) } groupBy{
            case (p, _) => (fields find { f => f._1 == p }).get._2 isEmpty
        }
        Json.toJson(
            Map(
                "_links" -> toJson(
                    Map(
                        "self" -> toJson(
                            Map(
                                "href" -> toJson("/" + domain + "/" + id)
                            )
                        )
                    )
                )
            )
            ++ (if(properties.get(true).isEmpty) Map() else properties(true))
            ++ (if(properties.get(false).isEmpty) Map() else
            Map(
                "_embedded" -> toJson(properties(false))
            ))
        )
    }
}