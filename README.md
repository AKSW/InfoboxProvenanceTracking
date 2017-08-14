# InfoboxProvenanceTracking
a tool for tracking the changes of values in infoboxes fro Wikipedia

# USAGE

After compiling, the .jar can be used with the following parameters  

 -earlier, -e  
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Earliest timestamp (Date in yyyy-MM-dd) to extract  
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Default: 2001-01-02  
 -help, -h  
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Print help information and exit  
 -language, -lang  
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Dump Language  
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;en  
 -lastchange, -last  
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Only last change to an existing triple will be saved  
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;false 
 -later, -l  
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Last timestamp(Date in yyyy-MM-dd) to extract  
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Default: 2017-08-14t  
 -name, -a  
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Name of the Article  
 -path  
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Path to the dump containing directory    
 -rerun, -r  
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Rerun program after a crash  
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Default: false  
 -threads, -t  
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Number of threads to run  
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Default: 1  
  
  e.g.:  
  -name mile -last  
  -path /src/test/resources/inputde -lang de -earlier 2014-10-09 -later 2016-12-30