package de.hsaugsburg.cep.visualisation

import de.hsaugsburg.cep.model._
import IndustrialPlantNode.logger
import model.MachineElement
import org.slf4j.LoggerFactory

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
// TODO implement SMAS node
class IndustrialPlantNode {

  def handleItemMovedEvent(event: ItemMovedEvent) {
    val source = IndustrialPlantApp.plant getSensorById event.sourceId
    val target = IndustrialPlantApp.plant getSensorById event.targetId
    //    TODO log AssertError
    source.moveItem(target)
    logger.info("Item " + event.itemId + " moved from " +
      event.sourceId + " to " + event.targetId)
  }

  def handleWorkEvent(event: WorkEvent) {
    // TODO Log invalid id error
    val machine = IndustrialPlantApp.plant getMaschine event.workerId
    event.work match {
      case Work.Begin =>
        machine.asInstanceOf[MachineElement].beginWork()
        logger info "Begin work at " + event.workerId
      case Work.End =>
        machine.asInstanceOf[MachineElement].endWork()
        logger info "End work at " + event.workerId
    }
  }

  def handleItemsChangedEvent(event: ItemsChangedEvent) {
    event.changeType match {
      case ChangeType.Added =>
        IndustrialPlantApp.plant addWorkItem event.itemId
        logger info "Added new work item " + event.itemId
      case ChangeType.Removed =>
        IndustrialPlantApp.plant removeWorkItem event.itemId
        logger info "Removed work item " + event.itemId
    }
  }
}

object IndustrialPlantNode {
  private val logger = LoggerFactory.getLogger(classOf[IndustrialPlantNode])
}