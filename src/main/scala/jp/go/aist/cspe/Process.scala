/*
 *
 *  * Copyright (c) 2014-2016. National Institute of Advanced Industrial Science and Technology (AIST)
 *  * All rights reserved.
 *
 */

package jp.go.aist.cspe

import jp.go.aist.cspe.CSPE._

abstract class Process {

  private[cspe] def acceptPrim(e: AbsEvent): Process

  private[cspe] def canTerminatePrim : Boolean

  private[cspe] def choiceProcesses : List[Process] =  List(this)

  private[cspe] def toFailure : Option[FailureClass] = None

  // used for creation
  def ->:(e: AbsEvent): Process = new Prefix(e, this)

  def <+>(that: Process) = choice(List(this, that))

  def ||(sync: Set[Symbol]): PartialParallel
  = new PartialParallel(this, sync)

  def |||(that: Process) = parallel(List(this, that), Set.empty)

  def $(that: Process) = sequence(List(this, that))

  def |(as: Set[Symbol]) = new PartialInterrupt(this, as)

  def recover(errorHandler : PartialFunction[Any, Process]) : Process =
    new Recover(this, errorHandler)

  //used for verification
  def accept(e: AbsEvent): Process = this.acceptPrim(e)

  //def accept(e: AbsEvent) : Process = this.acceptPrim(e)

  final def isFailure = this match {
    case p : FailureClass => true
    case _ : Process => false
  }

  final def canTerminate = this.canTerminatePrim && ! this.isFailure

  //Accept Event
  def <<(e: AbsEvent): Process = this.accept(e)

  //Accept Events
  def |=(s: Traversable[AbsEvent]): Boolean =
    (this /: s) ((p, e) => p.acceptPrim(e)) canTerminatePrim

  def |~(s: Traversable[AbsEvent]): Boolean =
    ! ((this /: s) ((p, e) => p.acceptPrim(e)) isFailure)

  def equals (other: Any) : Boolean

  def hashCode(): Int
}