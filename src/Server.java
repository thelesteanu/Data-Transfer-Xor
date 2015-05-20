import com.lloseng.ocsf.server.AbstractServer;
import com.lloseng.ocsf.server.ConnectionToClient;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

//server
public class Server extends AbstractServer {

    private final GUI gui;
    public Message firstMessage = null;

    public Server(int port, GUI gui) {
        super(port);
        this.gui = gui;
    }

    //metoda apelata cand clientul trimite un mesaj la server
    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        if (firstMessage == null) {
            firstMessage = (Message) msg;
        } else {
            Message message = (Message) msg;
            String newStringMessage = executeXor((String) firstMessage.getArgs()[0], (String) message.getArgs()[0]); //TODO!!!!!!
            sendToAllClients(new Message(message.getType(), new Object[]{newStringMessage}));
            firstMessage = null;
        }
    }

    //metoda apelata cand se conecteaza un client la server


    private String executeXor(String firstMessage, String secondMessage) {
        int i = 0;
        byte[] xorArray;
        byte[] firstArray = getByteArray(firstMessage);
        byte[] secondArray = getByteArray(secondMessage);

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

    @Override
    protected void clientConnected(ConnectionToClient client) {
        super.clientConnected(client);
        gui.addTextToLogger("Client " + client.getInetAddress().getHostAddress() + " connected!"); //afisam in logger
    }

    //metoda apelata cand se deconectaeza un client de la server
    @Override
    protected synchronized void clientDisconnected(ConnectionToClient client) {
        super.clientDisconnected(client);
        gui.addTextToLogger("Client " + client.getInetAddress().getHostAddress() + " disconnected!"); //afisam in logger
    }
}
