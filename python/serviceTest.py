#!/usr/bin/env python

import socket

class DataSender:
    def __init__(self, port=8080, host="localhost"):
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.connect((host, port))

    def send(self, str):
        self.sock.sendall(str+"\n")

sender = DataSender()
sender.send("Test");
sender.send("Other test");
