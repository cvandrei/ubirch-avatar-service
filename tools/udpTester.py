import socket
import binascii

UDP_IP = "127.0.0.1"
# UDP_IP = "13.93.47.253"
# UDP_IP = "13.93.92.129" #demo@aws

UDP_PORT = 9090

# MESSAGE = bytes("Hello, World!", "utf-8")
MESSAGE = binascii.unhexlify(
    "01CEBC9AB239D952487256D0F44075AE8BA8F8A9997CC8745FC37978179E78215D5A4EE7A1FDEE49F3C2C0FDB08AE739582D271984EAD7AFC5805B5E5633CC360A0ED9F6BE35B10A7B2274656D7065726174757265223A33307D")

print("UDP target IP:", UDP_IP)
print("UDP target port:", UDP_PORT)
print("message:", MESSAGE)

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)  # UDP
sock.sendto(MESSAGE, (UDP_IP, UDP_PORT))
