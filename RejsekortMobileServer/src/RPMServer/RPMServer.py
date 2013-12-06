#!/usr/bin/env python

# Author: cem2ran
from BaseHTTPServer import BaseHTTPRequestHandler,HTTPServer
from traveler import Traveler
from authentication import Authentication

Travellers = { 42 : Traveler(42, "Cem Turan", None), 43 : Traveler(43, "Klaus Klokmose Nielsen", None) }
AccessTokens = {'testToken1234' : Authentication(42, 'testToken1234'), 'testToken5678' : Authentication(43, 'testToken5678')}

PORT = 1337
HOST = "0.0.0.0"

def me(uid):
    if uid in Travellers:
        return Travellers[uid].__dict__
    else:
        return None

def checkIn(uid, sid):
    return {'CheckedIn' : Travellers[uid].checkIn(sid)}

def checkOut(uid):
    return {'CheckedIn': Travellers[uid].checkOut()}

#TODO
def user():
    pass
#TODO
def userJournies(uid):
    #Travellers[uid].journies.__dict__
    pass
#requires_auth {}
one_param_requests = {
                     'me' : me, #uid
                     'checkout' : checkOut #uid
                    }
'''
one_param_requests = {
                     'me' : {'main' : me, 'journies': userJournies}, #uid
                     'checkout' : checkOut #uid
                    }
'''
two_param_requests = {
                      'checkin' : checkIn, # uid, sid
                      'user': user #check other users info
                     }

tables = dict(one_param_requests.items() + two_param_requests.items())

class requestHandler(BaseHTTPRequestHandler):
    def sendResponse(self, body):
        self.send_response(200)
        self.send_header('Content-type','application/json')
        self.end_headers()
        self.wfile.write(body)
    def do_GET(self):
        paths = self.path.split("/")
        num_paths = len(paths)
        print num_paths
        if num_paths > 1:
            table = paths[1].lower() #me #checkout (user_id / access_token in header: 'X-Access-Token')
            #check to see if table requires authentication. If so, do the following
            if not ('X-Access-Token' in self.headers):
                self.send_error(200, "missing X-Access-Token in header")
                return
            token = self.headers['X-Access-Token']
            if (table in tables) and (token in AccessTokens):
                output = ""
                uid = AccessTokens[token].getUser()
                print uid
                if num_paths > 2:
                    actionId = paths[2].lower() #checkin/:sid (:sid = Station/Stop id)
                    if len(actionId) == 0: 
                        self.send_error(200, "missing action")
                    else:
                        output = two_param_requests[table](uid, actionId)    
                else:
                    output = one_param_requests[table](uid)
                self.sendResponse(output)
            else:
                self.send_error(200, "Invalid method request or missing access token")      
        else:    
            self.send_error(200, "Missing path. Consult API documentation")
        
try:
    server = HTTPServer((HOST, PORT), requestHandler)
    print 'Started httpserver on port ' , PORT
    server.serve_forever()
except KeyboardInterrupt:
    print '^C received, shutting down the web server'
    server.socket.close()