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
  * [WOP Ping](#wop-ping)
  * [RTP Ping](#rtp-ping)
  * [Environment](#environment)
  * [Process](#process)
* [Appendix](#appendix)
  * [Flowchart Key](#flowchart-key)

#Server

## User Environment Listener

* Listen for environment simulations from users over HTTPS, authentication is optional
* Examine Ping Queue (the number of devices that have pinged the server recently)
* Examine Code Queue (the number of objects currently waiting to be distributed)
* Split code based on number of available devices, (i.e. split amout is minimum of: pings received in the previous allowed window, space in queue and how much the code can be split) and add to code queue
* Respond to users with rejects/acknowledgements

### Database accesses:
* User authentication

### Shared variable accesses:
* Ping queue: read
* Code queue: read/write

[Top](#contents)

![SNUIL](WikiImages/ServerNetworkUserInterfaceListenerSNUIL.jpeg?raw=true)

## User Result Assembly

* Listen for [WOP](#wop-ping) pings from users over HTTPS, authentication is optional
* Check is database complete (all results returned)
* Respond to users with rejects/acknowledgement/compiled results


### Database accesses:
* User authentication
* Results: read/purge

[Top](#contents)

![SNURAL](WikiImages/ServerNetworkUserResultAssemblyLine.jpeg?raw=true)

## Bot Interface Listener

* Listen for data from devices over HTTP Post (results piggybacked on Ready To Process ping)
* If RTP (Ready to process) ping: device is available to receive object file
* If I’m still here ping: where device is still processing data, signals the server to restart the timer
* Send process files if any code in queue
* HTTP Response: timer information piggybacked with .py file
* Send vacancy response if no code in queue

### Database accesses:
* Result logging: read/write

### Shared variable accesses:
* Ping queue: read/write
* Code queue: read/write

[Top](#contents)

## Timer / Timeout handlers

* One for each portion of the simulation

### Shared variable accesses:

* Code queue: write 

[Top](#contents)

![SNAIL](WikiImages/ServerNetworkAppInterfaceListenerSNAIL.jpeg?raw=true)

# Bot

## Bot Network Interface

[Top](#contents)

![Android Network Interface](WikiImages/AndroidNetworkInterface.jpeg?raw=true)

# API Spec

## WOP Ping

[Top](#contents)

## RTP Ping

[Top](#contents)

## Environment

[Top](#contents)

## Process

[Top](#contents)

#Appendix

## Flowchart Key

* Yellow: Start
* Green: Data received
* Red: Response sent
* Blue: Database access

[Top](#contents)
