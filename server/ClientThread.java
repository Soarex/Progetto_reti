import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;

/*
    ClientThread
    Si occupa di gestire il client a cui è stato assegnato
    Usa le funzioni della classe Server
*/
public class ClientThread extends Thread {
    private Socket socket;
    private User user;
    private UserManager userManager;
    private SfidaManager sfidaManager;

    private BufferedWriter writer;
    private BufferedReader reader;

    public ClientThread(Socket socket) {
        this.socket = socket;
        userManager = UserManager.getInstance();
        sfidaManager = SfidaManager.getInstance();
        try {
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private String command;
    public void run() {
        try {
            int res = -1;
            do {
                //Riceve i commando di login dal client
                command = reader.readLine();
                if(command == null) {
                    socket.close();
                    writer.close();
                    reader.close();
                    return;  
                } 
                
                if(command.equals("login")) res = login();
                else {
                    writer.write("error\n");
                    writer.write("Invalid command, must login first\n");
                    res = -1;
                }
                writer.flush();
            } while(res != 0);

            boolean run = true;
            while(run) {
                //Riceve commando dal client
                command = reader.readLine();
                if(command == null) {
                    socket.close();
                    writer.close();
                    reader.close();
                    return;  
                } 

                //Processa il commando
                switch(command) {
                    case "logout":
                        logout();
                    break;

                    case "aggiungi_amico":
                        aggiungiAmico();
                    break;

                    case "lista_amici":
                        listaAmici();
                    break;
                    
                    case "sfida":
                        richiestaSfida();
                    break;

                    case "accetta_sfida":
                        accettaSfida();
                    break;

                    case "rifiuta_sfida":
                        rifiutaSfida();
                    break;

                    case "punteggio":
                        punteggio();
                    break;

                    case "classifica":
                        classifica();
                    break;

                    case "close":
                        run = false;
                    break;

                    default:
                        writer.write("error\n");
                        writer.write("Command not found\n");
                    break;
                }
                writer.flush();
            }

            socket.close();
            writer.close();
            reader.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private int login() throws IOException {
        String username = reader.readLine();
        String password = reader.readLine();
        if(username == null || password == null)
            return -1;

        User registeredData = userManager.getUser(username);
        if(registeredData == null) {
            writer.write("error\n");
            writer.write("Username not found\n");
            return -2;
        }

        if(!registeredData.getPassword().equals(password)) {
            writer.write("error\n");
            writer.write("Incorrect password\n");
            return -3;
        }

        userManager.logUserIn(username, socket);
        user = userManager.getUser(username);
        writer.write("success\n");
        return 0;
    }

    private void logout() throws IOException  {
        userManager.logUserOut(user.getNickname());
        writer.write("success\n");
    }

    private int aggiungiAmico() throws IOException { 
        String friendName = reader.readLine();
        if(friendName == null) return -1;

        if(user.getFriendList().contains(friendName)) {
            writer.write("error\n");
            writer.write("User is already a friend\n");
            return -2;
        }

        User friend = userManager.getUser(friendName);
        if(friend == null) {
            writer.write("error\n");
            writer.write("User not found\n");
            return -3;
        }

        user.addFriend(friendName);
        friend.addFriend(user.getNickname());
        writer.write("success\n");
        userManager.store();
        return 0;
    }

    private void listaAmici() throws IOException {
        List<String> friendList = user.getFriendList();
        writer.write("start\n");

        for(String s : friendList)
            writer.write(s + "\n");

        writer.write("end\n");
    }

    private void punteggio() throws IOException {
        writer.write(user.getScore() + "\n");
    }

    private void classifica() throws IOException {
        writer.write(userManager.getClassifica(user.getNickname()) + "\n");
    }

    //Manda la richiesta di sfida ad un altro client
    private int richiestaSfida() throws IOException {
        //Nome avversario
        String friend = reader.readLine();
        if(friend == null) return -1;
        UserInstanceData instanceData = userManager.getInstanceData(user.getNickname());
        instanceData.isInSfida = true;

        if(!user.isFriendOf(friend)) {
            writer.write("error\n");
            writer.write("User is not a friend\n");
            instanceData.isInSfida = false;
            return -2;
        }

        if(!userManager.isLogged(friend)) {
            writer.write("error\n");
            writer.write("User is offline\n");
            instanceData.isInSfida = false;
            return -3;
        }

        UserInstanceData friendInstanceData = userManager.getInstanceData(friend);
        if(friendInstanceData.isInSfida) {
            writer.write("error\n");
            writer.write("User is busy\n");
            instanceData.isInSfida = false;
            return -4;
        }

        //Genera la sfida con le parole da tradurre e le traduzioni
        Sfida sfida = sfidaManager.createSfida(user.getNickname());

        //Manda la richiesta all'altro client
        byte[] buffer = user.getNickname().getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, friendInstanceData.socket.getInetAddress(), friendInstanceData.udpPort);
        DatagramSocket socket = new DatagramSocket();
        socket.send(packet);
        socket.close();

        long elapsedTime = 0L;
        //Attende risposta dall'altro client
        synchronized(sfida.waitForFriend) {
            try {
                long startTime = System.currentTimeMillis();
                sfida.waitForFriend.wait(10000);
                elapsedTime =  System.currentTimeMillis() - startTime;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        //Se client non risponde entro ~ 10 secondi, manda request timeout
        if(elapsedTime > 9500) {
            writer.write("error\n");
            writer.write("Request timeout\n");
            sfidaManager.removeSfida(user.getNickname());
            instanceData.isInSfida = false;
            return -5;
        }

        if(!sfida.accepted) {
            writer.write("rifiutata\n");
            sfidaManager.removeSfida(user.getNickname());
            instanceData.isInSfida = false;
            return -6;
        }

        writer.write("accettata\n");
        sfida(sfida, friend);
        return 0;
    }

    private int accettaSfida() throws IOException {
        String sfidante = reader.readLine();
        Sfida sfida = sfidaManager.getSfida(sfidante);

        if(sfida == null) {
            writer.write("error\n");
            writer.write("Sfida non trovata\n");
            return -1;
        }
        
        sfida.accepted = true;
        userManager.getInstanceData(user.getNickname()).isInSfida = true;

        //Avvisa altro clientThread
        synchronized(sfida.waitForFriend) {
            sfida.waitForFriend.notify();
        }
        sfida(sfida, sfidante);
        return 0;
    }

    private int rifiutaSfida() throws IOException {
        String sfidante = reader.readLine();
        Sfida sfida = sfidaManager.getSfida(sfidante);

        if(sfida == null) {
            writer.write("error\n");
            writer.write("Sfida non trovata\n");
            return -1;
        }

        sfida.accepted = false;
        //Avvisa altro clientThread
        synchronized(sfida.waitForFriend) {
            sfida.waitForFriend.notify();
        }
        return 0;
    }

    //Fase di sfida
    private void sfida(Sfida sfida, String sfidante) throws IOException {
        UserInstanceData instanceData = userManager.getInstanceData(user.getNickname());
        //Statistiche della sfida
        instanceData.currentSfidaStats = new Stats();

        writer.write("inizio\n");

        //Per il controllo del timeout
        socket.setSoTimeout(1000);
        long startTime = System.currentTimeMillis();
        boolean timeout = false;

        int i = 0;
        while(i < Sfida.WORD_COUNT) {
            //Manda la parola da tradurre
            writer.write(sfida.wordList.get(i) + "\n");
            writer.flush();
            String answer = "";

            //Attende risposta e controlla timeout
            while(answer.equals("")) {
                try {
                    answer = reader.readLine();
                } catch (SocketTimeoutException e) {
                    if (System.currentTimeMillis() - startTime > Sfida.TIME * 1000) {
                        socket.setSoTimeout(0);
                        timeout = true;
                        break;
                    }
                }
            }

            if(timeout) break;

            //Controlla correttezza risposta
            if(answer.equals(sfida.translatedWordList.get(i))) {
                writer.write("corretto\n");
                instanceData.currentSfidaStats.rightAnswers++;
            } else {
                writer.write("sbagliato\n");
                instanceData.currentSfidaStats.wrongAnswers++;
            }

            writer.flush();
            i++;
        }

        if(timeout) {
            instanceData.currentSfidaStats.notAnswered = Sfida.WORD_COUNT - instanceData.currentSfidaStats.wrongAnswers -
                    instanceData.currentSfidaStats.rightAnswers;
            writer.write("timeout\n");
        } else
            writer.write("finito\n");

        writer.flush();

        //Sincronizzazione sfidanti, il primo che finisce aspetta l'altro
        synchronized(sfida.endWait) {
            if(sfida.onePlayerDone) {
                sfida.endWait.notify();
            } else {
                sfida.onePlayerDone = true;

                try {
                    writer.write("attesa\n");
                    writer.flush();
                    sfida.endWait.wait();
                }catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        Stats sfidanteStats = userManager.getInstanceData(sfidante).currentSfidaStats;

        //Calcolo punteggi e risultati
        int punteggio = instanceData.currentSfidaStats.rightAnswers * 2 + instanceData.currentSfidaStats.wrongAnswers * -1;
        int punteggioAvversario = sfidanteStats.rightAnswers * 2 + sfidanteStats.wrongAnswers * -1;

        if(punteggio > punteggioAvversario) {
            writer.write("Vincitore\n");
            punteggio += 5;
        }
        else if(punteggio < punteggioAvversario)
            writer.write("Perdente\n");
        else
            writer.write("Pareggio\n");

        writer.write(instanceData.currentSfidaStats.rightAnswers + "\n");
        writer.write(instanceData.currentSfidaStats.wrongAnswers + "\n");
        writer.write(instanceData.currentSfidaStats.notAnswered + "\n");

        writer.write(punteggio + "\n");
        user.addToScore(punteggio);

        instanceData.isInSfida = false;
        userManager.store();
        socket.setSoTimeout(0);
    }
}