import android
import urllib
import urllib2
import socket
import time

droid = android.Android()
droid.makeToast("Running script")

droid.makeToast("Waiting 10 seconds")
time.sleep(10)


#import piSimulation
#droid.makeToast("Pi: " + str(piSimulation.compute_pi(0, 100)))

droid.vibrate(300)
