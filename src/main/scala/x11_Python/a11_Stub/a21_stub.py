from __future__ import print_function
import thread
import time
import threading

'''
http://www.techbeamers.com/python-multithreading-concepts/
'''
class ServerThread(threading.Thread):
    def __init__(self):
        threading.Thread.__init__(self)
        self.threadID = counter
        
    def run(self):
        print("Inside run")
        print_date(self.name, self.counter)
        print("Exitting run")
     
def First_stub():
    print("Inside First_stub")
    
    # Read YAML File
    
    # Loop 
    
    
First_stub()

#t1 = thread.start_new_thread(Server_thread, ("hi",))
    
    # Create Server(nc) interaction thread
    
    # Create User interaction thread
    #time.sleep(10)
       