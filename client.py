import socket
import threading
import json

server = "192.168.43.238"
port = 1234

def main():
	s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
	s.connect((server, port))

	msg_out = "Que show mi erick, ya está o que?"	
	s.sendto(msg_out.encode('utf-8'), (server, port))

	thread = threading.Thread(name="receiver", target=receiver, args=[s])
	thread.start()

	#s.close()



def receiver(s):
	while True:
		msg_in, address = s.recvfrom(1024)
		print(msg_in.decode('utf-8'))



if __name__ == '__main__':
	main()