/*
 *
 *  * Copyright (c) 2014-2016. National Institute of Advanced Industrial Science and Technology (AIST)
 *  * All rights reserved.
 *
 */

package jp.go.aist.cspe;

/**
 * Created by yoriyuki on 2016/08/10.
 */
abstract public class QeaMonitor {
    abstract boolean step(int event, int arg);
    abstract boolean step(int event, int arg1, int arg2);
}
