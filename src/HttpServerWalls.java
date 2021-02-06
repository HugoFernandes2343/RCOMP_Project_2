
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author hugod
 */
public class HttpServerWalls {

    static private final String BASE_FOLDER = "../www";

    static private ServerSocket sock;

    public static void main(String args[]) throws Exception {
        Socket cliSock;

        /**
         * if(args.length!=1) { System.out.println("Local port number required
         * at the command line."); System.exit(1); }
         */
        try {
            sock = new ServerSocket(32113);
        } catch (IOException ex) {
            System.out.println("Server failed to open local port " + 32113);
            System.exit(1);
        }

        System.out.println("Server ready, listening on port number " + 32113);
        while (true) {
            cliSock = sock.accept();
            HttpWallsRequest req = new HttpWallsRequest(cliSock, BASE_FOLDER);
            req.start();
        }
    }

    // MESSAGES ARE ACCESSED BY THREADS - LOCKING REQUIRED
    private static final ArrayList<Wall> walls = new ArrayList<>();

    //metodo delete a uma msg 
    public static void deleteMessage(String uri) {
        String s[] = uri.split("/");
        String w = s[2];
        int index = Integer.parseInt(s[3]);

        for (int i = 0; i < walls.size(); i++) {

            if (walls.get(i).getName().equalsIgnoreCase(w)) {
                ArrayList<String> ms = walls.get(i).getMsgs();
                if (ms.get(index) == null) {
                    System.out.println("Mensagem nao existe.");
                }
                ms.set(index, "");
            }
        }

    }

    //metodo delete a uma wall
    public static void deleteWall(String uri) {
        synchronized (walls) {
            String s[] = uri.split("/");
            String w = s[2];

            for (int i = 0; i < walls.size(); i++) {

                if (walls.get(i).getName().equalsIgnoreCase(w)) {
                    walls.remove(i);
                }
            }
        }
    }

    //GET, recebe uri devolve string concatenada de msgs 
    public static String getMsgsWall(String uri) {
        synchronized (walls) {

            String s[] = uri.split("/");
            String name = s[2];
            String ret = "";


            for (int i = 0; i < walls.size(); i++) {
                if (walls.get(i).getName().equals(name)) {
                    System.out.println("encontrou a wall");
                    int j = 0;
                    for (String st : walls.get(i).getMsgs()) {
                        if (j == 0) {
                            ret = st;
                            System.out.println(st);
                            j++;
                        } else if (st.equalsIgnoreCase("")) {
                        } else {
                            ret = ret + "%%" + st;
                        }

                    }
                }
            }
            System.out.println("ret: " + ret);
            return ret;
        }
    }

    //por a receber nome da wall se nao existir invoca o metodo de criar a wall
    public static void addMsg(String uri) {
        System.out.println("URI:" + uri);
        synchronized (walls) {
            String s[] = uri.split("/");
            String nameW = s[2];
            String msg = s[3];
            System.out.println("Mensagem a adicionar: " + msg);
            boolean condition = false;

            for (int i = 0; i < walls.size(); i++) {
                if (walls.get(i).getName().equals(nameW)) {
                    walls.get(i).getMsgs().add(msg);
                    System.out.println("Adicionou mensagem a wall existente");
                    condition = true;
                }
            }

            if (condition == false) {
                System.out.println("criou nova Wall");
                System.out.println(nameW);
                Wall w = new Wall(nameW);
                w.getMsgs().add(msg);
                walls.add(w);
            }

            walls.notifyAll(); // notify all threads waiting on MSG_LIST's monitor
        }
    }
}
