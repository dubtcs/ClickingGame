package clicker.server

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import clicker.game.GameActor
import com.corundumstudio.socketio.{AckRequest, SocketIOClient}
import com.corundumstudio.socketio.listener._
import clicker.game._

import DatabaseControl._

class StartGameListener(Server: ClickerServer) extends DataListener[String] {

     override def onData(Client: SocketIOClient, Name: String, ackSender: AckRequest): Unit = {
          val Game = Server.context.system.actorOf(Props(classOf[GameActor], Name, Server.configuration ))
          Server.ClientMap += Client -> Game
          Server.UsernameMap += Client -> Name
          Client.sendEvent("initialize", Server.configuration)
          CheckInTable(Name, Game)
     }

}

class ClickListener(Server: ClickerServer) extends DataListener[Nothing] {

     override def onData(Client: SocketIOClient, Data: Nothing, ackSender: AckRequest): Unit = {
          val CurrentGame: ActorRef = Server.ClientMap(Client)
          CurrentGame ! Click
     }

}

class BuyListener(Server: ClickerServer) extends DataListener[String] {

     override def onData(Client: SocketIOClient, Data: String, ackSender: AckRequest): Unit = {
          val CurrentGame: ActorRef = Server.ClientMap(Client)
          CurrentGame ! BuyEquipment(Data)
     }

}
