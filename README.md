# ClockworksDSA

Clockworks distributed simulation app for android devices

## Contents

* [Server](#server)
  * [User Environment Listener](#user-environment-listener)
  * [User Result Assembly](#user-result-assembly)
  * [Bot Interface Listener](#bot-interface-listener)
  * [Timer / Timeout Handlers](#timer--timeout-handlers)
  * [Database Schema](#database-schema)
* [Bot](#bot)
  * [Bot Network Interface](#bot-network-interface)
* [API Spec](#api-spec)
  * [WOP Ping](#wop-ping-waiting-on-process)
  * [RTP Ping](#rtp-ping-ready-to-process)
  * [RTO Ping](#rto-ping-reset-time-out)
  * [Environment](#environment)
* [Appendix](#appendix)
  * [Flowchart Key](#flowchart-key)
  * [Ping Queue](#ping-queue)
  * [Code Queue](#code-queue)

#Server

## User Environment Listener

* Listen for [environments](#environment) from users over HTTPS, authentication is optional
* Get number of recent devices from [Ping Queue](#ping-queue).
* Get space in [Code Queue](#code-queue).
* Split code based on number of available devices, (i.e. split amout is minimum of: pings received in the previous allowed window, space in queue and how much the code can be split) and add to [code queue](#code-queue)
* Respond to users with [rejects/acknowledgements](#environment-response)

### Database accesses:
* User authentication

### Shared variable accesses:
* [Ping queue](#ping-queue): Pop off expired and read size
* [Code queue](#code-queue): Check if full and add to Queue

[Top](#contents)

![SNUIL](WikiImages/ServerNetworkUserInterfaceListenerSNUIL.jpeg?raw=true)

## User Result Assembly

* Listen for [WOP Pings](#wop-ping-waiting-on-process) from users over HTTPS, authentication is optional
* Check if database complete with all results
* Respond to users with [rejects/acknowledgement/compiled results](#wop-ping-response)


### Database accesses:
* User authentication
* Results: read/purge

[Top](#contents)

![SNURAL](WikiImages/ServerNetworkUserResultAssemblyLine.jpeg?raw=true)

## Bot Interface Listener

* Listen for HTTP request from devices
* If [RTP Ping](#rtp-ping-ready-to-process): 
 * Strip any results from ping and enter in database
 * Log to [Ping Queue](#ping-queue)
 * [Respond](#rtp-response)
* If [RTO Ping](#rto-ping-reset-time-out): 
 * Restart the processes [time out](#timer--timeout-handlers)

### Database accesses:
* Result logging: write

### Shared variable accesses:
* [Ping Queue](#ping-queue): Add to Queue
* [Code Queue](#code-queue): Take off Queue if available

[Top](#contents)

## Timer / Timeout handlers

* One for each process of the simulation

### Shared variable accesses:

* [Code Queue](#code-queue): Add to Queue if time out reached 

[Top](#contents)

![SNAIL](WikiImages/ServerNetworkAppInterfaceListenerSNAIL.jpeg?raw=true)

## Database Schema
### Authentication
<table>
 <tr>
  <td>
   Sample
  </td>
  <td>
   Sample
  </td>
  <td>
   Sample
  </td>
 </tr>
</table>
### Environment
<table>
 <tr>
  <td>
   Sample
  </td>
  <td>
   Sample
  </td>
  <td>
   Sample
  </td>
 </tr>
</table>
### Process
<table>
 <tr>
  <td>
   Sample
  </td>
  <td>
   Sample
  </td>
  <td>
   Sample
  </td>
 </tr>
</table>

[Top](#contents)

# Bot

## Bot Network Interface

[Top](#contents)

![Android Network Interface](WikiImages/AndroidNetworkInterface.jpeg?raw=true)

# API Spec

## WOP Ping (Waiting On Process)

* Sent from the user after every t seconds to check if the results have been collected

<code>
Sample here
</code>

### WOP Ping Response:

* 102: Process Not ready

<code>
Sample
</code>

* 200: Results

<code>
Sample
</code>

* 401: Authentication failure

<code>
Sample
</code>

* 404: Process not found

<code>
Sample
</code>

[Top](#contents)

## RTP Ping (Ready To Process)

* Contains results from a previously sent process if such a process exists
* Expects response of a new process

<code>
Sample RTP Ping Here
</code>

### RTP Ping Response:

* 200: Response with code to run

<code>
Sample Response of this type here
</code>

* 204: No code available at this time from Queue

<code>
Sample Response of this type here
</code>

[Top](#contents)

## RTO Ping (Reset Time Out)

* Tell the server to reset its process timer for the indicated process

<code>
Sample
</code>

### RTO Ping Response:

* 200: Success

<code>
Sample
</code>

* 404: Process Not Found

<code>
Sample
</code>

[Top](#contents)

## Environment

### Environment Response:

[Top](#contents)

#Appendix

## Flowchart Key

* Yellow: Start
* Green: Data received
* Red: Response sent
* Blue: Database access
* Black: Scary Stuff

[Top](#contents)

## Ping Queue

* On read:
 * Pop expired pings of the top of the Queue and update size, then read size
* On write:
 * Simply add ping log to the Queue

[Top](#contents)

## Code Queue

* Standard Queue of code objects that allows the threads to operate concurrently

[Top](#contents)
