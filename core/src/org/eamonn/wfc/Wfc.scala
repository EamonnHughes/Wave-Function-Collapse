package org.eamonn.wfc

import com.badlogic.gdx.Application.ApplicationType
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.{ApplicationAdapter, Gdx, Input}
import org.eamonn.wfc.scenes.Game
import org.eamonn.wfc.util.{GarbageCan, TextureWrapper}

class Wfc extends ApplicationAdapter {
  import Wfc.garbage

  private val idMatrix = new Matrix4()
  private var batch: PolygonSpriteBatch = _
  private var scene: Scene = _

  override def create(): Unit = {

    Gdx.input.setCatchKey(Input.Keys.BACK, true)

    batch = garbage.add(new PolygonSpriteBatch())

    Wfc.zero = TextureWrapper.load("0.png")
    Wfc.one = TextureWrapper.load("1.png")
    Wfc.two = TextureWrapper.load("2.png")
    Wfc.three = TextureWrapper.load("3.png")
    Wfc.four = TextureWrapper.load("4.png")
    Wfc.five = TextureWrapper.load("5.png")
    Wfc.six = TextureWrapper.load("6.png")
    Wfc.seven = TextureWrapper.load("7.png")
    Wfc.eight = TextureWrapper.load("8.png")
    Wfc.nine = TextureWrapper.load("9.png")
    Wfc.unChosen = TextureWrapper.load("Square.png")
    Wfc.stairs = TextureWrapper.load("stair.png")

    //    Wfc.sound = Wfc.loadSound("triangle.mp3")

    Text.loadFonts()

    setScene(new Game)
  }

  override def render(): Unit = {

    val delta = Gdx.graphics.getDeltaTime
    scene.update(delta) foreach setScene
    ScreenUtils.clear(0f, 0f, 0f, 1)
    batch.setTransformMatrix(idMatrix)
    batch.begin()
    scene.render(batch)

    batch.end()
  }

  private def setScene(newScene: Scene): Unit = {
    scene = newScene
    Gdx.input.setInputProcessor(scene.init())
  }

  override def dispose(): Unit = {
    garbage.dispose()
  }

}

object Wfc {
  implicit val garbage: GarbageCan = new GarbageCan

  var sound: Sound = _
  var zero: TextureWrapper = _
  var unChosen: TextureWrapper = _
  var one: TextureWrapper = _
  var two: TextureWrapper = _
  var three: TextureWrapper = _
  var four: TextureWrapper = _
  var five: TextureWrapper = _
  var six: TextureWrapper = _
  var seven: TextureWrapper = _
  var eight: TextureWrapper = _
  var nine: TextureWrapper = _
  var stairs: TextureWrapper = _

  def mobile: Boolean = isMobile(Gdx.app.getType)

  private def isMobile(tpe: ApplicationType) =
    tpe == ApplicationType.Android || tpe == ApplicationType.iOS

  private def loadSound(path: String)(implicit garbage: GarbageCan): Sound =
    garbage.add(Gdx.audio.newSound(Gdx.files.internal(path)))

}
