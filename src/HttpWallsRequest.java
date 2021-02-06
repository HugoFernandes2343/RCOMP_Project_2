/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author hugod
 */
import java.io.*;
import java.net.*;


/**
 *
 * @author ANDRE MOREIRA (asc@isep.ipp.pt)
 */
public class HttpWallsRequest extends Thread {

    String baseFolder;
    Socket sock;
    DataInputStream inS;
    DataOutputStream outS;

    public HttpWallsRequest(Socket s, String f) {
        baseFolder = f;
        sock = s;
    }

    @Override
    public void run() {
        try {
            outS = new DataOutputStream(sock.getOutputStream());
            inS = new DataInputStream(sock.getInputStream());
        } catch (IOException ex) {
            System.out.println("Thread error on data streams creation");
        }
        try {

            HTTPmessage request = new HTTPmessage(inS);
            HTTPmessage response = new HTTPmessage();
            response.setResponseStatus("200 Ok");

            if (request.getMethod().equals("GET")) {
                if (request.getURI().startsWith("/walls/")) {
                    System.out.println("entrou no Get");
                    String msgs = HttpServerWalls.getMsgsWall(request.getURI());

                    if (msgs != null && !msgs.equals("")) {
                        response.setContentFromString(msgs, "text/plain");
                        response.send(outS);
                    }

                } else { // NOT GET /walls/ , THEN IT MUST BE A FILE
                    String fullname = baseFolder + "/";
                    if (request.getURI().equals("/")) {
                        fullname = fullname + "index.html";
                    } else {
                        fullname = fullname + request.getURI();
                    }
                    if (!response.setContentFromFile(fullname)) {
                        response.setContentFromString("<html><body><h1>404 File not found</h1></body></html>", "text/html");
                        response.setResponseStatus("404 Not Found");

                    }
                }
            } else { // NOT GET
                if (request.getMethod().equals("POST") && request.getURI().startsWith("/walls/")) {
                    System.out.println("Entrou no post");
                    HttpServerWalls.addMsg(request.getURI());
                    response.setResponseStatus("200 Ok");

                } else if (request.getMethod().equals("DELETE") && request.getURI().startsWith("/walls/") && (request.getURI().split("/").length == 3)) {
                    System.out.println("Entrou no delete wall");
                    HttpServerWalls.deleteWall(request.getURI());
                    response.setResponseStatus("200 Ok");

                } else if (request.getMethod().equals("DELETE") && request.getURI().startsWith("/walls/") && (request.getURI().split("/").length == 4)) {
                    System.out.println("Entrou no delete message");
                    HttpServerWalls.deleteMessage(request.getURI());
                    response.setResponseStatus("200 Ok");

                } else {
                    response.setContentFromString("<html><body><h1>ERROR: 405 Method Not Allowed</h1></body></html>", "text/html");
                    response.setResponseStatus("405 Method Not Allowed");

                }
            }
            response.send(outS);
        } catch (IOException ex) {
            //System.out.println("Thread I/O error on request/response");
        }
        try {
            sock.close();
        } catch (IOException ex) {
            System.out.println("CLOSE IOException");
        }
    }
}
