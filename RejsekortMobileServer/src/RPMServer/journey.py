'''
Created on 23/11/2013

@author: cem2ran
'''
from stationstop import StationStop
from datetime import datetime

Stations = { '1337' : StationStop(1337, "Sydhavn st", {'lat':55.654756,'long':12.537174}, 2, 200) };


class Journey(object):
    '''
    classdocs
    '''


    def __init__(self, StationID):
        self.wayPoints = []
        self.startTime = datetime.now().now().isoformat()
        self.addWaypoint(StationID)
        
    def addWaypoint(self, sid):
        self.wayPoints.append(Stations[sid])
        print 'Waypoints: ',self.wayPoints