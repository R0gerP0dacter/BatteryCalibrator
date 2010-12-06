Battery Calibrator
========
This is a project to help properly calibrate the batteries on the Nexus One.

Contributors
========
Jon Richards

Roger Podacter

theloginwithnoname

mtw4991

Cosmin Bizon

All those who tested for us: http://forum.xda-developers.com/showthread.php?t=765609

Requirements
========
You must be running a kernel with the modified ds2784_battery.c driver.  Current kernels supported:

1.  Pershoot kernel
2.  Cyanogen kernel
3.  Wildmonks kernel

Installation
========
Currently there are a couple of ways to install the application:

1.  Download the latest binary of the application from <a href="http://jonrichards.net/xda/battery_calibrator/">http://jonrichards.net/xda/battery_calibrator/</a>.
2.  Fork the project and build the source code on your own.

Soon we will put the application up on the Android Market.

How-To
========

Originally Posted by mtw4991

This is what I do to achieve learn mode and calibrate my battery & it works every time....

1. Use the battery app to do the following:
a. set your age to 100% using the battery app under the Learn Prep tab
b. set your full40 to 1452 using the app in the same tab (i.e. stock capacity of OEM battery)

2. Navigate to sys>devices>platform>ds2784-battery>setreg>long press>open in text editor, then:
a. set your aEvolts to 3201 (type 0x66 a4 in setreg & save/exit)
b. set your minimum stop charging current to <20mA (type 0x65 06 in setreg & save/exit)
c. set a temporary capacity value in setreg (type 0x10 04 in setreg This bumps up capacity to 35-40% so phone doesn't die prior to reaching 3201mV which is the minimum mV before auto-shutdown)

3. Achieving Learn Mode with the app:
a. NOTE: to hit learn mode you must keep your current mA above -200mA draw! (turn on pandora+flashlight+wifi hotspot,etc but make sure the curr.(mA) is >200mA in Learn Mode tab)
b. turn learn mode on in Learn Mode tab
c. wait for mV to drop to <3201mV (learn popup will appear & learnf button will light up)
d. insert charger QUICKLY! (You will see a message saying Learn Mode is active on Learn Mode tab.)
e. turn off pandora, etc and close any open apps.
f. put phone into airplane mode so that you don’t get unexpected current draw near the full point.
g. charge to full (0x81 will show in battery status register when current drops below <20mA)
h. unplug and reboot, your new age should be set automatically. Learn is now complete and your phone should now charge to 100% and die at 0-1%.

4. Learn Failure:
If your new age shows 94% upon rebooting, then learn mode got interrupted or failed and you need to do it again, paying close attention as charging nears 80% and above. This is where learn mode can be lost by rogue apps, auto-updates, calls, etc pulling the current down below the minimum.

Note: as current gets close to <50-60mA don't run any apps or turn off airplane mode or you may artifically increase the current draw pulling it below 20mA and it will end the learn cycle prematurely. Airplane mode helps prevent calls/texts/twitters etc that can show up...happened to me @ 27mA! Also, expect it to take 2.5-3 hours to fully charge and complete the learn cycle. Patience peoples...
If I've left anything out guys, please add to it.

Note2: Learn mode cannot be achieve with the phone off. Leave the phone on until learn is complete and the battery status register shows 0x81. Done!

Technical Information
========

1.  Thread on XDA:  http://forum.xda-developers.com/showthread.php?t=765609
2.  DS2784 data sheet:  http://datasheets.maxim-ic.com/en/ds/DS2784.pdf
3.  Dr. Battery thread (inspiration for this project):  http://discussion.treocentral.com/homebrew-apps/260947-dr-battery.html

Licensing
========
This project is licensed under the WTFPL license.  See the included LICENSE file for more information.