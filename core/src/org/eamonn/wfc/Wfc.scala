package org.eamonn.wfc

import com.badlogic.gdx.Application.ApplicationType
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.{ApplicationAdapter, Gdx, Input}
import org.eamonn.wfc.scenes.Home
import org.eamonn.wfc.util.{GarbageCan, TextureWrapper}

class Wfc extends ApplicationAdapter {
  import Wfc.garbage

  private val idMatrix = new Matrix4()
  private var batch: PolygonSpriteBatch = _
  private var scene: Scene = _


  override def create(): Unit = {

    Gdx.input.setCatchKey(Input.Keys.BACK, true)

    batch = garbage.add(new PolygonSpriteBatch())

    Wfc.Blank = TextureWrapper.load("blank.png")
    Wfc.Down = TextureWrapper.load("down.png")
    Wfc.Left = TextureWrapper.load("left.png")
    Wfc.Right = TextureWrapper.load("right.png")
    Wfc.Up = TextureWrapper.load("up.png")


    //    Wfc.sound = Wfc.loadSound("triangle.mp3")

    Text.loadFonts()

    setScene(new Home)
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

  override def dispose(): Unit = {
    garbage.dispose()
  }

  private def setScene(newScene: Scene): Unit = {
    scene = newScene
    Gdx.input.setInputProcessor(scene.init())
  }

}

object Wfc {
  implicit val garbage: GarbageCan = new GarbageCan

  var sound: Sound = _
  var Blank: TextureWrapper = _
  var Down: TextureWrapper = _
  var Up: TextureWrapper = _
  var Left: TextureWrapper = _
  var Right: TextureWrapper = _

  def mobile: Boolean = isMobile(Gdx.app.getType)

  private def isMobile(tpe: ApplicationType) =
    tpe == ApplicationType.Android || tpe == ApplicationType.iOS

  private def loadSound(path: String)(implicit garbage: GarbageCan): Sound =
    garbage.add(Gdx.audio.newSound(Gdx.files.internal(path)))

}
