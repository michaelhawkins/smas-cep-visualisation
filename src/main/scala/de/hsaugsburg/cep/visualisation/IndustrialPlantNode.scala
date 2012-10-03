package de.hsaugsburg.cep.visualisation

import de.hsaugsburg.cep.model.ItemMovedEvent
import de.hsaugsburg.cep.model.WorkEvent
import de.hsaugsburg.cep.model.ItemsChangedEvent
import de.hsaugsburg.cep.model.ChangeType

/**
 * SMAS node that handles incoming events and delegates them to the 3D engine.
 * The handling does NOT include logical verification. For example a work item should only be
 * removed when it reaches the item exit element. This is not verified by this module, it simply
 * removes the item when it receives the event. Logical verification should be handled by the cep
 * agent.
 * Because of this simple errors like wrong sensor or item ids can lead to an inconsistent state.
 * Therefore the consistency also must be ensured by the event source (cep agent). Some errors
 * may be detected by the logging mechanism and should be visible in the logging window of the UI. 
 *
 * @author Benny
 */
//TODO implement SMAS node
class IndustrialPlantNode {

  def handleItemMovedEvent(event: ItemMovedEvent) {
    val target = IndustrialPlantApp.plant getSensorName event.targetId
    //    TODO log AssertError
    IndustrialPlantApp.plant moveWorkItem (event.itemId, target)
    //    TODO log event
  }

  def handleWorkEvent(event: WorkEvent) {
    //    TODO implement method to handle work event in IndustrialPlant class
    //    TODO log event
  }

  def handleItemsChangedEvent(event: ItemsChangedEvent) {
    //    TODO log event
  }
}