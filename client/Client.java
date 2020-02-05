import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/*
    Client
    Singleton che mette a disposizione le funzioni del client
    Si occupa delle communicazioni col server TCP e RMI
*/
public class Client {
    private Socket socket;
    private ChallengeThread challengeThread; //Thread che si occupa delle richieste di sfida su UDP
    private String serverAddress;
    private int port;
    private BufferedWriter writer;
    private BufferedReader reader;
    private String name; //Nome dell'utente

    public void setParameters(String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.port = port;
    }

    public int start(){
        try {
            socket = new Socket(InetAddress.getByName(serverAddress), port);
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            challengeThread = new ChallengeThread(socket.getLocalPort() + 1);
            return 0;
        } catch(Exception e) {
            return -10;
        }
    }

    public String login(String username, String password){
        try {
            writer.write("login\n");
            writer.write(username + "\n");
            writer.write(password + "\n");
            writer.flush();
            String result = reader.readLine();
            if(result.equals("success")) {
                challengeThread.start();
                name = username;
                return "Success";
            }

            String errorMessage = reader.readLine();
            return errorMessage;
        } catch(IOException e) {
            return "Errore di connessione";
        }
    }

    public String registra(String username, String password) {
        Registry registry = null;
        RegistrationInterface server = null;

        try {
            registry = LocateRegistry.getRegistry(8888);
            server = (RegistrationInterface)registry.lookup("RegistrationServer");
            int res = server.registraUtente(username, password);

            if(res == -1) return "Username già registrato";

            return "ok";
        } catch(Exception e) {
            return "Errore di connessione al server";
        }
    }

    public int punteggio() throws IOException {
        writer.write("punteggio\n");
        writer.flush();

        String score = reader.readLine();
        if(score == null) return -1;

        return Integer.parseInt(score);
    }

    public String aggiungiAmico(String friendName) {
        try {
            writer.write("aggiungi_amico\n");
            writer.write(friendName + "\n");
            writer.flush();

            String res = reader.readLine();
            if(res.equals("success")) return "success";

            String errorMessage = reader.readLine();
            return errorMessage;
        } catch(IOException e) {
            return "Errore di connessione";
        }

    }

    public List<String> listaAmici() {
        List<String> friendList = new ArrayList<>();
        try {
            writer.write("lista_amici\n");
            writer.flush();

            String line = reader.readLine();
            if (!line.equals("start")) return null;

            while (!(line = reader.readLine()).equals("end"))
                friendList.add(line);
        }catch (Exception e) {
            return null;
        }

        return friendList;
    }

    public List<ScoreboardRecord> classifica() {
        List<ScoreboardRecord> classifica = new ArrayList<>();
        try {
            writer.write("classifica\n");
            writer.flush();

            String json = reader.readLine();
            if(json.charAt(0) != '[') return null;

            try {
                JSONParser parser = new JSONParser();
                JSONArray array = (JSONArray) parser.parse(json);
                for (Object o : array) {
                    JSONObject object = (JSONObject) o;
                    ScoreboardRecord record = new ScoreboardRecord((String) object.get("Name"), ((Long) object.get("Score")).intValue());
                    classifica.add(record);
                }
            } catch (ParseException e) {
                e.printStackTrace();
                classifica = null;
            }
            return classifica;
        }catch(IOException e) {
            return null;
        }
    }

    public String richiestaSfida(String sfidante) {
        try {
            writer.write("sfida\n");
            writer.write(sfidante + "\n");
            writer.flush();

            String result = reader.readLine();
            if (result == null) return "Errore di connessione";

            if (result.equals("error")) {
                return reader.readLine();
            }

            return result;
        } catch(IOException e) {
            return "Errore di connessione";
        }
    }

    /*
        Insieme di funzioni per la gestione della sfida
        Chiamate dalla gui quando il client è in sfida
    */
    private boolean messageRetrieved = false;
    public String startSfida() {
        try {
            messageRetrieved = false;
            String message = reader.readLine();
            if (message == null)
                return "Errore di communicazione";

            if (message.equals("error")) {
                String errorMessage = reader.readLine();
                return errorMessage;
            }
        }catch(IOException e) {
            return "Errore di communicazione";
        }

        return "ok";
    }

    public String nextMessage() {
        try {
            if(messageRetrieved) return null;
            String message = reader.readLine();
            messageRetrieved = true;
            return message;
        } catch(IOException e) {
            return "errore";
        }
    }

    public String getTimeoutMessage() {
        try {
            String message = reader.readLine();
            return message;
        } catch(IOException e) {
            return "errore";
        }
    }


    public String sendAnswer(String answer) {
        try {
            if(!messageRetrieved) return null;

            writer.write(answer + "\n");
            writer.flush();
            messageRetrieved = false;
            return reader.readLine();
        } catch(IOException e) {
            return "errore";
        }
    }

    public List<String> getResults() {
        try {
            List<String> results = new ArrayList<>();
            String esito = reader.readLine();
            if(esito.equals("attesa")) esito = reader.readLine();

            String giuste = reader.readLine();
            String sbagliate = reader.readLine();
            String mancate = reader.readLine();
            String punteggio = reader.readLine();

            results.add(esito);
            results.add(giuste);
            results.add(sbagliate);
            results.add(mancate);
            results.add(punteggio);
            return results;
        } catch(IOException e) {
            return null;
        }
    }

    public String accettaRichiesta(String sfidante) {
        try {
            writer.write("accetta_sfida\n");
            writer.write(sfidante + "\n");
            writer.flush();
            return "accettata";
        } catch(IOException e) {
            return "Errore di connessione";
        }
    }

    public String rifiutaRichiesta(String sfidante) {
        try {
            writer.write("rifiuta_sfida\n");
            writer.write(sfidante + "\n");
            writer.flush();
            return "ok";
        } catch(IOException e) {
            return "Errore di communicazione";
        }
    }

    public int close() {
        try {
            writer.write("close");
            socket.close();
            challengeThread.close();
            return 0;
        } catch(Exception e) {
            return -1;
        }
    }

    public String getName() {
        return name;
    }

    private static Client instance; 
    public static Client getInstance() {
        if(instance == null) instance = new Client();
        return instance;
    }
}