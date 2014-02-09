package controllers

import play.api.mvc.{Action, Controller, AnyContentAsFormUrlEncoded}
import dispatch._, Defaults._
import play.api.libs.json._
import play.api.libs.json.Json._

object RequestEarthquakesController extends Controller {
    
    def post = Action { request =>
        val body = request.body.asFormUrlEncoded
        val starttime = body.get("starttime")(0)
        val endtime = body.get("endtime")(0)
        val minmagnitude = body.get("minmagnitude")(0)
        val latitude = body.get("latitude")(0)
        val longitude = body.get("longitude")(0)
        val minradiuskm = body.get("minradiuskm")(0)
        val maxradiuskm = body.get("maxradiuskm")(0)
        val orderby = body.get("orderby")(0)
        val map = (Map(
            "format" -> "geojson"
            ) ++ (if(starttime != "") Map("starttime" -> starttime) else Map())
            ++ (if(endtime != "") Map("endtime" -> endtime) else Map())
            ++ (if(minmagnitude != "") Map("minmagnitude" -> minmagnitude) else Map())
            ++ (if(latitude != "") Map("latitude" -> latitude) else Map())
            ++ (if(longitude != "") Map("longitude" -> longitude) else Map())
            ++ (if(minradiuskm != "") Map("minradiuskm" -> minradiuskm) else Map())
            ++ (if(maxradiuskm != "") Map("maxradiuskm" -> maxradiuskm) else Map())
            ++ (if(orderby != "") Map("orderby" -> orderby) else Map()))
        println("###################### " + map.toString())
        val req = url("http://comcat.cr.usgs.gov/fdsnws/event/1/query") <<? map
        val rez = Http(req OK as.String).apply()     
        Ok(rez)
    }
}