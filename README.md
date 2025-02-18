# nscom - TFTP Client

## **Instructions for Compiling and Running**

### **1. Open Command Prompt as Administrator**
Ensure you have administrative privileges and navigate to the directory containing `nscom.java`.

### **2. Compile the Program**
javac nscom.java

markdown
Copy
Edit

### **3. Run the Program**
#### **Usage:**
java nscom

bash
Copy
Edit
This will display the required command format.

#### **Command Format:**
java nscom <server_ip> <upload/download> <local_file> <remote_file>

markdown
Copy
Edit

### **4. Example Commands**
#### **Download a File:**
java nscom 192.168.1.190 download local1.java nscom.java

pgsql
Copy
Edit
This downloads `nscom.java` from the TFTP server and saves it as `local1.java` locally.  
Ensure `nscom.java` exists on the server.

#### **Upload a File:**
java nscom 192.168.1.190 upload sjfitness.png copy.png

markdown
Copy
Edit
This uploads `sjfitness.png` from the local machine to the TFTP server as `copy.png`.  
The remote file will be overwritten if it exists.

## **Implemented Features and Error Handling**

✅ **Timeout Handling with Retries**: The client retries up to 3 times if the server does not respond.

✅ **Duplicate ACK Handling**: Prevents misinterpretation of duplicate ACKs.

✅ **File Not Found Handling**:
- Displays an error message if the requested remote file does not exist.
- Prevents overwriting an existing local file during download.

✅ **Port and Connection Handling**:
- Uses UDP for communication.
- Checks if port 69 is active.

✅ **Overwriting Restrictions**:
- **Local Files:** Prevents overwriting existing local files during downloads.
- **Remote Files:** Allows overwriting existing files on the TFTP server.

## **Test Cases and Expected Outputs**

| **Test Case** | **Command** | **Expected Output** |
|--------------|------------|--------------------|
| Download a file that exists | `java nscom 192.168.1.190 download local1.java nscom.java` | `Download complete: local1.java` |
| Download a non-existent file | `java nscom 192.168.1.190 download local1.java missing.java` | `TFTP Error: File not found on the server.` |
| Upload a file that exists locally | `java nscom 192.168.1.190 upload sjfitness.png copy.png` | `Upload complete: copy.png` |
| Upload a non-existent local file | `java nscom 192.168.1.190 upload missing.png copy.png` | `Error: File missing.png does not
