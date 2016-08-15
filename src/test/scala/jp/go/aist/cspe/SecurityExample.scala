/*
 *
 *  * Copyright (c) 2014-2016. National Institute of Advanced Industrial Science and Technology (AIST)
 *  * All rights reserved.
 *
 */

package jp.go.aist.cspe
import jp.go.aist.cspe.CSPE._
import sun.security.krb5.Credentials

/**
  * Created by yoriyuki on 2016/08/08.
  */
object SecurityExample extends ExampleTrait {

  def genEventStream(n: Int) = List(Event('Req, 0, 1), Event('MakeResAvailable, 1), Event('BecomeResAvailable, 1),
    Event('Granted, 1), Event('Access, 0, 1), Event('Spawn, 0, 1), Event('Access, 1, 1), Event('Exit, 1),
    Event('Release, 0, 1), Event('ReleaseRes, 1)).take(n)

  def authServer(credentials : Set[(Int, Int)]) : Process = ?? {
    case Event('Req, pid: Int, credential: Int) if ! credentials(pid, credential) =>
      authServer(credentials + ((pid, credential))) |||
        Event('MakeResAvailable, credential) ->: Event('BecomeResAvailable, credential) ->:
        Event('Granted, credential) ->: SKIP
    case Event('Release, pid: Int, credential: Int) if credentials(pid, credential) =>
      authServer(credentials - ((pid, credential))) ||| Event('ReleaseRes, credential) ->: SKIP
  }

  def resource(credentials_asked: Set[Int], credentials_granted: Set[Int]) : Process = ?? {
    case Event('MakeResAvailable, credential: Int) => resource(credentials_asked + credential, credentials_granted)
    case Event('BecomeResAvailable, credential : Int) if credentials_asked(credential) =>
      resource(credentials_asked - credential, credentials_granted + credential)
    case Event('ReleaseRes, credential: Int) if credentials_granted(credential) =>
        resource(credentials_asked, credentials_granted - credential)
    case Event('Access, pid: Int, credential: Int) if credentials_granted(credential) =>
        resource(credentials_asked, credentials_granted)
  }

  def client(pid: Int, credentials: Set[Int]): Process = ?? {
    case Event('Spawn, `pid`, child_pid: Int) => client(pid, credentials) ||| client(child_pid, credentials)
    case Event('Exit, `pid`) => SKIP
    case Event('Req, `pid`, credential: Int) =>
      Event('Granted, credential) ->: client(pid, credentials + credential) $
        Event('Release, `pid`, credential) ->: client(pid, credentials)
    case Event('Access, `pid`, credential: Int) if credentials(credential) => client(pid, credentials)
  }

  def server = (authServer(Set()) || Set('MakeResAvailable, 'BecomeResAvailable, 'ReleaseRes) || resource(Set(), Set()))

  def system = server || Set('Req, 'Granted, 'Access, 'Release) || client(0, Set())

  def createCSPEModel() : Process = system
  def debugCSPEModel() = {
    val monitors = new ProcessSet(List(system))

    assert(! monitors.isFailure)
    assert(! (monitors |~ List(Event('Access, 4))))
    assert(monitors |~  List())
    assert(monitors |~  genEventStream(9))
  }

  def createQeaModel(): QeaMonitor = new StubQeaMonitor()
  def debugQeaModel() = {}

  def symbolMap = Map('Req -> 1, 'MakeResAvailable -> 2, 'Granted -> 3, 'Release -> 4, 'BecomeResAvailable -> 5,
  'ReleaseRes -> 6, 'Access -> 7, 'Spawn ->8, 'Exit -> 9)
}

