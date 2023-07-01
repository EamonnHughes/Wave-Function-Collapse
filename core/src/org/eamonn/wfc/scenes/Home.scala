package org.eamonn.wfc
package scenes

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import org.eamonn.wfc.Scene

import scala.util.Random

class Home extends Scene {

  var grid: Array[gridItem] = Array.fill(dimensions * dimensions)(
    gridItem(collapsed = false, List(BLANK, DOWN, LEFT, RIGHT, UP))
  )

  override def init(): InputAdapter = {
    new HomeControl(this)

  }

  override def update(delta: Float): Option[Scene] = {
    None
  }

  //pick cell with least entropy
  def collapseLeast(): Unit = {
    if (grid.exists(_.collapsed == false)) {
      val leastLength =
        grid.filter(_.collapsed == false).map(_.options.length).min
      val least = grid.filter(cell =>
        cell.options.length == leastLength && !cell.collapsed
      )

      val picked = least(Random.nextInt(least.length))
      picked.collapsed = true
      picked.options = List(
        picked.options(Random.nextInt(picked.options.length))
      )
    }
  }

  override def render(batch: PolygonSpriteBatch): Unit = {
    for (x <- 0 until dimensions) {
      for (y <- 0 until dimensions) {
        var cell = grid(x + y * dimensions)
        batch.setColor(Color.WHITE)
        if (cell.collapsed) {
          var index = cell.options.head
          batch.draw(
            tiles(index),
            x * screenUnit,
            y * screenUnit,
            screenUnit,
            screenUnit
          )
        } else {
          batch.draw(
            Wfc.unChosen,
            x * screenUnit,
            y * screenUnit,
            screenUnit,
            screenUnit
          )
        }
      }
    }
  }
}

case class gridItem(
    var collapsed: Boolean = false,
    var options: List[Int] = List(BLANK, DOWN, LEFT, RIGHT, UP)
)
