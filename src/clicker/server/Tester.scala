package clicker.server

import scala.io.{BufferedSource, Source}
import play.api.libs.json.{JsArray, JsObject, JsValue, Json}

object Tester {

     val UserData: String = Json.stringify(
          Json.toJson(
               Map(
                    "POOPNAME" -> Json.toJson(
                         Map(
                              "autolab" -> Json.toJson(List(0,0)),
                              "print" -> Json.toJson(List(0,0)),
                              "unit_test" -> Json.toJson(List(0,0)),
                              "behavior_test" -> Json.toJson(List(0,0)),
                              "debugger" -> Json.toJson(List(0,0)),
                         )
                    )
               )
          )
     )

     def main(args: Array[String]): Unit = {
          val E = scala.collection.mutable.Map("Poop" -> 1, "Pasd" -> 2)
          println(E)
          E.remove("Poop")
          println(E)
     }

}
