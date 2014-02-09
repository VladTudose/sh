package controllers

object DisasterController extends CrudController {  
    def domain = "disasters"
    def fields = Seq(("name", None),
        ("location", Some(LocationController.domainPath)),
        ("requireditems", Some(ResourceQuantityController.domainPath)))
}