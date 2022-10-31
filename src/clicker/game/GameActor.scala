package clicker.game

import akka.actor.Actor
import play.api.libs.json.{JsArray, JsObject, JsValue, Json}
import java.lang.System.{currentTimeMillis, nanoTime}

import Utility._

class GameActor(val Username: String, Configuration: String) extends Actor {

     val ItemList: Map[String,GameItem] = CreateItems(Configuration)
     var MyItems: Map[String,OwnedItem] = CreateOwned(ItemList)

     var LastTime: Long = nanoTime()

     var Money: Double = 0
     var PerSec: Double = 0
     var PerClick: Double = 0

     override def receive: Receive = {
          case Click =>
               Money += 1 + PerClick
          case BuyEquipment(id: String) =>
               if (Money >= MyItems(id).Cost){
                    Money -= MyItems(id).Cost
                    MyItems(id).Cost *= ItemList(id).Exponent
                    MyItems(id).Amount += 1
                    PerClick += ItemList(id).PerClick
                    PerSec += ItemList(id).PerSec
               }
          case Update =>
               val dif: Long = (nanoTime() / 1000000000 - LastTime / 1000000000)
               Money += PerSec * dif
               val Msg: String = CreateJson(Username, Money, MyItems)
               //println(Msg)
               sender() ! GameState(Msg)
               LastTime = nanoTime()
          case DataRequest =>
               val Msg: String = CreateJson(Username, Money, MyItems)
               sender() ! SaveData(Msg)
          case StateTransfer(stateString: String) =>
               println(stateString)
               val Parsed: Map[String,JsValue] = Json.parse(stateString).as[Map[String,JsValue]]
               Money += Parsed("currency").as[Double]
               for (part <- Parsed("equipment").as[List[JsValue]]){
                    val Item: OwnedItem = MyItems((part \ "id").as[String])
                    Item.Cost = (part \ "cost").as[Double]
                    Item.Amount = (part \ "numberOwned").as[Int]
                    PerClick += Item.Amount * ItemList(Item.id).PerClick
                    PerSec += Item.Amount * ItemList(Item.id).PerSec
               }
          case CorrectMoney(last: Long) =>
               Money += PerSec * ((currentTimeMillis() - last)/1000)
     }

}
