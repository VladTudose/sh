package controllers

object LocationController extends CrudController {
    def domain = "locations"
    def fields = Seq(("city", None), ("country", None), ("latitude", None),
        ("longitude", None))
}