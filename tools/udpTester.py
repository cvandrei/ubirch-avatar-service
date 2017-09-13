import socket
import binascii

UDP_IP = "127.0.0.1"
# UDP_IP = "13.93.47.253"
UDP_PORT = 9090

# MESSAGE = bytes("Hello, World!", "utf-8")
MESSAGE = binascii.unhexlify(
    "01CEBC9AB239D94CF4ABA72EDA90D506D8B0586DAA71BCBBB3B6FE02807BAD6B0221FE604A4F0C538A9D53BA7D0AD1E1CB1E98193AAA1F140E4942AD782643C68D1477FDD86048047B226C69676874223A34397D")

print("UDP target IP:", UDP_IP)
print("UDP target port:", UDP_PORT)
print("message:", MESSAGE)

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)  # UDP
sock.sendto(MESSAGE, (UDP_IP, UDP_PORT))
