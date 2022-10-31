package clicker.server

import play.api.libs.json.{JsArray, JsObject, JsValue, Json}
import java.sql.{Connection, DriverManager, ResultSet}
import java.lang.System.currentTimeMillis

import akka.actor.ActorRef
import clicker.game._
import play.api.libs.json.JsValue

object DatabaseControl {

     val url: String = "jdbc:mysql://localhost/mysql?serverTimezone=UTC"
     val ps: String = sys.env("dbpw")
     val MyConnection: Connection = DriverManager.getConnection(url, "root", ps)

     def CheckInTable(_Name: String, GameInstance: ActorRef): Unit = {
          val Statement = MyConnection.createStatement()
          val result: ResultSet = Statement.executeQuery("SELECT * FROM players WHERE username = '"+_Name+"'")
          if (result.next()){
               println("Username in database")
               val state = result.getString("gamestate")
               GameInstance ! StateTransfer(state)
               GameInstance ! CorrectMoney(result.getLong("lasttime"))
          } else {
               println("Username not in database")
               val s = MyConnection.prepareStatement("INSERT INTO players VALUES (?,0,'EMPTY')")
               s.setString(1,_Name)
               s.execute()
          }
     }

     def SaveToTable(_Data: String, GameInstance: ActorRef): Unit = {
          val Parsed: Map[String, JsValue] = Json.parse(_Data).as[Map[String,JsValue]]
          val name: String = Parsed("username").as[String]
          val time = currentTimeMillis()
          val Statement = MyConnection.createStatement()
          Statement.execute("UPDATE players SET lasttime = '"+time.toString+"', gamestate = '"+_Data+"' WHERE username = '"+name+"'")
     }

     def CheckAndCreate(): Unit = {
          val Statement = MyConnection.createStatement()
          Statement.execute("CREATE TABLE IF NOT EXISTS players (username tinytext, lasttime bigint, gamestate mediumtext)")
     }

     def main(args: Array[String]): Unit = {
          //val Statement = MyConnection.createStatement()
          CheckAndCreate()
          //Statement.execute("DROP TABLE playerdata")
          //Statement.execute("create table if not exists playerdata (username tinytext, money double, equipment text)")
//          val PrepStatement = MyConnection.prepareStatement("INSERT INTO playerdata VALUE (?,?,?)")
//          PrepStatement.setString(1,"poop")
//          PrepStatement.setDouble(2,100)
//          PrepStatement.setString(3,"{'PEPEPEPEPEPE'}")
//          PrepStatement.execute()
//          val r: ResultSet = Statement.executeQuery("SELECT * FROM playerdata")
//          while (r.next()){
//               println(r.getString("username"))
//          }
          //GetUserData("poop")
     }

}
