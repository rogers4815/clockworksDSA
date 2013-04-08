# ClockworksDSA

Clockworks distributed simulation app for android devices

## Contents

* [Server](#server)
  * [User Environment Listener](#user-environment-listener)
  * [User Result Assembly](#user-result-assembly)
  * [Bot Interface Listener](#bot-interface-listener)
  * [Timer / Timeout Handlers](#timer--timeout-handlers)
* [Bot](#bot)
  * [Bot Network Interface](#bot-network-interface)
* [API Spec](#api-spec)
  * [WOP Ping](#wop-ping-waiting-on-process)
  * [RTP Ping](#rtp-ping-ready-to-process)
  * [RTO Ping](#rto-ping-reset-time-out)
  * [Environment](#environment)
  * [Environment Segment](#environment-segment)
* [Appendix](#appendix)
  * [Flowchart Key](#flowchart-key)
  * [Code Queue](#code-queue)

#Server

## User Environment Listener

* Get [environments](#environment) from users over HTTP.
* Split code into all permutations of variable categories given and add to [Code Queue](#code-queue)
* Respond to users with [rejects/acknowledgements](#environment-response)


### Shared variable accesses:
* [Code queue](#code-queue): Add to Queue

[Top](#contents)

![SNUIL](WikiImages/ServerNetworkUserInterfaceListenerSNUIL.png?raw=true)

## User Result Assembly

* Listen for [WOP Pings](#wop-ping-waiting-on-process) from users over HTTP.
* Check if [Environment](#environment) is complete with all results
* Respond to users with [rejects/acknowledgement/compiled results](#wop-ping-response)


[Top](#contents)

![SNURAL](WikiImages/ServerNetworkUserResultAssemblyLine.png?raw=true)

## Bot Interface Listener

* Listen for HTTP request from devices
* If [RTP Ping](#rtp-ping-ready-to-process): 
 * Strip any results from ping and add to Segment
 * [Respond](#rtp-response)
* If [RTO Ping](#rto-ping-reset-time-out): 
 * Restart the segment's [timer](#timer--timeout-handlers)

### Shared variable accesses:
* [Code Queue](#code-queue): Take off Queue if available

[Top](#contents)

## Timer / Timeout handlers

* One process for each segment of the simulation [Environment](#environment)

### Shared variable accesses:

* [Code Queue](#code-queue): Re-add segment to Queue if time out interval reached 

[Top](#contents)

![SNAIL](WikiImages/ServerNetworkAppInterfaceListenerSNAIL.jpeg?raw=true)

# Bot

## Bot Network Interface

* [Ping](#rtp-ping-ready-to-process) server to indicate readiness to process
* Process python script

[Top](#contents)

![Android Network Interface](WikiImages/AndroidNetworkInterface.jpeg?raw=true)

# API Spec

## WOP Ping (Waiting On Process)

* Sent from the user after every t seconds to check whether all results have been collated

<pre>
GET /resultassemblyhandler HTTP/1.1
Host: www.example.com
Content-Type: text/plain-text; charset=utf-8
Content-Length: length
Environment-Id: 0
</pre>

### WOP Ping Response:

* 102: Process Not ready

<pre>
 
</pre>

* 200: Results

<pre>
[
	{
		"params":[
			"param1",
			"rap"
		],
		"results" : "result",
		"script-valid":true
	},
	{
		"params":[
			"param1",
			"rap"
		],
		"results" : "result",
		"valid":true
	}
]
</pre>

* 404: Process not found

<pre>
 
</pre>

[Top](#contents)

## RTP Ping (Ready To Process)

* Contains results from a previously sent process if such a process exists
* Expects response of a new process

<pre>
GET /botrequesthandler HTTP/1.1
Host: www.example.com
Content-Type: text/plain-text; charset=utf-8
Content-Length: length
Environment-Id: 0
Segment-Id: 0
May contain traces of results
</pre>

### RTP Ping Response:

* 200: Response with code to run

<pre>
{
	"script" : "script"
}
</pre>

* 204: No code available at this time from Queue

<pre>

</pre>

[Top](#contents)

## RTO Ping (Reset Time Out)

* Tell the server to reset its process timer for the indicated segment

<pre>
POST /botrequesthandler HTTP/1.1</br>
Host: www.example.com</br>
Content-Type: text/plain-text; charset=utf-8</br>
Content-Length: length</br>
Environment-Id: 0</br>
Segment-Id: 0
</pre>

### RTO Ping Response:

* 200: Success

<pre>

</pre>

* 404: Process Not Found

<pre>

</pre>

[Top](#contents)

## Environment

* The user-defined simulation
* Creation defines the [Environment Segments](#environment-segment) for this simulation

[Top](#contents)

## Environment Segment

* Segment of a simulation

[Top](#contents)

#Appendix

## Flowchart Key

* Yellow: Start
* Green: Data received
* Red: Response sent
* Black: Segments created

[Top](#contents)

## Code Queue

* Standard Queue of code objects that allows the threads to operate concurrently

[Top](#contents)
