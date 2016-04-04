# ZWave Controller

Runs using an Aeon Labs S2 stick and tested with Aeon Labs Smart Switch 6 Gen 5.

Used <https://github.com/whizzosoftware/WZWave>
Its an Java based RXTX implementation requiering minimal host OS adaptions.
Note that at least for the S2/SmartSwitch version 0.0.3 did not work but 0.0.4-SNAPSHOT did.

###On Mac OSX
Donwload `librxtxSerial.jnilib` from
<http://jlog.org/rxtx-mac.html>
and place it in `/Library/Java/Extensions`

###On Linux/Raspberry PI
`sudo apt-get install librxtx-java`

Then you need to add an environment variable.

`export JAVA_OPTS=-Djava.library.path=/usr/lib/jni`

###Pairing the S2 Dongle with the Smart Switch
Remove the dongle from the USB port. Go to where the smart switch is plugged in to the wall.
On the S2 press and hold the button for 3 seconds. It should start a rapid blink.
More information on pairing can be found here. <http://wiki.micasaverde.com/index.php/ZWave_Add_Device>
Note that Its and reset but just describing the most robust case to start fresh.
 
On the Smart Switch there is a small indentation on the lower right corner. Press it for 3 seconds.
Now both blink then after a couple of seconds both should discover each other and have a solid light.

###Building
`gradlew distZip`

or using IntelliJ

Creates a **zwacectrl.zip**
Unpack and run `bin/zwavectrl`

###Test Main
The Program included is more to just see everything is in order.
It will try to detect a Switch on the ZWave network, then order the switch to turn on.
After a 5 second timer it will turn the switch off.
That is it but its a good base to build more.

If you have creative ideas please comment.
<http://smarthome.elcris.com>