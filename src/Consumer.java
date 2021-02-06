/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.*;
import java.net.*;

/**
 *
 * @author hugod
 */
class Consumer {

    private static InetAddress serverIP;
    private static int serverPort;
    public static boolean userExit;

    // o nosso args vai ter 4 parametros como diz no enunciado do projecto, server port number, ip address do server, nome da wall, mensagem de texto
    public static void main(String args[]) throws Exception {
        String wallName, msgLine;
        /**
         * if (args.length != 4) { System.out.println("Server address, port
         * number, wall name and message required at command line.");
         * System.out.println("Usage: java DemoConsumer {SERVER - ADDRESS }
         * {SERVER - PORT - NUMBER }"); System.exit(1);
         *
         * }
         */
        try {
            serverIP = InetAddress.getByName("127.0.0.1");
        } catch (UnknownHostException ex) {
            System.out.println("Invalid SERVER-ADDRESS.");
            System.exit(1);
        }

        try {
            serverPort = 32113;
        } catch (NumberFormatException ex) {
            System.out.println("Invalid SERVER-PORT.");
            System.exit(1);
        }

        HTTPmessage request = new HTTPmessage();
        request.setRequestMethod("POST");
        
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Enter the name of the wall: ");
        wallName = in.readLine();

        System.out.print("Enter the message: ");
        msgLine = in.readLine();
        
        postMessage(wallName,msgLine);

       
    }

    private static boolean postMessage(String wallName, String msg) {
        Socket TCPconn;
        DataOutputStream sOut;
        DataInputStream sIn;
        try {
            TCPconn = new Socket(serverIP, serverPort);
        } catch (IOException ex) {
            return false;
        }

        try {
            sOut = new DataOutputStream(TCPconn.getOutputStream());
            sIn = new DataInputStream(TCPconn.getInputStream());
            HTTPmessage request = new HTTPmessage();
            request.setRequestMethod("POST");
            request.setURI("/walls/" + wallName+"/"+msg);
            request.send(sOut);
            HTTPmessage response = new HTTPmessage(sIn);
            if (!response.getStatus().startsWith("200")) {
                throw new IOException();
            }

        } catch (IOException ex) {
            try {
                TCPconn.close();
            } catch (IOException ex2) {
            }
            return false;
        }
        try {
            TCPconn.close();
        } catch (IOException ex2) {
        }
        return true;
    }
}// Consumer CLASS

