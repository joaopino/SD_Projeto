package meta1;

import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;




public class SearchModule extends UnicastRemoteObject implements Serializable,SearchModule_I{
    public static StorageBarrels_I s;


    protected SearchModule() throws RemoteException {
        super();
        
    }


    public ArrayList<String[]> sendAnswer(String[] answer) throws Exception {
        ArrayList<String[]> search_result=s.listaResultados(answer,"Pages.obj","urlList.obj");

        return search_result;
    }


    public ArrayList<String> sendTop10() throws Exception {
        return s.lista_10_maiores();
    }

    public ArrayList<String> sendUrlConnections(String url) throws Exception {
        ArrayList<String> resultado = s.urlsLigados(url);
        return resultado;
    }

    public void addUrl(String s) throws Exception{
        try {
            String serverAddress = "localhost";

                Socket socket = new Socket(serverAddress, 433);
                PrintWriter outToServer = new PrintWriter(socket.getOutputStream(), true);


                outToServer.println(s);
                System.out.println("Sent message: " + s);

                socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getDownloadersStatus() throws Exception{

        Socket clientSocket = new Socket("localhost", 433);

        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);

        String output = "STATUS";
        out.println(output);

        String state = in.readLine();

        clientSocket.close();

        return state;

    }


/*public String sendBarrelThreads() throws Exception {
        return s.getBarrelThreads();
}*/


    public static void main(String[] args) throws Exception {

           
             s = (StorageBarrels_I) LocateRegistry.getRegistry(1099).lookup("Storage_Barrel");
            SearchModule c = new SearchModule();




            LocateRegistry.createRegistry(2000).rebind("Search_Module",c);


            
    


    

    
    }}
