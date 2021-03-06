#! /usr/bin/env python

import sys
from socket import *
import os
import requests

BUFSIZE = 1024

SERVERHOSTKEY = "SERVERHOST"
SERVERPORTKEY = "SERVERPORT"

serverHost = os.environ[SERVERHOSTKEY]
serverUrl = "/api/avatarService/v1/device/update/mpack"
serverUri = "%s%s" % (serverHost, serverUrl)

serverPort = int(os.environ[SERVERPORTKEY])

def main():
    server()


def server():
    print("used server: %s" % (serverUri))
    s = socket(AF_INET, SOCK_DGRAM)
    s.bind(('', int(serverPort)))
    print("udp proxy server ready on %s " % (serverPort))
    while 1:
        data, addr = s.recvfrom(BUFSIZE)
        print('server received %r from %r' % (data, addr))

        try:
            requests.post(url=serverUri, data=data, headers={'Content-Type': 'application/octet-stream'})
            print("posted data")
        except:
            print("post error")


main()
