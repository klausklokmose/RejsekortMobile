'''
Created on 23/11/2013

@author: cem2ran
'''

class Authentication(object):
    '''
    classdocs
    '''

    def __init__(self, uid, accessToken):
        '''
        Constructor
        '''
        self.uid = uid;
        self.accessToken = accessToken;
        
    def getUser(self):
        return self.uid