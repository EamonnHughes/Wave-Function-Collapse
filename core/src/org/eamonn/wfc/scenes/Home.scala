package org.eamonn.wfc
package scenes

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import org.eamonn.wfc.Scene

class Home extends Scene {

  var grid: Array[gridItem] = Array.fill(dimensions * dimensions)(gridItem(collapsed = false, List(BLANK, DOWN, LEFT, RIGHT, UP)))

  override def init(): InputAdapter = {
      new HomeControl(this)

  }

  override def update(delta: Float): Option[Scene] = {
    None
  }

  override def render(batch: PolygonSpriteBatch): Unit = {
    for(x <- 0 until dimensions) {
      for(y <- 0 until dimensions){
        var cell = grid(x + y*dimensions)
        if(cell.collapsed){
          var index = cell.options.head
          batch.draw(tiles(index), x * screenUnit, y * screenUnit, screenUnit, screenUnit)
        }
      }
    }
  }
}

case class gridItem(collapsed: Boolean = false, options: List[Int] = List(BLANK, DOWN, LEFT, RIGHT, UP))