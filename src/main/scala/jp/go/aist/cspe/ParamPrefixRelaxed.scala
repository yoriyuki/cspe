/*
 *
 *  * Copyright (c) 2014-2016. National Institute of Advanced Industrial Science and Technology (AIST)
 *  * All rights reserved.
 *
 */

package jp.go.aist.cspe
import jp.go.aist.cspe.CSPE._

private[cspe] class ParamPrefixRelaxed(f0: PartialFunction[AbsEvent, Process], id0 : Int) extends Process {
  val id = id0
  private val f = f0
  override def acceptPrim(e: AbsEvent): ProcessSet =
    processSet(List(if (f.isDefinedAt(e)) f(e) else this)) // note 'this' instead of 'Failure'

  override def canTerminate = false

  def canEqual(other: Any): Boolean = other.isInstanceOf[ParamPrefix]

  override def equals(other: Any): Boolean = other match {
    case that: ParamPrefix =>
      (that canEqual this) &&
        id == that.id
    case _ => false
  }

  override def hashCode(): Int = {
    id
  }
}
