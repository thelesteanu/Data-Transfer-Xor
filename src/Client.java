import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import com.lloseng.ocsf.client.AbstractClient;

//client
public class Client extends AbstractClient {

    private final GUI gui;
    public String sentMessage = null;

    public Client(String host, int port, GUI gui) {
        super(host, port);
        this.gui = gui;
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        Message message = (Message) msg;
        boolean notSendByMe = true;
        switch (message.getType()) {
            case DATA_TRANSFER: // s-a trimis un mesaj de transfer de date
                if (message.getArgs()[0].equals(gui.fileStreamName)) {
                    notSendByMe = false;
                }
                if (notSendByMe) {
                    gui.addTextToLogger("Received file: "
                            + (String) message.getArgs()[0]);// afisam in logger
                    byte[] b = (byte[]) message.getArgs()[1];// fisierul (sub forma
                    // de sir de biti)
                    // salvam fisierul

                    b = getFileBytes(gui.fileStreamArray, b);
                    FileOutputStream fileOuputStream;
                    try {
                        fileOuputStream = new FileOutputStream(
                                (String) message.getArgs()[0]);
                        fileOuputStream.write(b);
                        fileOuputStream.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case MSG:
                String encodedResponse = (String) message.getArgs()[0];
                String desiredResponse = getDesiredResponse(encodedResponse, gui.sentMessage);
                gui.addTextToLogger("Decoded Response: " + desiredResponse);

                break;
        }
    }

    public String getDesiredResponse(String response, String sent) {
        byte[] xorArray;
        byte[] firstArray = getByteArray(response);
        byte[] secondArray = getByteArray(sent);

        int i = 0;
        if (firstArray.length > secondArray.length) {
            xorArray = new byte[firstArray.length];
            byte[] zeros = new byte[firstArray.length - secondArray.length];
            Arrays.fill(zeros, (byte) 0);
            secondArray = concat(zeros, secondArray);
            for (byte b : firstArray) {
                xorArray[i] = (byte) (b ^ secondArray[i++]);
            }
        } else {
            xorArray = new byte[secondArray.length];
            byte[] zeros = new byte[secondArray.length - firstArray.length];
            Arrays.fill(zeros, (byte) 0);
            firstArray = concat(zeros, firstArray);
            for (byte b : secondArray) {
                xorArray[i] = (byte) (b ^ firstArray[i++]);
            }
        }
        return returnStringFromArray(xorArray);
    }

    public byte[] getFileBytes(byte[] firstArray, byte[] secondArray) {
        int i = 0;
        byte[] xorArray;
        if (firstArray.length > secondArray.length) {
            xorArray = new byte[firstArray.length];
            byte[] zeros = new byte[firstArray.length - secondArray.length];
            Arrays.fill(zeros, (byte) 0);
            secondArray = concat(zeros, secondArray);
            for (byte b : firstArray) {
                xorArray[i] = (byte) (b ^ secondArray[i++]);
            }
        } else {
            xorArray = new byte[secondArray.length];
            byte[] zeros = new byte[secondArray.length - firstArray.length];
            Arrays.fill(zeros, (byte) 0);
            firstArray = concat(zeros, firstArray);
            for (byte b : secondArray) {
                xorArray[i] = (byte) (b ^ firstArray[i++]);
            }
        }
        return xorArray;
    }

    public byte[] concat(byte[] a, byte[] b) {
        int aLen = a.length;
        int bLen = b.length;
        byte[] c = new byte[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    public String returnStringFromArray(byte[] array) {
        String s = new String(array);
        return s;
    }

    public byte[] getByteArray(String message) {
        byte[] bytes = message.getBytes();
        return bytes;
    }

}
