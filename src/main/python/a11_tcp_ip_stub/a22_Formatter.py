from datetime import datetime, timedelta
import logging.config
import time
import threading
import queue
from math import floor, modf
import sys
import subprocess
import re

class Formatter(threading.Thread):
    
    def __init__(self, metaDny, dataLst):
        threading.Thread.__init__(self)
        self.log = logging.getLogger(__name__)
        self.fifoQ = queue.Queue()
        self.metaDny = metaDny
        self.dataLst = dataLst
        ncCommand = "nc -lk 9999"
        splitted = re.split(" ", ncCommand)
        self.process = subprocess.Popen(splitted, 
                                   stdin=subprocess.PIPE,   \
                                   stdout=subprocess.PIPE,  \
                                   stderr=subprocess.PIPE,  \
                                   universal_newlines=True)
        self.firstDtime = datetime.now()
        self.curDtime = datetime.now()
        
    def Increment_counter(self, counter, delta):
        for no in range(delta):
            counter += 1
            #self.log.info("%s Sec", counter)
            self.Print_counter(counter, None)
        return counter

    def Print_counter(self, counter, data):
        if data == None:
            self.log.info("%s", counter)
        else:
            self.log.info(" .%s | Data -> %s", counter, data)
    
    def Get_element_from_dq(self, size, diffInSec):
        dataList = []
        rounded = round(diffInSec, 1)
        decVal = str(rounded).split(".")[1]
        self.log.debug("diffInSec -> %s", diffInSec)
        self.log.debug("rounded -> %s", rounded)
        self.log.debug("decVal -> %s", decVal)
        for inc in range(size):
            data = self.fifoQ.get_nowait()
            dataList.append(data)
            self.Print_counter(decVal, data)
        return dataList    
            
    def Send_data_to_nc(self, dataList):
        for data in dataList:
            self.process.stdin.write(data + "\n")
            self.process.stdin.flush()
                
    def run(self):
        counter = 0
        floored = 0
        self.firstDtime = datetime.now()
        self.Print_counter(counter, None)
        while True:
            time.sleep(0.5)
            self.curDtime = datetime.now()
            diffInSec = (self.curDtime - self.firstDtime).\
                                            total_seconds()
            self.log.debug("diffInSec -> %s", diffInSec)
            floored = floor(diffInSec)
            self.log.debug("floored -> %s", floored)
            delta = floored - counter
            if delta >= 1:
                counter = self.Increment_counter(counter, delta)
            size = self.fifoQ.qsize()
            if(size > 0):  
                  dataList = self.Get_element_from_dq(size, 
                                                      diffInSec)
                  self.Send_data_to_nc(dataList)
            
                
    def Place_data_in_queue(self):
        
        for element in self.dataLst:
            delay = element['delay']
            data = element['data']
            self.log.debug("element -> %s", element)
            self.log.debug("data -> %s", data)
            self.log.debug("delay -> %s", delay)
            
            if delay.endswith('s'):
                delayTime = delay[:-1]
                self.log.debug("Delaying by : %s", delayTime)
                time.sleep(float(delayTime))
            else:
                sys.exit(1)
            
            if data != 'None':
                self.fifoQ.put(data)
