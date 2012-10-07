package de.hsaugsburg.cep.visualisation

import com.jme3.system.AppSettings
import scala.actors.Actor.actor
import de.hsaugsburg.cep.model._
import de.hsaugsburg.cep.model.ItemsChangedEvent
import de.hsaugsburg.cep.model.WorkEvent
import de.hsaugsburg.cep.model.ItemMovedEvent

/**
 * Class used to test the visualization.
 *
 * @author Benny
 */
object Main extends App {
  val Delay = 2000

  IndustrialPlantApp setShowSettings false

  val settings = new AppSettings(true)
  settings setResolution(1024, 768)
  settings setBitsPerPixel 32
  settings setSamples 4

  IndustrialPlantApp setSettings (settings)
  IndustrialPlantApp start()

  testWorkEvent()

  /**
   * Tests item movement using the following steps. Add a work item, move the item to a neighboring position
   * and remove the item. Each action is performed with a delay of approx. 2 seconds. Can only be verified
   * visually.
   */
  def testItemMovement() {
    val node = new IndustrialPlantNode
    actor {
      val itemName = "TestItem01"

      addItem(node, itemName)
      moveItem(node, itemName, "sensor01", "sensor02")
      removeItem(node, itemName)
    }
  }

  /**
   * Tests the work "animation" of a machine. Add a work item, move the item to machine one,
   * start work, end work and remove the item. Each action is performed with a delay of approx.
   * 2 seconds. Can only be verified visually.
   */
  def testWorkEvent() {
    val node = new IndustrialPlantNode
    actor {
      val itemName = "TestItem01"

      beginWork(node, itemName, "Maschine01")
      endWork(node, itemName, "Maschine01")
    }
  }

  /**
   * Tests a complete simple workflow of the industrial plant. Adds an item, moves the item to the
   * first machine, perform work on item and end work, move to exit and remove the item. Each action
   * is performed with a delay of approx. 2 seconds. Can only be verified visually.
   */
  def testWorkflow() {
    val node = new IndustrialPlantNode
    actor {
      val itemName = "TestItem01"

      addItem(node, itemName)
      moveItem(node, itemName, "sensor01", "sensor02")
      moveItem(node, itemName, "sensor02", "sensor03")
      moveItem(node, itemName, "sensor03", "sensor04")
      beginWork(node, itemName, "Maschine01")
      endWork(node, itemName, "Maschine01")
      moveItem(node, itemName, "sensor04", "sensor05")
      removeItem(node, itemName)
    }


  } /**
   * Performs a basic ItemMoveEvent after approx. two seconds.
   *
   * @param itemName name of the item to move
   * @param source source sensor of the movement
   * @param target target sensor ofthe movement
   */
  def moveItem(node: IndustrialPlantNode, itemName: String, source: String, target: String) {
    Thread sleep Delay
    val moveEvent = ItemMovedEvent("MoveEvent", System.nanoTime(), itemName, source, target)
    node handleItemMovedEvent moveEvent
    println("Item " + itemName + " moved")
  }

  /**
   * Adds an item to the scene after approx. two seconds.
   *
   * @param itemName the name of the item to add
   */
  def addItem(node: IndustrialPlantNode, itemName: String) {
    Thread sleep Delay
    val addItemEvent = ItemsChangedEvent("ItemsChangedAdd", System.nanoTime(), itemName, ChangeType.Added)
    node handleItemsChangedEvent addItemEvent
    println("Item " + itemName + " added")
  }

  /**
   * Remove the item from the scene after approx. two seconds.
   *
   * @param itemName the name of the item to remove
   */
  def removeItem(node: IndustrialPlantNode, itemName: String) {
    Thread sleep Delay
    val removeItemEvent = ItemsChangedEvent("ItemsChangedRemove", System.nanoTime(), itemName, ChangeType.Removed)
    node handleItemsChangedEvent removeItemEvent
    println("Item "+ itemName + " removed")
  }

  /**
   * Begins work at the specified machine.
   *
   * @param itemName name of the item which is being worked on
   * @param machine the name of the machine performing the work
   */
  def beginWork(node: IndustrialPlantNode, itemName: String, machine: String) {
    Thread sleep Delay
    val workEventBegin = WorkEvent("WorkEvent", System.nanoTime(), itemName, machine, Work.Begin)
    node handleWorkEvent workEventBegin
    println("Begin Work at " + machine)
  }

  /**
   * Ends the work at the specified machine.
   *
   * @param itemName name of the item which is being worked on
   * @param machine the name of the machine performing the work
   */
  def endWork(node: IndustrialPlantNode, itemName: String, machine: String) {
    Thread sleep Delay
    val workEventEnd = WorkEvent("WorkEvent", System.nanoTime(), itemName, machine, Work.End)
    node handleWorkEvent workEventEnd
    println("End Work at " + machine)
  }

}