package controllers

object DonationController extends CrudController {  
    def domain = "donations"
    def fields = Seq(("donator", Some(DonatorController.domainPath)),
        ("resourcequantity", Some(ResourceQuantityController.domainPath)),
        ("warehouse", Some(WarehouseController.domainPath)))
        
    override def links = Seq("warehouse")
    
    // override def doExtra(body: Map[String, Seq[String]]) = {
    //     val warehouseId = body("warehouse")(0)
    //     val (_, p, o) = WarehouseController.getBindings(warehouseId)
    //     (p zips o._)
    // }
}