package controllers

object WarehouseController extends CrudController {
    def domain = "warehouses"
    def fields = Seq(("location", Some(LocationController.domainPath)),
         ("inventory", Some(ResourceQuantityController.domainPath)))
     override def arrayFields = Seq("inventory")
}