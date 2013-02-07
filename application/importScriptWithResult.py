import android
import urllib
import urllib2

droid = android.Android()
#droid.makeToast('Import script!')

SCRIPT_URL = 'http://10.6.17.68/remoteScript2.py'
urllib.urlretrieve(SCRIPT_URL, '/mnt/sdcard/sl4a/scripts/remoteScript2.py')

import remoteScript2
print remoteScript2.run()
droid.makeToast(remoteScript2.run())

