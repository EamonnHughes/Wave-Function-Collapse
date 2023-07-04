package org.eamonn.wfc
package scenes

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.InputAdapter

class GameControl(game: Game) extends InputAdapter {
  override def touchDown(
      screenX: Int,
      screenY: Int,
      pointer: Int,
      button: Int
  ): Boolean = {
    true
  }

  override def mouseMoved(screenX: Int, screenY: Int): Boolean = {
    game.mouseX = screenX
    game.mouseY = Geometry.ScreenHeight - screenY
    true
  }

  override def keyUp(keycode: Int): Boolean = {
    if (keycode == Keys.R) {
      game.reset()

    }
    true

  }
}
