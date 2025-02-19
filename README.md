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
This will display the table .

#### Table Format:
```sh
Enter TFTP Server IP Address:

then,

===== TFTP Client =====
1 - Download a file
2 - Upload a file
3 - Exit
Select an option:
```

### 4. Example Commands

#### Download a File:
```sh
Enter remote file: nscom.java
Enter local file: local1.java
```
This downloads `nscom.java` from the TFTP server and saves it as `local1.java` locally.
Ensure `nscom.java` exists on the server.

#### Upload a File:
```sh
Enter local file: sjfitness.png
Enter remote file: copy.png
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
| **Download a file that exists** | ` Enter local file: FileA.jpg Enter remote file: FileJ.jpg` | `Download complete: FileJ.jpg` |
| **Download a non-existent file** | `Enter local file: missing.jpg Enter remote file: FileM.jpg` | `TFTP Error: File not found on the server.` |
| **Upload a file that exists locally** | `Enter local file: FileA.jpg Enter remote file: FileJ.jpg` | `Upload complete: FileA.jpg` |
| **Upload a non-existent local file** | `Enter local file: missing.jpg Enter remote file: FileZ.jpg` | `Error: File missing.jpg does not exist.` |
| **Try downloading to an existing file** | `Enter local file: FileA.jpg Enter remote file: FileB.jpg` | `Error: File FileA.jpg already exists. Preventing overwrite.` |

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


1. check if you have tftp
> tftp
2. check if you have java 
> java
3. ensure IP address
> ping <IP address>
4. check if port 69 is active
> netstat -an | find "69"
5. make sure to manually start server
