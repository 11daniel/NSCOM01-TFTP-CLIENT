import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Scanner;

public class nscom1 {
    private static final int TFTP_PORT = 69;
    private static int blockSize = 512;
    private static int timeout = 10000;
    private static int tsize;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter TFTP Server IP Address: ");
        String serverIP = scanner.nextLine();

        System.out.print("Enter block size (default 512): ");
        blockSize = scanner.nextInt();

        System.out.print("Enter timeout in milliseconds (default 10000): ");
        timeout = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        while (true) {
            showMenu();
            System.out.print("Select an option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter remote file to download: ");
                    String remoteFile = scanner.nextLine();
                    System.out.print("Enter local filename to save: ");
                    String localFile = scanner.nextLine();
                    handleDownload(serverIP, localFile, remoteFile);
                    break;

                case "2":
                    System.out.print("Enter local file to upload: ");
                    localFile = scanner.nextLine();
                    System.out.print("Enter remote filename: ");
                    remoteFile = scanner.nextLine();
                    handleUpload(serverIP, localFile, remoteFile);
                    break;

                case "3":
                    System.out.println("Exiting...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    private static void showMenu() {
        System.out.println("\n===== TFTP Client =====");
        System.out.println("1 - Download a file");
        System.out.println("2 - Upload a file");
        System.out.println("3 - Exit");
    }

    private static void handleDownload(String serverIP, String localFile, String remoteFile) {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(timeout);
            if (new File(localFile).exists()) {
                System.out.println("Error: File " + localFile + " already exists. Preventing overwrite.");
                return;
            }
            downloadFile(socket, serverIP, localFile, remoteFile);
        } catch (Exception e) {
            System.out.println("Download Error: " + e.getMessage());
        }
    }

