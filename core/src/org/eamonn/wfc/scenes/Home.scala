package org.eamonn.wfc
package scenes

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import org.eamonn.wfc.Scene

class Home extends Scene {

  override def init(): InputAdapter = new HomeControl(this)

  override def update(delta: Float): Option[Scene] = {
    None
  }

  override def render(batch: PolygonSpriteBatch): Unit = {

  }
}
