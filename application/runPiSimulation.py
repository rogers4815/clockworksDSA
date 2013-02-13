import android
import urllib
import urllib2

droid = android.Android()
#droid.makeToast('Import script!')

SCRIPT_URL = 'http://192.168.1.101/piSimulation.py'
print("Downloading script "+SCRIPT_URL)
urllib.urlretrieve(SCRIPT_URL, '/mnt/sdcard/sl4a/scripts/piSimulation.py')

print("Computing Pi from 0 to 1000")
import piSimulation
print piSimulation.compute_pi(0,1000)


