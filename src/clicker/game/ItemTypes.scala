package clicker.game

class GameItem(val id: String, val Name: String, val PerClick: Int, val PerSec: Double, val Initial: Int, val Exponent: Double)
class OwnedItem(val id: String, var Amount: Int, var Cost: Double)
