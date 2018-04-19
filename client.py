import socket
import threading
import json
import sys

server = "192.168.43.238"
port = 1234

def main():
	s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
	s.connect((server, port))

	thread = threading.Thread(name="receiver", target=receiver, daemon=True, args=[s])
	thread.start()

	print("-------- Bienvenido al minichat -------\n")

	while True:
		line = input()

		if(len(line) == 0):
			continue

		line = line.split(' ', 1)

		if(line[0] == "exit"):
			s.close()
			exit()

		elif(line[0] == "list_commands"):
			print("listing commands")

		elif(line[0] == "get_users"):
			print("users")

		elif(line[0] == "set_name"):
			print("settting name")

		elif(line[0] == "send_message"):
			print("sending message")

		else:
			print("command not recognized")


	
	#msg_out = {
	#	'type': "setName",
	#	'data': {
	#				'to': "yourmom",
	#				'content': "migueloco"
	#			}
	#}

	#s.sendto(json.dumps(msg_out).encode('utf-8'), (server, port))

	
	s.close()
	exit()

def 


def receiver(s):
	while True:
		msg_in, address = s.recvfrom(1024)
		json_in = json.loads(msg_int.decode('utf-8'))	
		print(json.dumps(json_in))
		print(address)




if __name__ == '__main__':
	main()