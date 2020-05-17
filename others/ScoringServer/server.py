#!/usr/bin/python
from BaseHTTPServer import BaseHTTPRequestHandler,HTTPServer
import cgi
import json
from typingTask import score_typing_task
from typingInColorsTask import score_typing_in_colors_task

PORT_NUMBER = 8082
DEBUG = True
#This class will handles any incoming request from
#the browser 
class myHandler(BaseHTTPRequestHandler):
	
	#Handler for the GET requests
	def do_GET(self):
		self.send_response(200)
		self.send_header('Content-type','text/html')
		self.end_headers()
		# Send the html message
		self.wfile.write("Hello World !")
		return

	def do_POST(self):
		if DEBUG:
			print 'Got POST in path'
			print self.path
		ctype, pdict = cgi.parse_header(self.headers.getheader('content-type'))

		if ctype == 'multipart/form-data':
			postvars = cgi.parse_multipart(self.rfile, pdict)
		elif ctype == 'application/x-www-form-urlencoded':
			length = int(self.headers.getheader('content-length'))
			postvars = cgi.parse_qs(self.rfile.read(length), keep_blank_values=1)
		else:
			postvars = {}

		params = postvars

		if DEBUG:
			for param in params :
				print 'paramName: ' + str(param) +' - paramValue:' #+ str(params[param][0])


		if self.path != None:
			if "/typingTask" in self.path :
				resp = score_typing_task(params)
			if "/typingInColorsTask" in self.path : 
				resp = score_typing_in_colors_task(params)

		self.send_response(200)
		self.send_header('Content-type','text/html')
		self.end_headers()
		   	
		# Send the html message
		self.wfile.write(json.dumps(resp))

def main():
	try:
		#Create a web server and define the handler to manage the
		#incoming request
		server = HTTPServer(('', PORT_NUMBER), myHandler)
		print 'Started httpserver on port ' , PORT_NUMBER
		
		#Wait forever for incoming htto requests
		server.serve_forever()

	except KeyboardInterrupt:
		print '^C received, shutting down the web server'
		server.socket.close()

if __name__ == '__main__':
	main()
