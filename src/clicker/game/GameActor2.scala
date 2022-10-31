package clicker.game

import akka.actor.Actor
import play.api.libs.json.{JsArray, JsObject, JsValue, Json}

// OLD VERSION
class GameActor2(Name: String, Configuration: String) extends Actor {

     val Username: String = Name
     val Config: JsValue = Json.parse(Configuration)

     val EquipmentData: Map[String, JsValue] = (for (i <- (Config \ "equipment").as[List[JsObject]]) yield {
          (i \ "id").as[String] -> i
     }).toMap

     // "id" -> (numberOwned, Cost)
     var Equipment: Map[String, List[Double]] = (for (i <- EquipmentData.keys) yield {
          i -> List(0.0,0.0)
     }).toMap
     var Test: List[Double] = List(0.0,0.0)

     var Money: Double = 0
     var PerClick: Int = 0
     var PerSec: Double = 0

     override def receive: Receive = {
          case Click =>
               Money += 1 + PerClick
          case BuyEquipment(id: String) =>
               val Price: Double = (EquipmentData(id) \ "initialCost").as[Double]
               if (Money >= Price) {
                    Money -= Price
                    Test.patch(0, Seq(Test.head + 1), 1)
                    //Equipment(id).head += 1.toDouble
                    PerSec += (EquipmentData(id) \ "incomePerSecond").as[Double]
                    PerClick += (EquipmentData(id) \ "incomePerClick").as[Int]
               }
          case Update =>
               Money += PerSec
               val SendIt: String = Json.stringify(Json.toJson(Map(
                    "username" -> Json.toJson(Username),
                    "equipment" -> Json.toJson(
                         for (id <- Equipment) yield { Map(
                              "id" -> Json.toJson(id._1),
                              "numberOwned" -> Json.toJson(id._2.head),
                              "cost" -> Json.toJson(id._2.last),
                              )
                         }
                    )
               )))
               sender() ! GameState(SendIt)
     }

}