    private static void handleUpload(String serverIP, String localFile, String remoteFile) {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(timeout);
            if (!new File(localFile).exists()) {
                System.out.println("Error: File " + localFile + " does not exist.");
                return;
            }
            uploadFile(socket, serverIP, localFile, remoteFile);
        } catch (Exception e) {
            System.out.println("Upload Error: " + e.getMessage());
        }
    }

    private static void downloadFile(DatagramSocket socket, String serverIP, String localFile, String remoteFile)
            throws IOException {
        System.out.println("Requesting file: " + remoteFile);

        InetAddress serverAddress = InetAddress.getByName(serverIP);
        byte[] requestPacket = createRequestPacket(remoteFile, "octet", true);

        DatagramPacket sendPacket = new DatagramPacket(requestPacket, requestPacket.length, serverAddress, TFTP_PORT);
        socket.send(sendPacket);

        try (FileOutputStream fos = new FileOutputStream(localFile)) {
            receiveFile(socket, fos);
        }

        System.out.println("Download complete: " + localFile);
    }

    private static void uploadFile(DatagramSocket socket, String serverIP, String localFile, String remoteFile)
            throws IOException {
        System.out.println("Uploading file: " + localFile);

        InetAddress serverAddress = InetAddress.getByName(serverIP);
        // System.out.println("Uploading to: " + serverAddress.getHostAddress() + " on
        // port " + TFTP_PORT);

        // Create and send WRQ (Write Request) packet
        byte[] requestPacket = createWriteRequestPacket(remoteFile, "octet");
        DatagramPacket sendPacket = new DatagramPacket(requestPacket, requestPacket.length, serverAddress, TFTP_PORT);
        socket.send(sendPacket);

        // Wait for server response (OACK or ACK)
        byte[] responseBuffer = new byte[512];
        DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
        socket.receive(responsePacket);

        serverAddress = responsePacket.getAddress();
        int serverPort = responsePacket.getPort();
        System.out.println("Server assigned new port: " + serverPort);

        // Read and send file contents
        try (FileInputStream fis = new FileInputStream(localFile)) {
            sendFile(socket, fis, serverAddress, serverPort);
        }

        System.out.println("Upload complete: " + remoteFile);
    }

    private static byte[] createRequestPacket(String filename, String mode) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.writeShort(1); // RRQ (Read Request)
            dos.writeBytes(filename);
            dos.writeByte(0); // NULL terminator
            dos.writeBytes(mode);
            dos.writeByte(0); // NULL terminator
        } catch (IOException e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    private static byte[] createRequestPacket(String filename, String mode, boolean isReadRequest) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            dos.writeShort(isReadRequest ? 1 : 2);
            dos.write(filename.getBytes());
            dos.writeByte(0);
            dos.write(mode.getBytes());
            dos.writeByte(0);
            dos.write("blksize".getBytes());
            dos.writeByte(0);
            dos.write(String.valueOf(blockSize).getBytes());
            dos.writeByte(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bos.toByteArray();
    }

    private static void receiveFile(DatagramSocket socket, FileOutputStream fos) throws IOException {
        byte[] buffer = new byte[516]; // Standard TFTP packet size
        DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);

        while (true) {
            socket.receive(receivedPacket);
            int len = receivedPacket.getLength();
            byte[] data = Arrays.copyOf(buffer, len);

            // System.out.println("Received Data: " + Arrays.toString(data));

            // Check opcode
            int opcode = ((data[0] & 0xFF) << 8) | (data[1] & 0xFF);

            if (opcode == 6) { // OACK (Option Acknowledgment)
                // System.out.println("Received OACK, sending ACK for block 0");
                sendAck(socket, receivedPacket.getAddress(), receivedPacket.getPort(), 0);
                continue; // Wait for actual file data
            } else if (opcode == 3) { // DATA packet
                int blockNumber = ((data[2] & 0xFF) << 8) | (data[3] & 0xFF);
                fos.write(data, 4, len - 4); // Write data to file

                sendAck(socket, receivedPacket.getAddress(), receivedPacket.getPort(), blockNumber);

                if (len < 516) { // Last block (less than 512 bytes of data)
                    // System.out.println("Final block received.");
                    break;
                }
            } else if (opcode == 5) { // ERROR packet
                String errorMessage = new String(data, 4, len - 4);
                System.out.println("TFTP Error: " + errorMessage);
                break;
            }
        }
    }

    private static void sendFile(DatagramSocket socket, FileInputStream fis, InetAddress serverAddress, int serverPort)
            throws IOException {
        byte[] buffer = new byte[blockSize];
        int bytesRead;
        int blockNumber = 1;

        while ((bytesRead = fis.read(buffer)) != -1) {
            byte[] dataPacket = new byte[4 + bytesRead];
            dataPacket[0] = 0;
            dataPacket[1] = 3; // DATA opcode
            dataPacket[2] = (byte) (blockNumber >> 8);
            dataPacket[3] = (byte) (blockNumber & 0xFF);
            System.arraycopy(buffer, 0, dataPacket, 4, bytesRead);

            DatagramPacket sendPacket = new DatagramPacket(dataPacket, dataPacket.length, serverAddress, serverPort);
            socket.send(sendPacket);
            // System.out.println("Sent DATA block " + blockNumber);

            // Wait for ACK
            byte[] ackBuffer = new byte[4];
            DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length);
            socket.receive(ackPacket);

            if (ackBuffer[1] == 4) { // ACK opcode
                int ackBlockNumber = ((ackBuffer[2] & 0xFF) << 8) | (ackBuffer[3] & 0xFF);
                if (ackBlockNumber == blockNumber) {
                    // System.out.println("Received ACK for block " + blockNumber);
                    blockNumber++;
                } else {
                    System.out.println("Unexpected ACK block number: " + ackBlockNumber);
                }
            } else {
                System.out.println("Unexpected response received!");
                break;
            }
        }

        System.out.println("File upload complete.");
    }

    private static byte[] createDataPacket(int blockNumber, byte[] data, int dataLength) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            dos.writeShort(3);
            dos.writeShort(blockNumber);
            dos.write(data, 0, dataLength);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bos.toByteArray();
    }

    private static void sendAck(DatagramSocket socket, InetAddress address, int port, int block) throws IOException {
        byte[] ackPacket = { 0, 4, (byte) (block >> 8), (byte) block };
        DatagramPacket packet = new DatagramPacket(ackPacket, ackPacket.length, address, port);
        socket.send(packet);
        // System.out.println("Sent ACK for block " + block);
    }

    private static void receiveAcknowledgment(DatagramSocket socket) throws IOException {
        byte[] ackBuffer = new byte[4]; // TFTP ACK packet is 4 bytes
        DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length);

        socket.receive(ackPacket);

        int opcode = ((ackBuffer[0] & 0xFF) << 8) | (ackBuffer[1] & 0xFF);
        int blockNumber = ((ackBuffer[2] & 0xFF) << 8) | (ackBuffer[3] & 0xFF);

        if (opcode == 4) { // ACK Opcode
            System.out.println("Received ACK for block " + blockNumber);
        } else {
            System.out.println("Unexpected packet: " + Arrays.toString(ackBuffer));
        }
    }

    private static byte[] createWriteRequestPacket(String filename, String mode) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            dos.writeShort(2); // WRQ (Write Request)
            dos.write(filename.getBytes());
            dos.writeByte(0);
            dos.write(mode.getBytes());
            dos.writeByte(0);
            dos.write("blksize".getBytes());
            dos.writeByte(0);
            dos.write(String.valueOf(blockSize).getBytes());
            dos.writeByte(0);
            dos.write("tsize".getBytes());
            dos.writeByte(0);
            dos.write(String.valueOf(new File(filename).length()).getBytes());
            dos.writeByte(0);

            File file = new File(filename);
            dos.write(String.valueOf(file.length()).getBytes()); // Send file size
            dos.writeByte(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bos.toByteArray();
    }

}
