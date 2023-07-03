package org.eamonn.wfc.scenes

import org.eamonn.wfc
import org.eamonn.wfc.tiles

import scala.util.Random

case class Minion(var location: Int, game: Game) {
  var destination = location
  var home = -1
  var tick = 0f
  def update(delta: Float): Unit = {
    if (home == -1) {
      home = game.grid.indexOf(
        game.grid.filter(g => tiles(g.options.head).isRoom)(
          Random.nextInt(game.grid.count(g => tiles(g.options.head).isRoom))
        )
      )
    }
    tick += delta
    if (tick >= .25f) {
      var path = wfc.findPath(location, destination, game)
      path
        .filter(p => p.list.length > 1)
        .foreach(p => location = p.list.reverse(1))
      tick = 0f
    }
    if (location == destination && Math.abs(game.time) <= 8f) {
      destination = game.grid.indexOf(
        game.grid.filter(g =>
          !g.isEntrance && g.options.head != 0 && !game.minions.exists(m =>
            m.home == game.grid.indexOf(g)
          )
        )(
          Random.nextInt(
            game.grid.count(
              g => !g.isEntrance && g.options.head != 0 && !game.minions.exists(
                m => m.home == game.grid.indexOf(g)
              )
            )
          )
        )
      )
    } else if (Math.abs(game.time) > 8f) {
      destination = home
    }
  }
}
