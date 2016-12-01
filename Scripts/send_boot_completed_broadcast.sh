#!/bin/bash
adb shell am broadcast -a android.intent.action.BOOT_COMPLETED -n de.hsrm.derns002.dsmoa.service/.BootReceiver
