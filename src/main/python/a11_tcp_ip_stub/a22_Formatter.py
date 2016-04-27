from datetime import datetime, timedelta
import logging.config
import time
import threading
import queue
from math import floor, modf
import sys
import subprocess
import re

'''===================================================================
Formatter
    -    Generates dummy input to be sent to netcat (nc)
==================================================================='''
class Formatter(threading.Thread):
    
    '''===============================================================
    __init__()
    ==============================================================='''
    def __init__(self, metaDny, dataLst):
        threading.Thread.__init__(self)
        self.log = logging.getLogger(__name__)
        self.fifoQ = queue.Queue()
        self.metaDny = metaDny
        self.dataLst = dataLst
        self.counter = 0
        self.diffInSec = 0
        self.decimalSeconds = 0
        self.firstDtime = datetime.now()
        self.curDtime = datetime.now()
        self.process = self.Connect_to_nc()

    '''===============================================================
    Connect_to_nc()
    ==============================================================='''
    def Connect_to_nc(self):
        ncCommand = "nc -lk 9999"
        splitted = re.split(" ", ncCommand)
        process = subprocess.Popen(splitted, 
                                   stdin=subprocess.PIPE,   \
                                   stdout=subprocess.PIPE,  \
                                   stderr=subprocess.PIPE,  \
                                   universal_newlines=True)
        
        return process
    '''===============================================================
    Increment_counter()
    ==============================================================='''
    def Increment_counter(self, delta):
        for no in range(delta):
            self.counter += 1
            self.Print_counter()

    '''===============================================================
    Print_counter()
    ==============================================================='''
    def Print_counter(self):
        minNsec = self.curDtime.strftime("[%M][%S]")
        if self.counter == 0:
            self.log.info("%s%s", "[min][sec]" + " " * 1, \
                                            "[seconds-counter]")
            self.log.info("")
        self.log.info("%s : [%s]", minNsec, \
                      (" " * 3 + str(self.counter))[-3:])


    '''===============================================================
    Print_data()
    ==============================================================='''            
    def Print_data(self, data):
        self.log.debug("self.diffInSec -> %s", self.diffInSec)
        self.log.info("%s => %s", \
                    (" " * 16 + "[." + self.decimalSeconds + "]"),\
                                                     data)
        
    '''===============================================================
    Get_element_fifoq()
    ==============================================================='''   
    def Get_element_from_fifoq(self, size):
        dataList = []
        for inc in range(size):
            data = self.fifoQ.get_nowait()
            dataList.append(data)
        return dataList    
    
    '''===============================================================
    Add_meta_info()
    ==============================================================='''            
    def Add_meta_info(self, data):
        dataNmeta = []
        dataNmeta.append("data=" + data)
        metaList = self.metaDny['addtodata']
        for meta in metaList:
            self.log.debug("Meta -> %s", meta)
            if(meta == "minsec") :
                minsec = self.curDtime.\
                        strftime("Min -> [%M], Sec -> [%S]")
                dataNmeta.append("minsec=" + minsec)        
            if(meta == "secondscounter"):
                dataNmeta.append("secondscounter=" + \
                                 str(self.counter) + \
                                 "." + str(self.decimalSeconds))
        return dataNmeta
    
    
    '''===============================================================
    Send_data_to_nc()
    ==============================================================='''            
    def Send_data_to_nc(self, dataList):
        for data in dataList:
            dataNmeta = self.Add_meta_info(data)
            self.log.debug("dataNmeta -> %s", dataNmeta)
            formattedData = ' || '.join(dataNmeta)
            self.log.debug("formattedData -> %s", formattedData)
            self.Print_data(formattedData)
            self.process.stdin.write(formattedData + "\n")
            self.process.stdin.flush()
            
    '''===============================================================
    Place_data_in_queue()
    ==============================================================='''                
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
    '''===============================================================
    run()
    ==============================================================='''                
    def run(self):
        floored = 0
        self.firstDtime = datetime.now()
        self.Print_counter()
        while True:
            time.sleep(0.5)
            self.curDtime = datetime.now()
            self.diffInSec = (self.curDtime - self.firstDtime).\
                                            total_seconds()
            rounded = round(self.diffInSec, 1)
            self.decimalSeconds = str(rounded).split(".")[1]
            self.log.debug("rounded -> %s", rounded)
            self.log.debug("decimalSeconds -> %s", \
                           self.decimalSeconds)
                                            
            self.log.debug("self.diffInSec -> %s", self.diffInSec)
            floored = floor(self.diffInSec)
            self.log.debug("floored -> %s", floored)
            delta = floored - self.counter
            if delta >= 1:
                self.Increment_counter(delta)
            size = self.fifoQ.qsize()
            if(size > 0):  
                dataList = self.Get_element_from_fifoq(size)
                self.Send_data_to_nc(dataList)
