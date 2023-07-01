package org.eamonn

import com.badlogic.gdx.Gdx.input
import com.badlogic.gdx.Input.Peripheral
import com.badlogic.gdx.graphics.Color
import org.eamonn.wfc.scenes.tileType
import org.eamonn.wfc.util.TextureWrapper

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration
import scala.util.Random

// Things kinda stolen from scaloi
package object wfc {

  val BLANK = 0
  val DOWN = 1
  val LEFT = 2
  val RIGHT = 3
  val UP = 4
  val dimensions = 4
  val blankTile = tileType(
    down = 0,
    left = 0,
    right = 0,
    up = 0,
    texture = Wfc.Blank
  )
  val downTile = tileType(
    down = 1,
    left = 1,
    right = 1,
    up = 0,
    texture = Wfc.Down
  )
  val leftTile = tileType(
    down = 1,
    left = 1,
    right = 0,
    up = 1,
    texture = Wfc.Left
  )
  val rightTile = tileType(
    down = 1,
    left = 0,
    right = 1,
    up = 1,
    texture = Wfc.Right
  )
  val upTile = tileType(
    down = 0,
    left = 1,
    right = 1,
    up = 1,
    texture = Wfc.Up
  )
  val tiles: List[tileType] = List(blankTile, downTile, leftTile, rightTile, upTile)


  def d(die: Int): Int = Random.nextInt(die) + 1
  def d(nOd: Int, die: Int): Int = {
    var amt = 0
    for (i <- 0 until (nOd)) { amt += Random.nextInt(die) + 1 }
    amt
  }

  def screenUnit: Float = (Geometry.ScreenWidth) / dimensions

  def compassAvailable: Boolean =
    input.isPeripheralAvailable(Peripheral.Compass)

  implicit class AnyOps(val self: Any) extends AnyVal {

    /** Replace this value with [a]. */
    def as[A](a: A): A = a
  }

  implicit class FloatOps(val self: Float) extends AnyVal {

    /** Clamp this value between 0f and 1f inclusive. */
    def clamp: Float = clamp(1f)

    /** Clamp this value between 0f and [max] inclusive. */
    def clamp(max: Float): Float =
      if (self < 0f) 0f else if (self > max) max else self

    /** Increases an alpha by [delta] time interval spread over [seconds] seconds limited to 1f. */
    def alphaUp(delta: Float, seconds: Float): Float =
      (self + delta / seconds) min 1f

    /** Decreases an alpha by [delta] time interval spread over [seconds] seconds limited to 0f. */
    def alphaDown(delta: Float, seconds: Float): Float =
      (self - delta / seconds) max 0f
  }

  implicit class BooleanOps(val self: Boolean) extends AnyVal {
    def option[A](a: => A): Option[A] = if (self) Some(a) else None
    def fold[A](ifTrue: => A, ifFalse: => A): A = if (self) ifTrue else ifFalse
  }

  implicit class FiniteDurationOps(val self: FiniteDuration) extends AnyVal {
    def toFiniteDuration(tu: TimeUnit): FiniteDuration =
      FiniteDuration(self.toUnit(tu).toLong, tu)

    protected def largestUnit: Option[TimeUnit] =
      TimeUnit.values.findLast(u => self.toUnit(u) >= 1.0)

    def toHumanString: String = {
      largestUnit.fold("no time at all") { u =>
        val scaled = toFiniteDuration(u)
        scaled.toString
        val v = TimeUnit.values.apply(u.ordinal - 1)
        val modulus = FiniteDuration(1, u).toUnit(v).toInt
        val remainder = self.toUnit(v).toLong % modulus
        if (remainder > 0)
          scaled.toString + ", " + FiniteDuration(remainder, v)
        else
          scaled.toString
      }
    }
  }

  implicit class OptionOps[A](val self: Option[A]) extends AnyVal {
    def isTrue(implicit Booleate: Booleate[A]): Boolean =
      self.fold(false)(Booleate.value)
    def isFalse(implicit Booleate: Booleate[A]): Boolean =
      self.fold(false)(Booleate.unvalue)
  }

  private trait Booleate[A] {
    def value(a: A): Boolean
    final def unvalue(a: A): Boolean = !value(a)
  }

  private object Booleate {
    implicit def booleate: Booleate[Boolean] = b => b
  }

  implicit class ColorOps(val self: Color) extends AnyVal {

    /** Returns a new colour with alpha set to [alpha]. */
    def withAlpha(alpha: Float): Color =
      new Color(self.r, self.g, self.b, alpha)

    /** Returns a new colour with alpha multiplied by [alpha]. */
    def ⍺(alpha: Float): Color =
      new Color(self.r, self.g, self.b, self.a * alpha)

    /** Returns a new colour with alpha multiplied by [alpha]². */
    def ⍺⍺(alpha: Float): Color =
      new Color(self.r, self.g, self.b, self.a * alpha * alpha)
  }

  val CenterAlign = 1
}
