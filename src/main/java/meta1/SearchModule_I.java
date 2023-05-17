package meta1;
import java.rmi.Remote;
import java.util.ArrayList;

public interface SearchModule_I extends Remote {
    public ArrayList<String[]> sendAnswer(String[] answer) throws Exception;
    public void addUrl(String s) throws Exception;
    public ArrayList<String> sendTop10() throws Exception;
    public String getDownloadersStatus() throws Exception;
    public ArrayList<String> sendUrlConnections(String url) throws Exception;
}
