package org.eamonn.wfc
package scenes

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import org.eamonn.wfc.Scene
import org.eamonn.wfc.Wfc.square
import org.eamonn.wfc.util.TextureWrapper

import scala.util.Random

class Game extends Scene {
  def walkables = grid.filter(g => g.options.head != 0)
  var time = 0f

  def reset(): Unit = {
    grid = Array.fill(dimensions * dimensions)(
      gridItem(collapsed = false, getAllTileNumbers(), isEntrance = false)
    )
    exclavesRemoved = false
    minions = List.empty
  }

  var minions: List[Minion] = List.empty

  var exclavesRemoved = false

  var grid: Array[gridItem] = Array.fill(dimensions * dimensions)(
    gridItem(collapsed = false, getAllTileNumbers())
  )

  override def init(): InputAdapter = {
    new GameControl(this)

  }

  override def update(delta: Float): Option[Scene] = {
    time += delta / 4
    if (time >= 12) {
      time = -12
    }
    if (grid.exists(item => !item.collapsed)) { collapseLeast() }
    else if (!exclavesRemoved) {
      removeExclave()
      exclavesRemoved = true
      if (grid.count(e => tiles(e.options.head).isRoom) <= 12) reset()
    }
    minions.foreach(m => m.update(delta))
    None
  }

  def removeExclave(): Unit = {
    var onlyRooms =
      grid.filter(item => item.collapsed && tiles(item.options.head).isRoom)

    var loc = onlyRooms(Random.nextInt(onlyRooms.length))

    loc.isEntrance = true

    for (i <- 0 until dimensions * dimensions) {
      if (findPath(i, grid.indexOf(loc), this).isEmpty) {
        grid(i).options = List(0)
      }

    }
    for(i <- 0 until 10) {
      minions = Minion(grid.indexOf(loc), this) :: minions
    }
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
      availOptions =
        availOptions.filter(avail => tiles(avail).down == "000000000")
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
      availOptions =
        availOptions.filter(avail => tiles(avail).left == "000000000")
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
      availOptions =
        availOptions.filter(avail => tiles(avail).right == "000000000")
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
      availOptions =
        availOptions.filter(avail => tiles(avail).up == "000000000")
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

      var x = grid.indexOf(picked) % dimensions
      var y = (grid.indexOf(picked) - x) / dimensions
      var allowed = getAllowed(x, y)
      if (allowed.isEmpty) {
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
            screenUnit / 2,
            screenUnit / 2,
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
          if (cell.isEntrance) {
            batch.draw(
              Wfc.stairs,
              x * screenUnit,
              y * screenUnit,
              screenUnit,
              screenUnit
            )
          }

        } else {
          batch.setColor(Color.GRAY)
          batch.draw(
            Wfc.square,
            x * screenUnit,
            y * screenUnit,
            screenUnit,
            screenUnit
          )
        }
        minions.foreach(m => {
          batch.draw(
            Wfc.minionBed,
            toXnY(m.home)._1 * screenUnit,
            toXnY(m.home)._2 * screenUnit,
            screenUnit,
            screenUnit
          )
          batch.draw(
            Wfc.minion,
            toXnY(m.location)._1 * screenUnit,
            toXnY(m.location)._2 * screenUnit,
            screenUnit,
            screenUnit
          )
        })
      }
    }
    batch.setColor(0f, 0f, 0f, ((((Math.abs(time)/3))/4 - 0.25f) min 0.725f) max 0f)
    batch.draw(square, 0f, 0f, Geometry.ScreenWidth, Geometry.ScreenHeight)
    batch.setColor(Color.WHITE)
  }

}

case class gridItem(
    var collapsed: Boolean = false,
    var options: List[Int] = getAllTileNumbers(),
    var isEntrance: Boolean = false
)

case class tileType(
    var down: String,
    var left: String,
    var right: String,
    var up: String,
    var rot: Float = 0f,
    var texture: TextureWrapper,
    var isRoom: Boolean = false
) {

  def rotateTile(rotation: Int): tileType = {
    if (rotation == 1) {
      return tileType(
        texture = texture,
        down = right,
        left = down,
        up = left,
        right = up,
        rot = -90,
        isRoom = this.isRoom
      )
    } else if (rotation == 2) {
      return tileType(
        texture = texture,
        down = up,
        left = right,
        up = down,
        right = left,
        rot = -180,
        isRoom = this.isRoom
      )
    } else if (rotation == 3) {
      return tileType(
        texture = texture,
        down = left,
        left = up,
        up = right,
        right = down,
        rot = -270,
        isRoom = this.isRoom
      )
    } else return this
  }
}
