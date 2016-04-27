from a22_Formatter import Formatter
from datetime import datetime

class MinFormatter(Formatter):
    
    def __init__(self, metaDny, dataLst):
        super().__init__(metaDny, dataLst)

        
    def Print_counter(self, counter, data):
        if counter == 0:
            self.log.info("%s%s", "Timestamp" + " " * 18, \
                                            "Seconds Counter")
            self.log.info("")
        minNsec = self.curDtime.strftime("Min -> [%M], Sec -> [%S]")
        if data == None:
            self.log.info("%s : [%s]", minNsec, \
                          (" " * 3 + str(counter))[-3:])
        else:
            self.log.info("%s | Data -> %s", \
                            (" " * 32 + "[." + counter + "]"),\
                                                         data)
            