package controllers

object ResourceQuantityController extends CrudController {
    def domain = "resourcequantities"
    def fields = Seq(("resource", Some(ResourceController.domainPath)),
        ("quantity", None))
}