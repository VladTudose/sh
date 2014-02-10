package controllers

import dispatch._, Defaults._

object DonationController extends CrudController {  
    def domain = "donations"
    def fields = Seq(("donator", Some(DonatorController.domainPath)),
        ("resourcequantity", Some(ResourceQuantityController.domainPath)),
        ("warehouse", Some(WarehouseController.domainPath)))
        
    override def links = Seq("warehouse")
    
    def getResQ(rqId: String) = {
        val (rqs, rqp, rqo) = ResourceQuantityController.getBindings(rqId)
        val resId = ((rqp zip rqo) filter { case (p, _) => p == "resource"})(0)._2._2
        val quantity = ((rqp zip rqo) filter { case (p, _) => p == "quantity"})(0)._2._2
        (resId, quantity.toInt)
    }
    
    override def doExtra(body: Map[String, Seq[String]]) = {
        val warehouseId = body("warehouse")(0)
        val rqId = body("resourcequantity")(0)
        val (resId, quantity) = getResQ(rqId)
        val (_, p, o) = WarehouseController.getBindings(warehouseId)
        val oResQ = ((p zip (o.unzip._2)) filter { 
            case (p, o) => p == "inventory" }).unzip._2 map {
            o => o.substring(ResourceQuantityController.lg)
        } map { o => (o, getResQ(o)) } filter {
            case (o, resQ) => resQ._1 == resId
        }
        
        val q = (if(oResQ.isEmpty) 0
        else {
            val rqId2 = oResQ(0)._1
            val req = url(updateUrl).POST << Map("update" ->
                ResourceQuantityController.deleteCommand.format(rqId2, rqId2))
            Http(req OK as.String).apply()
            oResQ(0)._2._2.toInt
        })
        
        
        // creaza resource quantity
        val id = java.util.UUID.randomUUID.toString
        val ttl = "@prefix : <http://socialhelper.com/api/resourcequantities/> .\n" +
            ":" + id + "\n" +
            ":quantity "  + (q + quantity) + " ;\n" +
            ":resource <http://socialhelper.com/api/resources/" + resId + "> ."
        val req = url(postUrl).POST.setBody(ttl).addHeader("Content-type", "text/turtle")
        Http(req OK as.String)
        
        val ttl2 = "@prefix : <http://socialhelper.com/api/warehouses/> .\n" +
            ":" + warehouseId + "\n" +
            ":inventory <http://socialhelper.com/api/resourcequantities/" + id + "> ."
        val req2 = url(postUrl).POST.setBody(ttl2).addHeader("Content-type", "text/turtle")
        Http(req2 OK as.String)
    }
}