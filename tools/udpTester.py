import binascii
import socket
import time

UDP_IP = "127.0.0.1"
# UDP_IP = "13.93.47.253"
# UDP_IP = "13.93.92.129" #demo@aws
# UDP_IP = "13.80.77.86"  # ubirch-dev@azure
# UDP_IP = "23.101.65.255"  # ubirch-demo@azure
# UDP_IP = "34.251.87.35"
# UDP_IP = "udp.api.ubirch.dev.ubirch.com"

UDP_PORT = 7070

# MESSAGE = bytes("Hello, World!", "utf-8")
hexDatas = [
    "01CEB6877DCAD9519B36981A4D60010B14E7A80ED32919217F3B453D81C3AF2660EE31D7F6606D92FF0325FE39391FD234F20CAD6D7F6CDF27B71FEA73800301E0D25CD94C41AE0E7B2274656D70657261747572223A32337D",
    "01CEB6877DCAD94DDD3454577AF51B8BD0D7D0715AB9479626369F7D30D992A8FCC99474B9DE1466FF9DC567EC5CD411A632C5AEA6199F446A63CE5C605EFAE6091B49BC52A20A097B226C69636874223A3235357D"
]

print("UDP target IP:", UDP_IP)
print("UDP target port:", UDP_PORT)

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)  # UDP

while (True):
    for hexData in hexDatas:
        binData = binascii.unhexlify(hexData)
        print("send message:", binascii.hexlify(binData))
        sock.sendto(binData, (UDP_IP, UDP_PORT))
        time.sleep(5)
