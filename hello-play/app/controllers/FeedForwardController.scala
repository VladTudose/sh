package controllers

import play.api.mvc.{Action, Controller, AnyContentAsFormUrlEncoded}
import dispatch._, Defaults._
import play.api.libs.json._
import play.api.libs.json.Json._
import models.Earthquake

object FeedForwardController extends Controller {
    def processAllWarehouses = Action {
        //Json x = Warehouse.getall        
        val req = url("http://localhost:9000/api/warehouses").GET.addHeader("Accept", "application/json")
        val result = Json.parse(Http(req OK as.String).apply())
        val warehouses = (result \ "_embedded" \\ "warehouses")(0).as[List[JsValue]]
        
        var warehousePredictions = List[Map[String, JsValue]]()
        
        for (warehouse <- warehouses) {
            val latitude = (warehouse \ "_embedded" \ "location" \ "latitude").as[String]
            val longitude = (warehouse \ "_embedded" \ "location" \ "longitude").as[String]
            val minradiuskm = "0"
            val maxradiuskm = "1000"
            val orderby = "time"
            val starttime = "2013-01-01 00:00:00"
            val minmagnitude = "4.5"
            val limit = "20000"
            
            val map = (Map(
            "format" -> "geojson"
            ) ++ (if(starttime != "") Map("starttime" -> starttime) else Map())
            ++ (if(minmagnitude != "") Map("minmagnitude" -> minmagnitude) else Map())
            ++ (if(latitude != "") Map("latitude" -> latitude) else Map())
            ++ (if(longitude != "") Map("longitude" -> longitude) else Map())
            ++ (if(minradiuskm != "") Map("minradiuskm" -> minradiuskm) else Map())
            ++ (if(maxradiuskm != "") Map("maxradiuskm" -> maxradiuskm) else Map())
            ++ (if(orderby != "") Map("orderby" -> orderby) else Map())
            ++ (if(limit != "") Map("limit" -> limit) else Map()))
            
            
            val req = url("http://comcat.cr.usgs.gov/fdsnws/event/1/query") <<? map
            val rez = Json.parse(Http(req OK as.String).apply())
            
            val earthquakes = (rez \\ "features")(0).as[List[JsValue]]
            
            var eqList = new java.util.ArrayList[Earthquake]()
            for (earthquake <- earthquakes){
                var date = (earthquake \ "properties" \ "time").as[Long];
                var mag = (earthquake \ "properties" \ "mag").as[Double];
                
                var eq = new Earthquake(new java.util.Date(date), mag)
                eqList.add(eq)
            }
            var ff = new FeedForwardNN();
            var prediction = ff.predictNextValue(eqList)
            
            var lregression = new PolynomialRegression(eqList, 1)
            var lprediction = lregression.predict(eqList.size())
            
            val warehousePrediction = Map(
              "latitude" -> toJson(latitude),
              "longitude" -> toJson(longitude),
              "prediction" -> toJson(prediction.toString()),
              "linear_prediction" -> toJson(lprediction.toString())
            )
            warehousePredictions = warehousePrediction :: warehousePredictions
        }
        
        Ok(Json.prettyPrint(toJson(warehousePredictions))).as("application/json")
    }
}