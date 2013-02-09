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
* [Ping queue](#ping-queue): read
* [Code queue](#code-queue): read/write

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
* [Ping Queue](#ping-queue): write
* [Code Queue](#code-queue): read

[Top](#contents)

## Timer / Timeout handlers

* One for each portion of the simulation

### Shared variable accesses:

* [Code Queue](#code-queue): write 

[Top](#contents)

![SNAIL](WikiImages/ServerNetworkAppInterfaceListenerSNAIL.jpeg?raw=true)

# Bot

## Bot Network Interface

[Top](#contents)

![Android Network Interface](WikiImages/AndroidNetworkInterface.jpeg?raw=true)

# API Spec

## WOP Ping (Waiting On Process)

### WOP Ping Response:

[Top](#contents)

## RTP Ping (Ready To Process)

* Contains results from a previously sent process if such a process exists
* Expects response of a new process

<code>
Sample RTP Ping Here
</code>

### RTP Ping Response:

* Response with code to run

<code>
Sample Response of this type here
</code>

* No code available at this time

<code>
Sample Response of this type here
</code>

[Top](#contents)

## RTO Ping (Reset Time Out)

* Tell the server to reset its process timer for the indicated process

### RTO Ping Response:

* None

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

[Top](#contents)

## Ping Queue

[Top](#contents)

## Code Queue

[Top](#contents)
