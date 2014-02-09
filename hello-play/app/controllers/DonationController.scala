package controllers

object DonationController extends CrudController {  
    def domain = "donations"
    def fields = Seq(("donator", Some(DonatorController.domainPath)),
        ("resourcequantity", Some(ResourceQuantityController.domainPath)),
        ("warehouse", Some(WarehouseController.domainPath)))
}