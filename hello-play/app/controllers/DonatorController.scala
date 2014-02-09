package controllers

object DonatorController extends CrudController {  
    def domain = "donators"
    def fields = Seq(("firstname", None), ("lastname", None), ("email", None))
}