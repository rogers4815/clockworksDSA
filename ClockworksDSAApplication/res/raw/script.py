import android
import urllib
import urllib2
import socket
import time

import socket

class DataSender:
    def __init__(self, port=8080, host="localhost"):
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.connect((host, port))

    def send(self, str):
        self.sock.sendall(str+"\n")

sender = DataSender()

droid = android.Android()
droid.makeToast("Running script")

sender.send("Running script")

sender.send("Waiting 5 seconds")
droid.makeToast("Waiting 5 seconds")
time.sleep(5)


#import piSimulation
#droid.makeToast("Pi: " + str(piSimulation.compute_pi(0, 100)))

droid.vibrate(300)

sender.send("Script Finished")
