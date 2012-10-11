package de.hsaugsburg.cep.visualisation

import com.jme3.input.controls.ActionListener
import com.jme3.collision.CollisionResults
import com.jme3.math.{Ray, Vector2f}
import com.jme3.input.{ChaseCamera, InputManager}
import com.jme3.renderer.Camera
import com.jme3.scene.Node

/**
 * Listens for MouseInputListener.PickCameraTarget events which set the target of the ChaseCamera to
 * the target of the registered mouse click.
 *
 * User: Benny
 * Date: 11.10.12
 * Time: 16:00
 */
class MouseInputListener(val root: Node, val inputManager: InputManager, val camera: Camera, val chaseCam: ChaseCamera) extends ActionListener {

  def onAction(name: String, isPressed: Boolean, timePerFrame: Float) {
    val keyReleased = !isPressed
    if (name == MouseInputListener.PickCameraTarget && keyReleased) {
      val collisions = collectCollisions(inputManager.getCursorPosition)
      if (collisions.size() > 0) {
        val closestCollision = collisions.getClosestCollision
        chaseCam setSpatial closestCollision.getGeometry
      }
    }
  }

  private def collectCollisions(click: Vector2f) = {
    val results = new CollisionResults()
    val ray = toRay(click)
    root.collideWith(ray, results)
    results
  }

  private def toRay(click: Vector2f) = {
    val click3d = camera.getWorldCoordinates(new Vector2f(click.x, click.y), 0f).clone()
    val direction = camera.getWorldCoordinates(new Vector2f(click.x, click.y), 1f).subtractLocal(click3d).normalizeLocal()
    new Ray(click3d, direction)
  }
}

object MouseInputListener {
  val PickCameraTarget = "pick cam target"
}
