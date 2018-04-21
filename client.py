import socket
import threading
import json
import sys

server = '127.0.0.1'#'192.168.43.238'
port = 1234
LIST_USERS = 'list_users'
MESSAGE_RECEIVED = 'message_received'

def main():
	s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
	s.connect((server, port))

	thread = threading.Thread(name='receiver', target=receiver, daemon=True, args=[s])
	thread.start()

	print('-------- Welcome to the greatest minichat -------')
	print_commands()

	while True:
		line = input('>>')

		if(len(line) == 0):
			continue

		line.strip()
		line = line.split(' ', 1)

		req = {}

		if(line[0] == 'exit' or line[0] == 'e'):
			req['type'] = 'exit'
			send_request(s, req)
			break

		elif(line[0] == 'list_commands' or line[0] == 'lc'):
			print_commands()

		elif(line[0] == 'list_users' or line[0] == 'lu'):
			req['type'] = 'list_users'
			send_request(s, req)

		elif(line[0] == 'set_name' or line[0] == 'sn'):
			if(len(line) < 2):
				print('ERROR -> \'set_name\' command expects 1 argument')
				continue
			req['type'] = 'set_name'
			req['data'] = {}
			req['data']['content'] = line[1]
			send_request(s, req)	

		elif(line[0] == 'send_message' or line[0] == 'sm'):
			if(len(line) < 2):
				print('ERROR -> \'send_message\' command expects 3 arguments')
				continue	
			line[1].strip()
			line[1] = line[1].split(' ', 1)

			if(len(line[1]) < 2):
				print('ERROR -> \'send_message\' command expects 3 arguments')
				continue

			req['type'] = 'send_message'
			req['data'] = {}
			req['data']['to'] = line[1][0]
			req['data']['content'] = line[1][1]
			send_request(s, req)

		else:
			print('command not recognized')

	s.close()
	exit()

def send_request(s, req):
	s.sendto(json.dumps(req).encode('utf-8'), (server, port))

def print_commands():
	print('list_commands \t\t\t\t\tprints all the available commands in the chat')
	print('exit \t\t\t\t\t\texit from the chat')
	print('set_name [username] \t\t\t\tset your chat username')
	print('send_message all|[username] [message] \t\tsend message to all or one connected user')
	print('list_users \t\t\t\t\tget all connected users in the chat')

def receiver(s):
	while True:
		res_raw, address = s.recvfrom(1024)
		res = json.loads(res_raw.decode('utf-8'))
		if (res['resultCode'] == 500):
			print(res['error'])
		elif (res['resultCode'] == 200):
			if (res['type'] == LIST_USERS):
				if (len(res['data']['users']) == 0):
					print('No online users.')
					return
				print('Online users:')
				for user in res['data']['users']:
					print('* {}'.format(user))
			elif (res['type'] == MESSAGE_RECEIVED):
				print('{} says: {}'.format(res['data']['from'], res['data']['content']))

if __name__ == '__main__':
	main()