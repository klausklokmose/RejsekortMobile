'''
Created on 21/11/2013

@author: cem2ran
'''
from journey import Journey

class Traveler(object):
    '''
    classdocs
    '''

    def __init__(self,ID=-1, name=None, journey=None):
        '''
        Constructor
        '''
        self.ID = ID
        self.name = name
        self.journey = journey
        self.journies = []
        self.checkedIn = False
        
    def checkIn(self, stationID):
        print stationID
        if(self.isCheckedIn()):
            self.journey.addWaypoint(stationID)
        else:
            self.journey = Journey(stationID)
        self.checkedIn = True
        return self.checkedIn
    def checkOut(self):
        #db insert 
        self.journies.append(self.journey);
        self.journey = None;
        self.checkedIn = False;
        return self.checkedIn
    def isCheckedIn(self):
        return True if (self.journey != None and self.checkedIn == True) else False

        
        