package controllers

object DonationController extends CrudController {  
    def domain = "donations"
    def fields = Seq(("donator", Some(DonatorController.domainPath)),
        ("resourcequantity", Some(ResourceQuantityController.domainPath)),
        ("warehouse", Some(WarehouseController.domainPath)))
        
    override def links = Seq("warehouse")
    
    // def getResQ(rqp: Seq[String], rqo: Seq[(String, String)]) = {
    //     val resId = ((rqp zip rqo) filter { case (p, _) => p == "resource"})(0)._2._2
    //     val quantity = ((rqp zip rqo) filter { case (p, _) => p == "quantity"})(0)._2._2
    //     (resId, quantity)
    // }
    // 
    // override def doExtra(body: Map[String, Seq[String]]) = {
    //     val warehouseId = body("warehouse")(0)
    //     val rqId = body("resourcequantity")(0)
    //     
    //     val (rqs, rqp, rqo) = ResourceQuantityController.getBindings(rqId)
    //     
    //     
    //     val (_, p, o) = WarehouseController.getBindings(warehouseId)
    //     (p zips (o.unzip._2)) filter { 
    //         case (p, o) => p == "inventory" }).unzip._2 map {
    //         o => o.substring(ResourceQuantityController.lg)
    //     } map {
    //         o => (o, ResourceQuantityController.getBindings(o))
    //     } filter { case (o, b) => 
    // }
}