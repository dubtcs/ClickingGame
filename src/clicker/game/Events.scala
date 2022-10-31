package clicker.game

// Objects to use as event handlers n whatever

case object Click
case class BuyEquipment(id: String)
case object Update
case class GameState(a: String)
case object DataRequest
case class UpdateData(b: Map[String,List[Double]])
case class SaveData(c: String)
case class StateTransfer(s: String)
case class CorrectMoney(d: Long)
