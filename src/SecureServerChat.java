import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// STAI PROGRAMMANDO IL SERVER

public class SecureServerChat extends JFrame implements ActionListener {

    private static SSLServerSocketFactory sslServerSocketFactory;
    private static SSLServerSocket sslServerSocket;
    private static SSLSocket sslSocket;

    private static DataInputStream din;
    private static DataOutputStream dout;

    private static JTextArea chatArea;
    public static JTextField inputField;

    private static int port = 8080; //porta di ascolto

    public SecureServerChat (){
        setTitle("Chat Server");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        inputField = new JTextField();
        inputField.addActionListener(this);
        mainPanel.add(inputField, BorderLayout.SOUTH);

        getContentPane().add(mainPanel);
    }

    public static void main(String[] args) {
        new SecureServerChat().setVisible(true);

        String msgReceive = "";

        try{
            sslServerSocketFactory = getServerSocketFactory();
            sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);

            System.out.println("Server started on port " + port);

            sslSocket = (SSLSocket) sslServerSocket.accept();

            din = new DataInputStream(sslSocket.getInputStream());
            dout = new DataOutputStream(sslSocket.getOutputStream());

            while (msgReceive != "stop"){
                msgReceive = din.readUTF();
                chatArea.append("Client: " + msgReceive + "\n");
                System.out.println("Receive from client: " + msgReceive);
            }

        } catch (Exception e){

        }
    }

    private static SSLServerSocketFactory getServerSocketFactory() {
        SSLServerSocketFactory ssf = null;
        try {
            SSLContext ctx;
            KeyManagerFactory kmf;
            KeyStore ks;
            char[] passphrase = "password".toCharArray();
            String keyfile = "mykey";
            ctx = SSLContext.getInstance("TLS");
            kmf = KeyManagerFactory.getInstance("SunX509");
            ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(keyfile), passphrase);
            kmf.init(ks, passphrase);
            ctx.init(kmf.getKeyManagers(), null, null);
            ssf = ctx.getServerSocketFactory();
            return ssf;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try{
            String msgOut = "";
            msgOut = inputField.getText();
            inputField.setText("");
            dout.writeUTF(msgOut);
            chatArea.append("you: " + msgOut + "\n");
            System.out.println("Send message: " + msgOut + "\n");
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
    
}
