import android
import urllib
import urllib2

droid = android.Android()

print("Computing Pi from 0 to 1000")
import piSimulation
pi=piSimulation.compute_pi(0,100)
print(pi)

mIntent = droid.getIntent().result
Extras = mIntent["extras"]
print(Extras)
#Input = Extras["serialinput"]

resultData = "Returned from SL4a Script!"
droid.setResultString("result", resultData)
#droid.setResultArray(Result_OK, Extras)
