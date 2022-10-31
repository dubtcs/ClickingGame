package clicker.server

import play.api.libs.json.{JsArray, JsObject, JsValue, Json}
import clicker.game._
import akka.actor.{Actor, ActorRef, ActorSystem, Props, scala2ActorRef}
import clicker.UpdateGames
import clicker.game.{GameActor, GameState}
import com.corundumstudio.socketio.{AckRequest, Configuration, SocketIOClient, SocketIOServer}

import DatabaseControl._

import scala.collection.mutable
import scala.io.Source

class ClickerServer(val configuration: String) extends Actor {

     val config: Configuration = new Configuration {
          setHostname("localhost")
          setPort(8080)
     }

     val Server: SocketIOServer = new SocketIOServer(config)

     // ( Socket -> Actor )
     var ClientMap: Map[SocketIOClient, ActorRef] = Map()
     var UsernameMap: Map[SocketIOClient, String] = Map()

     // Connection Listeners
     Server.addConnectListener((Client: SocketIOClient) => {
          println("Connecting: " + Client)
     })
     Server.addDisconnectListener((Client: SocketIOClient) => {
          println("Disconnect: " + Client)
          ClientMap(Client) ! DataRequest
     })

     // Event Listeners
     Server.addEventListener("startGame", classOf[String], new StartGameListener(this))
     Server.addEventListener("click", classOf[Nothing], new ClickListener(this))
     Server.addEventListener("buy", classOf[String], new BuyListener(this))

     val PlayerData: mutable.Map[String, JsValue] = mutable.Map()

     CheckAndCreate()
     Server.start()

     override def receive: Receive = {
          case UpdateGames => {
               for (game <- ClientMap.values) {
                    game ! Update
               }
          }
          case GameState(data: String) => {
               val Client: SocketIOClient = ClientMap.filter(_._2 == sender()).keys.head
               Client.sendEvent("gameState", data)
          }
          case SaveData(_Data: String) => {
               SaveToTable(_Data, sender())
          }
     }

     override def postStop(): Unit = {
          println("stopping server")
          Server.stop()
     }

}

object ClickerServer {

     def main(args: Array[String]): Unit = {
          val actorSystem = ActorSystem()

          import actorSystem.dispatcher
          import scala.concurrent.duration._

          val configuration: String = Source.fromFile("codeConfig.json").mkString

          val server = actorSystem.actorOf(Props(classOf[ClickerServer], configuration))

          actorSystem.scheduler.schedule(0.milliseconds, 100.milliseconds, server, UpdateGames)
     }

}
