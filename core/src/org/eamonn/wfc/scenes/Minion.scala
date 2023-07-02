package org.eamonn.wfc.scenes

import org.eamonn.wfc

import scala.util.Random

case class Minion(var location: Int, game: Game) {
  var destination = location
  var tick = 0f
  def update(delta: Float): Unit = {
    tick += delta
    if(tick >= .25f){
      var path = wfc.findPath(location, destination, game)
      path.filter(p => p.list.length > 1).foreach(p => location = p.list.reverse(1))
      tick = 0f
    }
      while(game.grid(destination).options.head == 0 || location == destination) {
        destination = Random.nextInt(game.grid.length)
      }
  }
}
