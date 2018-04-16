import socket
import threading
import json

server = "127.0.0.1"
port = 1234

def main():
	s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
	s.connect((server, port))

	msg = "Que show mi erick, ya está o que?"	
	s.sendto(msg.encode('ascii'))

	s.close()

if __name__ == '__main__':
	main()