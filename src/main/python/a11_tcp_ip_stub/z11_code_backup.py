    '''
    def Get_nc_inputstream(self):
        ncCommand = "nc -lk 9999"
        splitted = re.split(" ", ncCommand)
        process = subprocess.Popen(splitted, stdin=subprocess.PIPE,\
                                   stdout=subprocess.PIPE, \
                                   stderr=subprocess.PIPE, \
                                   universal_newlines=True)
        inc = 0
        while True:
            #
            self.log.debug("Writing to stdin...")
            # process.communicate("some datadas112\n")
            # process.
            mystring = "some data" + str(inc) + "\n"
            #mystring = "some data" + str(inc)
            inc += 1
            process.stdin.write(mystring)
            process.stdin.flush()
            time.sleep(1)
    '''        
        
'''
    # http://stackoverflow.com/questions/20631855/get-the-difference-between-two-datetime-objects-in-minutes-in-python
    # http://www.ehow.com/info_10044907_nearest-integer-function-python.html
'''    