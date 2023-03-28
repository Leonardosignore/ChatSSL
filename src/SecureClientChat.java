import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class SecureClientChat extends JFrame implements ActionListener {

    static SSLSocketFactory sslSocketFactory;
    static SSLSocket sslSocket;
    static String host = "localhost";
    static int port = 8080;

    private static DataInputStream din;
    private static DataOutputStream dout;

    private static JTextArea chatArea;
    public static JTextField inputField;

    public SecureClientChat() {
        setTitle("Chat Client");
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

        System.setProperty("javax.net.ssl.trustStore", "clientstore");
        System.setProperty("javax.net.ssl.trustStorePassword", "password");

        new SecureClientChat().setVisible(true);
        System.out.println("Grafica ON");

        String msgReceive = "";

        try {
            sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            sslSocket = (SSLSocket) sslSocketFactory.createSocket(host, port);

            System.out.println("connected to Server");

            din = new DataInputStream(sslSocket.getInputStream());
            dout = new DataOutputStream(sslSocket.getOutputStream());

            while (msgReceive != "stop") {
                msgReceive = din.readUTF();
                chatArea.append("Server: " + msgReceive + "\n");
                System.out.println("Receive from Server: " + msgReceive);
            }

        } catch (Exception e) {

        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String msgOut = "";
            msgOut = inputField.getText();
            inputField.setText("");
            dout.writeUTF(msgOut);
            chatArea.append("you: " + msgOut + "\n");
            System.out.println("Send message: " + msgOut + "\n");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
