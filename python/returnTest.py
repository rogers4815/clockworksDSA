import android
import urllib
import urllib2
import socket

class DataSender:
    def __init__(self, port=8080, host="localhost"):
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.connect((host, port))

    def send(self, str):
        self.sock.sendall(str+"\n")



droid = android.Android()

sender = DataSender()

sender.send("Computing Pi from 0 to 100")

print("Computing Pi from 0 to 100")
import piSimulation
piStr = ""
for i in range(0,10):
    piStr+=str(piSimulation.compute_pi(0,10*i))
    sender.send("Progress: "+str(10*i)+"%")
print(piStr)

sender.send("Results:")
sender.send(piStr)

#mIntent = droid.getIntent().result
#Extras = mIntent["extras"]
#print(Extras)
##Input = Extras["serialinput"]

#resultData = "Returned from SL4a Script!"
#droid.setResultString("result", resultData)
##droid.setResultArray(Result_OK, Extras)
