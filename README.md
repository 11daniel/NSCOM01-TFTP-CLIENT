# NSCOM - TFTP Client

## Instructions for Compiling and Running

### 1. Open Command Prompt as Administrator
Ensure you have administrative privileges and navigate to the directory containing `nscom.java`.

### 2. Compile the Program
```sh
javac nscom.java
```

### 3. Run the Program
#### Usage:
```sh
java nscom
```
This will display the required command format.

#### Command Format:
```sh
java nscom <server_ip> <upload/download> <local_file> <remote_file>
```

### 4. Example Commands

#### Download a File:
```sh
java nscom 192.168.1.190 download local1.java nscom.java
```
This downloads `nscom.java` from the TFTP server and saves it as `local1.java` locally.
Ensure `nscom.java` exists on the server.

#### Upload a File:
```sh
java nscom 192.168.1.190 upload sjfitness.png copy.png
```
This uploads `sjfitness.png` from the local machine to the TFTP server as `copy.png`.
The remote file will be overwritten if it exists.

---
## Implemented Features and Error Handling

✅ **Timeout Handling with Retries:** The client retries up to 3 times if the server does not respond.

✅ **Duplicate ACK Handling:** Prevents misinterpretation of duplicate ACKs.

✅ **File Not Found Handling:**
- Displays an error message if the requested remote file does not exist.
- Prevents overwriting an existing local file during download.

✅ **Port and Connection Handling:**
- Uses UDP for communication.
- Checks if port 69 is active.

✅ **Overwriting Restrictions:**
- **Local Files:** Prevents overwriting existing local files during downloads.
- **Remote Files:** Allows overwriting existing files on the TFTP server.

---
## Test Cases and Expected Outputs

| Test Case | Command | Expected Output |
|-----------|---------|----------------|
| **Download a file that exists** | `java nscom 192.168.1.190 download FileA.jpg FileA1.jpg` | `Download complete: FileA.jpg` |
| **Download a non-existent file** | `java nscom 192.168.1.190 download FileB.jpg FileA.jpg` | `TFTP Error: File not found on the server.` |
| **Upload a file that exists locally** | `java nscom 192.168.1.190 upload FileA.jpg FileA.jpg` | `Upload complete: FileA.jpg` |
| **Upload a non-existent local file** | `java nscom 192.168.1.190 upload missing.png FileA.jpg` | `Error: File missing.png does not exist.` |
| **Try downloading to an existing file** | `java nscom 192.168.1.190 download FileA.jpg FileA.jpg` | `Error: File FileA.jpg already exists. Preventing overwrite.` |

---
## Useful Commands for Debugging

### Check your IP address:
```sh
ipconfig /all
```

### Verify TFTP is installed:
```sh
tftp
```

### Check if port 69 is active:
```sh
netstat -an | find "69"
