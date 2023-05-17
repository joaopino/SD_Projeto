package meta1;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import javax.sound.midi.Soundbank;

public class RMIClient extends UnicastRemoteObject implements Serializable {

    static ArrayList<String> palavras;
    static String url;
    protected RMIClient() throws RemoteException {
        super();
    }


    public static void printArrayList(ArrayList<String> list) {

            for(String element : list){
                System.out.println(element);
            }
        }


    public static void printList(ArrayList<String[]> lista) {
        for (String[] elementos : lista) {
            for (int i=0;i<=2;i++ ) {
                System.out.println(elementos[i]);
            }
            System.out.println("================================");
        }
    }
    public static void main(String[] args) throws Exception {

        SearchModule_I s = (SearchModule_I) LocateRegistry.getRegistry(2000).lookup("Search_Module");
        RMIClient c = new RMIClient();

        boolean isAuthenticated = false;
        System.out.println("====================================");
        System.out.println("        WELCOME TO GOOGOL!!");
        System.out.println("    log in for more features :)");
        System.out.println("====================================");
        //MENU
        while (true) {
            
            System.out.println("Select an option:");
            if(!isAuthenticated){
                System.out.println("1. Log in");
            }
            if(isAuthenticated){
                System.out.println("1. Log out");
            }
            System.out.println("2. Adicionar um novo URL");
            System.out.println("3. Googol Search");
            System.out.println("4. Estado do Sistema");
            if(isAuthenticated) System.out.println("5. Consultar as páginas associadas a um URL");
            System.out.println("0. Leave");
            
            System.out.print("Option:");
            Scanner menuScanner = new Scanner(System.in);
            int option = 0;
            while (true) {

                if (menuScanner.hasNextInt()) {
                     option = menuScanner.nextInt();
                    break; // sai do loop se um inteiro válido foi inserido
                } else {
                    System.out.println("Valor inválido. Tente novamente.");
                    menuScanner.next(); // consome a entrada inválida
                }
            }
            
            System.out.println("====================================");
            switch (option) {
                case 0:
                    System.out.println("Bye bye!");
                    System.exit(0);
                case 1:
                    if(!isAuthenticated){
                        System.out.print("Enter your username: ");
                        String username = menuScanner.next();

                        System.out.print("Enter your password: ");
                        String password = menuScanner.next();

                        isAuthenticated = authenticate(username,password);
                    }
                    else{
                        System.out.println("Logging out...");
                        isAuthenticated = false;
                    }
                    System.out.println("====================================");
                    break;


                case 2:


                    System.out.print("Enter your password: ");
                    String url = menuScanner.next();

                    s.addUrl(url);

                    break;


                case 3:
                    Scanner scanner = new Scanner(System.in);

                    System.out.print("Digite o texto a pesquisar: ");
                    String texto = scanner.nextLine();

                    String[] palavras = texto.split("\\s+");


                    printList(s.sendAnswer(palavras));


                    break;
                case 4:
                    Boolean aux_verificar = true;
                    while(aux_verificar){
                        System.out.println("Printing Status");
                        
                        String state = s.getDownloadersStatus();

                        //Gets it all beautifull
                        String[] threads = state.split(" ");
                        System.out.printf("%-15s \t %-15s \t %-15s \t %s\n", "Name", "State", "Priority", "isDaemon");
                        for(int i = 0; i < Integer.parseInt(threads[0]); i++ ){
                            System.out.printf("%-15s \t %-15s \t %-15s \t %s\n", threads[1+i*4], threads[2+i*4],threads[3+i*4], threads[4+i*4]);
                        }

                       /* String barrelState= s.sendBarrelThreads();
                        String[] threadsB = barrelState.split(" ");
                        System.out.printf("%-15s \t %-15s \t %-15s \t %s\n", "Name", "State", "Priority", "isDaemon");
                        for(int i = 0; i < Integer.parseInt(threads[0]); i++ ){
                            System.out.printf("%-15s \t %-15s \t %-15s \t %s\n", threads[1+i*4], threads[2+i*4],threads[3+i*4], threads[4+i*4]);
                        }*/
                        System.out.println("Top 10 results");

                        printArrayList(s.sendTop10());
                        //Continues the option to refresh
                        System.out.println("1. Refresh");
                        System.out.println("2. Back");
                        System.out.print("Option:");
                        String status_option = menuScanner.next();
                        if(status_option.equals("2")) aux_verificar = false;
                    }
                    System.out.println("====================================");
                break;
                case 5:
                    //meter a cena da autenticação aqui
                    System.out.print("Desired URL:");
                    String user_url = menuScanner.next();
                    user_url = user_url.trim();
                    if(s.sendUrlConnections(user_url)==null){
                        System.out.println("Não existe url no ficheiro!!");
                    }
                    else {
                        printArrayList(s.sendUrlConnections(user_url));
                    }

                    break;

                






            }
        }
        
    }

    private static boolean authenticate(String username, String password) {
        
        if(username.equals("user") && password.equals("password")) return true;
        
        return false;
        
    }
}
