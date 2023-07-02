package org.eamonn.wfc
package scenes

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import org.eamonn.wfc.Scene
import org.eamonn.wfc.util.TextureWrapper

import scala.util.Random

class Home extends Scene {

  var grid: Array[gridItem] = Array.fill(dimensions * dimensions)(
    gridItem(collapsed = false, getAllTileNumbers())
  )

  override def init(): InputAdapter = {
    new HomeControl(this)

  }

  override def update(delta: Float): Option[Scene] = {
    collapseLeast()
    None
  }

  def getAllowed(x: Int, y: Int): List[Int] = {
    var index = x + y * dimensions

    var availOptions = grid(index).options

    //look down
    if (y > 0) {
      var downTile = grid(index - dimensions)
      availOptions = availOptions.filter(optionAvailable =>
        downTile.options.exists(optionDown =>
          tiles(optionDown).up == tiles(optionAvailable).down.reverse
        )
      )
    } else {
      availOptions = availOptions.filter(avail => tiles(avail).down == "000000000")
    }
    //look left
    if (x > 0) {
      var leftTile = grid(index - 1)
      availOptions = availOptions.filter(optionAvailable =>
        leftTile.options.exists(optionLeft =>
          tiles(optionLeft).right == tiles(optionAvailable).left.reverse
        )
      )
    } else {
      availOptions = availOptions.filter(avail => tiles(avail).left == "000000000")
    }
    //look right
    if (x < dimensions - 1) {
      var rightTile = grid(index + 1)
      availOptions = availOptions.filter(optionAvailable =>
        rightTile.options.exists(optionRight =>
          tiles(optionRight).left == tiles(optionAvailable).right.reverse
        )
      )

    } else {
      availOptions = availOptions.filter(avail => tiles(avail).right == "000000000")
    }
    //look up
    if (y < dimensions - 1) {
      var upTile = grid(index + dimensions)
      availOptions = availOptions.filter(optionAvailable =>
        upTile.options.exists(optionUp =>
          tiles(optionUp).down == tiles(optionAvailable).up.reverse
        )
      )
    } else {
      availOptions = availOptions.filter(avail => tiles(avail).up == "000000000")
    }
    availOptions
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

      var x = grid.indexOf(picked)%dimensions
      var y = (grid.indexOf(picked) - x)/dimensions
      var allowed = getAllowed(x, y)
      if(allowed.isEmpty){
        grid = Array.fill(dimensions * dimensions)(
          gridItem(collapsed = false, getAllTileNumbers())
        )
      } else {
        picked.options = List(
          allowed(Random.nextInt(allowed.length))
        )
        picked.collapsed = true


        grid = generateNewGrid()
      }
    }
  }

  def generateNewGrid(): Array[gridItem] = {
    var nextGrid: Array[gridItem] = grid.clone()
    for (x <- 0 until dimensions) {
      for (y <- 0 until dimensions) {
        var index = x + y * dimensions
        if (grid(index).collapsed) {
          nextGrid(index) = grid(index)
        } else {
          var availOptions = getAllowed(x, y)
          if (availOptions.nonEmpty) {
            nextGrid(index).options = availOptions
            if (availOptions.length == 1) nextGrid(index).collapsed = true
          } else {
            grid = Array.fill(dimensions * dimensions)(
              gridItem(collapsed = false, getAllTileNumbers())
            )
          }
        }
      }
    }
    return nextGrid
  }

  override def render(batch: PolygonSpriteBatch): Unit = {
    for (x <- 0 until dimensions) {
      for (y <- 0 until dimensions) {
        var cell = grid(x + y * dimensions)
        batch.setColor(Color.WHITE)
        if (cell.collapsed) {
          var index = cell.options.head

          batch.draw(
            tiles(index).texture,
            x * screenUnit,
            y * screenUnit,
            screenUnit/2,
            screenUnit/2,
            screenUnit,
            screenUnit,
            1f,
            1f,
            tiles(index).rot,
            0,
            0,
            9,
            9,
            false,
            false
          )
        } else {
          batch.setColor(Color.GRAY)
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

case class tileType(
    var down: String,
    var left: String,
    var right: String,
    var up: String,
    var rot: Float = 0f,
    var texture: TextureWrapper
) {

  def rotateTile(rotation: Int): tileType = {
    if (rotation == 1) {
      return tileType(
        texture = texture,
        down = right,
        left = down,
        up = left,
        right = up,
        rot = -90
      )
    } else if (rotation == 2) {
      return tileType(
        texture = texture,
        down = up,
        left = right,
        up = down,
        right = left,
        rot = -180
      )
    } else if (rotation == 3) {
      return tileType(
        texture = texture,
        down = left,
        left = up,
        up = right,
        right = down,
        rot = -270
      )
    } else return this
  }
}