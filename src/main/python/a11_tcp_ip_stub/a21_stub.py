'''==================================================================
Thread that sends data to 'nc'
Reference :
    http://www.techbeamers.com/python-multithreading-concepts/
    https://docs.python.org/3/library/queue.html
    http://stackoverflow.com/questions/11829982/piping-data-from-python-to-an-external-command
=================================================================='''

from __future__ import print_function

import sys
import threading
import time
import yaml
import logging.config
from a22_Formatter import Formatter
from a25_MinFormatter import MinFormatter

def Get_arguments():
    log = logging.getLogger(__name__)
    argDny = {}
    for arg in sys.argv[1:]:
        log.debug("Arg -> %s", arg)
        key, value = arg.split("=")
        argDny[key] = value
    return argDny

def Load_input_file(argDny):
    with open(argDny['inputfile'], 'rt') as fin:
        text = fin.read()
    inputDny = yaml.load(text)
    return inputDny    
 
def Load_meta_info(inputList):
     metaDny = inputList[0]
     return metaDny
 
def Load_data(inputList):
     dataLst = inputList[1:]
     return dataLst
 
        
def Setup_logger():
    with open("./z11_logging.yaml", "rt") as fin:
        config = yaml.load(fin.read())
    logging.root.handlers = []
    logging.config.dictConfig(config)
    
    log = logging.getLogger(__name__)

def Main():    
    
    Setup_logger()

    log = logging.getLogger(__name__)
    
    # Get command line arguments
    argDny = Get_arguments()
    log.debug("argDny -> %s", argDny)
    
    # Load input file into Dictionary
    inputList = Load_input_file(argDny)
    log.debug("inputDny -> %s", inputList)
    
    # Get Meta data from inputList
    metaDny = Load_meta_info(inputList)
    log.debug("metaDny -> %s", metaDny)
    
    # Get Data List
    dataLst = Load_data(inputList)
    log.debug("dataList -> %s", dataLst)

    if metaDny['type'] == "simple":
        #thrd = MinFormatter(metaDny, dataLst)
        thrd= Formatter(metaDny, dataLst)
        log.info("Press ENTER when Ready...")
        input("")
        thrd.start()
        thrd.Place_data_in_queue()
        thrd.join()
        
    
Main()



