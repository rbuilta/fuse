/**
 * Copyright (C) 2010-2011, FuseSource Corp.  All rights reserved.
 *
 *     http://fusesource.com
 *
 * The software in this package is published under the terms of the
 * CDDL license a copy of which has been included with this distribution
 * in the license.txt file.
 */

package org.fusesource.fabric.apollo.amqp.broker

import org.fusesource.hawtdispatch._
import org.apache.activemq.apollo.util.Logging
import org.fusesource.fabric.apollo.amqp.codec.marshaller.v1_0_0.AmqpMarshaller
import org.fusesource.hawtbuf.AsciiBuffer.ascii
import org.fusesource.fabric.apollo.amqp.codec.types.TypeFactory._
import org.fusesource.fabric.apollo.amqp.protocol._
import org.fusesource.fabric.apollo.amqp.api._
import org.fusesource.fabric.apollo.amqp.api.Message
import org.apache.activemq.apollo.dto.DestinationDTO
import org.fusesource.fabric.apollo.amqp.codec.types.{AmqpTransfer, AmqpRole}
import org.apache.activemq.apollo.broker._
import scala.math._

/**
 * An AMQP message listener that produces message deliveries
 */
class AmqpDeliveryProducer(val handler:AmqpProtocolHandler, val link:Receiver, val destination:Array[DestinationDTO]) extends MessageListener with Logging {

  // TODO - check if null, if so check dynamic
  val addr = ascii(link.getAddress)

  val producer = new DeliveryProducerRoute(handler.host.router) {
    override def connection = Some(handler.connection)
    override def dispatch_queue = handler.dispatchQueue

    refiller = ^{
      //trace("Running refiller %s", transportRefiller)
      transportRefiller.run
    }
  }

  handler.connectDeliveryProducer(this)

  // TODO - should calculate how much credit to supply to get maximum throughput
  def needLinkCredit(available:Long) : Long = {
    if (producer.full) {
      0L
    } else {
      available.max(20)
    }
  }

  def offer(receiver:Receiver, message:Message) = {

    assert(receiver == link)

    val protoMessage = message.asInstanceOf[AmqpProtoMessage]
    //trace("Received message : %s", protoMessage);
    val delivery = new Delivery
    val message_transfer = new AmqpMessageTransfer(protoMessage, createAmqpString(link.getName), createAmqpString(link.getAddress))
    delivery.message = message_transfer
    delivery.size = message_transfer.size
    // TODO - when transactions are supported
    delivery.uow = null;

    if (!protoMessage.settled) {
      delivery.ack = { (consumed, uow) =>
        if (consumed) {
          //trace("Ack'ing message id %s", protoMessage.transfer_id);
          link.settle(message, Outcome.ACCEPTED)
        } else {
          //trace("Rejecting message id %s", protoMessage.transfer_id);
          link.settle(message, Outcome.REJECTED)
        }
      }
    }

    assert(!producer.full)
    producer.offer(delivery)
    if(producer.full) {
      //trace("Delivery producer full, flow-controlling client")
      link.drainLinkCredit;
      //handler.suspendRead("blocked destination: " + producer.overflowSessions.mkString(", "))
      false
    } else {
      val available = link.getAvailableLinkCredit
      if (available != null && available.longValue < 5) {
        link.addLinkCredit(20 - available.longValue)
      }
      true
    }
  }

  var refiller:Runnable = null

  def transportRefiller = refiller

  def refiller(r:Runnable) = {
    refiller = r
    //trace("Setting refiller to %s", refiller)
  }

  def full = producer.full
}
