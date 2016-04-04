package com.elcris.iot.zwave;
/*
 * The MIT License (MIT)
 * Copyright (c) 2016 Kristoffer Gronowski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import com.whizzosoftware.wzwave.controller.ZWaveControllerListener;

import com.whizzosoftware.wzwave.commandclass.BinarySwitchCommandClass;
import com.whizzosoftware.wzwave.controller.netty.NettyZWaveController;
import com.whizzosoftware.wzwave.node.ZWaveEndpoint;
import com.whizzosoftware.wzwave.node.specific.BinaryPowerSwitch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZWaveCtrl implements ZWaveControllerListener {
    private NettyZWaveController controller;
    private final String devicePath;
    private BinaryPowerSwitch mySwitch;
    static {
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "debug");
        log = LoggerFactory
                .getLogger(ZWaveCtrl.class);
    }
    private static final Logger log;

    public ZWaveCtrl(String device) {
        devicePath = device;
        controller = new NettyZWaveController(devicePath);
        controller.setListener(this);
        controller.start();
    }

    public static void main(String[]args) throws InterruptedException {
        log.info("Started on OS = {},{},{}", System.getProperty("os.name"),System.getProperty("os.arch"),
                System.getProperty("os.version"));

        ZWaveCtrl ctrl;
        if("Mac OS X".equals(System.getProperty("os.name"))) {
            ctrl = new ZWaveCtrl("/dev/tty.SLAB_USBtoUART");
            log.info("Running on Mac!");
        } else if("Linux".equals(System.getProperty("os.name"))){
            ctrl = new ZWaveCtrl("/dev/ttyUSB0");
            log.info("Running on Linux!");
        } else {
            log.error("Not supported Architecture");
            System.exit(0);
            return;
        }

        new Thread (new Runnable() {
            @Override
            public void run() {
                try {
                    ctrl.toggleLight(true); //False test
                    while( !ctrl.isLightDiscovered() ) {
                        log.info("No light discovered --- sleep 50ms");
                        Thread.sleep(50);
                    }
                    log.info("Light discovered !!!");

                    ctrl.toggleLight(true);

                    Thread.sleep(5000);
                    ctrl.toggleLight(false);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void toggleLight(boolean on) {
        if( mySwitch != null ) {
            controller.sendDataFrame(BinarySwitchCommandClass.createSetv1(mySwitch.getNodeId(), on));
            log.info("Light --- {} : switch : {}",on,mySwitch);
        }
    }

    public void stop() {
        controller.stop();
    }

    public String getDevicePath() { return devicePath; }

    public boolean isLightDiscovered() { return mySwitch != null; }

    @Override
    public void onZWaveNodeAdded(ZWaveEndpoint node) {
        log.info("Z-Wave node added: {}", node.getNodeId());
        if( node instanceof BinaryPowerSwitch) {
            mySwitch = (BinaryPowerSwitch) node;
        }
    }

    @Override
    public void onZWaveNodeUpdated(ZWaveEndpoint node) {
        log.info("Z-Wave node updated: {}",node.getNodeId());
    }

    @Override
    public void onZWaveConnectionFailure(Throwable t) {
        log.info("Something bad happened: {}",t);
    }
}