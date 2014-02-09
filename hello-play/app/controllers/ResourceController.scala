package controllers

object ResourceController extends CrudController {
    def domain = "resources"
    def fields = Seq(("name", None), ("description", None))
}