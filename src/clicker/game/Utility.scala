package clicker.game

import play.api.libs.json.{JsArray, JsObject, JsValue, Json}
//import java.lang.System.nanoTime

object Utility {

     def CreateItems(_JsonString: String): Map[String,GameItem] = {
          val Parsed: JsValue = Json.parse(_JsonString)
          val EqList: List[Map[String,JsValue]] = (Parsed \ "equipment").as[List[Map[String,JsValue]]]
          val Items: Map[String,GameItem] = (for (item <- EqList) yield {
               item("id").as[String] -> new GameItem(item("id").as[String], item("name").as[String], item("incomePerClick").as[Int], item("incomePerSecond").as[Double], item("initialCost").as[Int], item("priceExponent").as[Double])
          }).toMap
          Items
     }

     def CreateOwned(_ItemList: Map[String,GameItem]): Map[String,OwnedItem] = {
          (for (item <- _ItemList.values) yield {
               item.id -> new OwnedItem(item.id, 0, item.Initial)
          }).toMap
     }

     def CreateJson(_Name: String, _Money: Double, _OwnedList: Map[String,OwnedItem]): String = {
          val Msg: String = Json.stringify(
               Json.toJson(
                    Map(
                         "username" -> Json.toJson(_Name),
                         "currency" -> Json.toJson(_Money),
                         "equipment" -> Json.toJson(
                              for (item <- _OwnedList.values) yield {
                                   Map(
                                        "id" -> Json.toJson(item.id),
                                        "numberOwned" -> Json.toJson(item.Amount),
                                        "cost" -> Json.toJson(item.Cost)
                                   )
                              }
                         )
                    )
               )
          )
          Msg
     }

     // Print useless things
     def main(args: Array[String]): Unit = {
//          // Checking units
//          val Last: Long = nanoTime()
//          println(Last)
//          Thread.sleep(1000)
//          println((nanoTime() - Last) / 1000000000)
          println(CreateJson("me",69,Map("1" -> new OwnedItem("1",1,1))))
     }

}
