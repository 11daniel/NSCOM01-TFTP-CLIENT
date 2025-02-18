# NSCOM01-TFTP-CLIENT

open cmd as administrator, make sure you are in the file directory 
1. [ javac nscom.java]
2. [ java nscom ] for usage
3. [ java nscom <server_ip> <upload/download> <local_file> <remote_file> ]
   - example: [java nscom 192.168.1.190 download local1.java nscom.java]
      - this creates local1.java (local copy for tftp client), a local copy of nscom.java (remote file from tftp server)
      - make sure <remote_file> exists (in this example: nscom.java)
      - does not allow overwriting an existing local_file
             [java nscom 192.168.1.190 upload sjfitness.png copy.png]
           - this uploads sjfitness.png (local copy from tftp client) to the tftp server as copy.png (the remote file)
            - make sure <local_file> exists (in this example: sjfitness.png)
            - allows overwriting an existing remote_file



   to see IP address: [ipconfig /all]
   
   to see if tftp exists: tftp
   
   to check port69: netstat -an | find "69"
   

   
