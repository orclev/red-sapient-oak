package org.tenletters.controller

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.tenletters.model.QueueEntry
import java.time.Duration
import java.time.ZonedDateTime

/**
 * Created by kylemurphy on 4/2/16.
 */
@RestController
@RequestMapping(value = "/queue")
class QueueController {

  private val queue: MutableSet<QueueEntry> = mutableSetOf();

  @RequestMapping(method = arrayOf(RequestMethod.GET))
  fun getQueueEntries(): List<QueueEntry> {
    return sortQueue(ZonedDateTime.now())
  }

  @RequestMapping(value = "/{id}", method = arrayOf(RequestMethod.PUT))
  fun addQueueEntry(@PathVariable("id") id: Int, @RequestBody time: ZonedDateTime): QueueEntry {
    val entry = QueueEntry(id, time)
    synchronized(queue) {
      queue.add(entry)
    }
    return entry
  }

  @RequestMapping(value = "/pop", method = arrayOf(RequestMethod.POST))
  fun popQueue(): QueueEntry {
    val entry = sortQueue(ZonedDateTime.now()).firstOrNull() ?: throw EmptyQueueException()
    synchronized(queue) {
      queue.remove(entry)
    }
    return entry
  }

  @RequestMapping(value = "/{id}", method = arrayOf(RequestMethod.DELETE))
  @ResponseStatus(HttpStatus.NO_CONTENT)
  fun removeQueueEntry(@PathVariable("id") id: Int) {
    val entry = QueueEntry(id, ZonedDateTime.now())
    synchronized(queue) {
      queue.remove(entry)
    }
  }

  @RequestMapping(value = "/{id}", method = arrayOf(RequestMethod.GET))
  fun findPosition(@PathVariable("id") id: Int): Int {
    val sorted = sortQueue(ZonedDateTime.now())
    val entry = QueueEntry(id, ZonedDateTime.now())
    if(!sorted.contains(entry)) {
      throw EntryNotFoundException(id)
    }
    return sorted.indexOf(entry)
  }

  @RequestMapping(value = "/wait", method = arrayOf(RequestMethod.GET))
  fun findWait(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("time")
      now: ZonedDateTime): Long {
    return queue.map { entry -> Math.abs(Duration.between(now, entry.added).seconds) }.sum()
        .div(Math.max(queue.size, 1))
  }

  fun sortQueue(now : ZonedDateTime): List<QueueEntry> {
    synchronized(queue) {
      val queues = queue.groupBy { it.getEntryType() == QueueEntry.EntryType.Manager }
      val managers = queues.get(true).orEmpty().sortedWith(QueueEntry.QueueComparator(now))
      val nonManagers = queues.get(false).orEmpty().sortedWith(QueueEntry.QueueComparator(now))
      return managers.plus(nonManagers)
    }
  }
}