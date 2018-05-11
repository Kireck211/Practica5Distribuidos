import socket
import threading
import json
import sys
import os
import time

server = '127.0.0.1'#'192.168.43.238'
port = 1234
LIST_USERS = 'list_users'
MESSAGE_RECEIVED = 'message_received'
SEND_FILE = 'send_file'

def main():
	s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
	s.connect((server, port))

	thread = threading.Thread(name='receiver', target=receiver, daemon=True, args=[s])
	thread.start()

	print('-------- Welcome to the greatest minichat -------')
	print_commands()

	while True:
		line = input('>>')

		if (len(line) == 0):
			continue

		line.strip()
		line = line.split(' ')
		req = None

		if (line[0] == 'exit' or line[0] == 'ex'):
			req = {'type' : 'exit'}
			send_request(s, req)
			break

		elif (line[0] == 'list_commands' or line[0] == 'lc'):
			print_commands()

		elif (line[0] == 'set_name' or line[0] == 'sn'):
			if (len(line) != 2):
				print('ERROR -> \'set_name\' command expects 1 argument')
				continue

			req = {
				'type': 'set_name',
				'data': {
					'content': line[1]
				}
			}
			send_request(s, req)	

		elif (line[0] == 'list_users' or line[0] == 'lu'):
			req = { 'type' : 'list_users' }
			send_request(s, req)

		elif (line[0] == 'send_message' or line[0] == 'sm'):
			if (len(line) < 2):
				print('ERROR -> \'send_message\' command expects 3 arguments')
				continue	

			req = {
				'type': 'send_message',
				'data': {
					'to': line[1],
					'content': ''.join(line[2:])
				}
			}
			send_request(s, req)

		elif (line[0] == 'send_file' or line[0] == 'sf'):
			if (len(line) != 3):
				print('ERROR -> \'send_file\' command expects 3 arguments')
				continue

			req = {
				'type' : 'send_file',
				'data': {
					'receiver': line[1],
					'name': line[2]
				}
			}
			send_request(s, req)

		else:
			print('command not recognized')

	s.close()
	exit()

def send_request(s, req):
	s.sendto(json.dumps(req).encode('utf-8'), (server, port))

def send_file(s, f):
	#file_length = os.stat(f).st_size
	package = f.read(1024)
	# TODO progress
	while package:
		s.sendto(package, (server, port))
		package = f.read(1024)
	print('File was sent')
	req = {}
	req['type'] = 'file_sent'
	send_request(s, req)

def print_commands():
	print('list_commands \t\t\t\t\tprints all the available commands in the chat')
	print('exit \t\t\t\t\t\texit from the chat')
	print('set_name [username] \t\t\t\tset your chat username')
	print('send_message all|[username] [message] \t\tsend message to all or one connected user')
	print('list_users \t\t\t\t\tget all connected users in the chat')
	print('send_file [username] [filename]\t\t\tsend file to one connected user')
	print('block_user [username]\t\t\t\tblock user by username')

def receiver(s):
	while True:
		try: 
			res_raw, address = s.recvfrom(1024)
		except:
			print('Connection failed, try again')
			break
		res = json.loads(res_raw.decode('utf-8'))
		if (res['type'] == 'send_file'):
			base_path = os.path.dirname(__file__)
			path = os.path.abspath(os.path.join(base_path, 'files', res['data']['name']))
			f = open(path, 'wb')
			res_raw, address = s.recvfrom(1024)
			try:
				while(res_raw):
					f.write(res_raw)
					s.settimeout(2)
					res_raw, address = s.recvfrom(1024)
			except timeout:
				f.close()
				print('File received, check files folder.')
		elif (res['resultCode'] == 500):
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
			elif (res['type'] == SEND_FILE):
				print('Sending file')
				base_path = os.path.dirname(__file__)
				file_path = os.path.abspath(os.path.join(base_path, 'files' , line[2]))
				try:
					f = open(file_path, 'rb')
				except:
					print('Error, file not found')
					continue
				#send_file(s,f)


if __name__ == '__main__':
	main()