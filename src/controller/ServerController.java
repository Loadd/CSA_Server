package controller;

import model.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Random;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

//https://stackoverflow.com/a/10131449
//https://stackoverflow.com/a/13116162

public class ServerController {
    private static ArrayList<ConnectionToClient> clientList;
    private static Thread roulette;
    private static DataConnectionClient db;

    public static void main(String args[]){
        clientList = new ArrayList<>();
        db = new DataConnectionClient();

        try{
            System.out.println("Starting server on port 2222");
            ServerSocket serverSocket = new ServerSocket(2222);
            System.out.println("Server Started!");

            Thread accept = new Thread(() -> {
                while (true) {
                    try {
                        System.out.println("LISTENING FOR CLIENTS....");
                        Socket socket = serverSocket.accept();
                        System.out.println("FOUND CLIENT!");
                        System.out.println("ADDING TO LIST...");
                        clientList.add(new ConnectionToClient(socket));
                        System.out.println("ADDED!");

                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                }
            });

            System.out.println("STARTING THREAD...");
            accept.start();

        } catch (IOException e){
            e.printStackTrace();

        }

        roulette = new Thread(() -> {
            try{
                while(true){
                    Integer count = 10;

                    if(clientList.isEmpty()){
                        TimeUnit.SECONDS.sleep(1);

                    } else{
                        db.createRouletteRoll();
                        RouletteRoll roll = db.getRouletteRoll();
                        while (count != -1){
                            if (!clientList.isEmpty()) {
                                broadcast("ROULETTETIMER", count.toString());
                                TimeUnit.SECONDS.sleep(1);
                                count -= 1;

                            } else {
                                TimeUnit.SECONDS.sleep(1);
                            }

                        }
                        if (count == -1){
                            setColour(roll);
                            TimeUnit.SECONDS.sleep(4);
                            if (!clientList.isEmpty()) {
                                broadcast("ROULETTEUPDATEPREVROLLS");
                                TimeUnit.SECONDS.sleep(1);
                                System.out.println("CLEARING...");
                                broadcast("ROULETTECLEAR");
                            }
                        }
                    }
                }
            } catch (InterruptedException e){
                e.printStackTrace();
                System.out.println("INTERRUPTED");
                roulette.stop();
            }
        });

        //roulette.setDaemon(true);
        System.out.println("Starting Roulette Thread..");
        roulette.start();
        System.out.println("Roulette Thread Started!");

        Thread coinflip = new Thread(() -> {
            while(true){
                ArrayList<CoinflipGame> games = db.getFullCoinflipGames();
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!games.isEmpty()){
                    for (CoinflipGame game: games){
                        Account winner = db.getAccount(game.getWinner());
                        Float winnings = game.getBet()*2;

                        if (game.getWinner() ==  null){
                            Account player1 = db.getAccount(game.getPlayerOneId());
                            Account player2 = db.getAccount(game.getPlayerTwoId());

                            Random rand = new Random();
                            Integer n = rand.nextInt(2);

                            if (n.equals(1)){
                                db.forceCoinflipWinner(game, player1);
                                db.updateCoinflipDone(game);
                                game.setWinner(game.getPlayerOneId());

                                db.updateProfileMoney(player1, player1.getMoney()+winnings);

                            } else{
                                db.forceCoinflipWinner(game, player2);
                                db.updateCoinflipDone(game);
                                game.setWinner(game.getPlayerTwoId());

                                db.updateProfileMoney(player2, player2.getMoney()+winnings);

                            }
                        } else {
                            db.updateCoinflipDone(game);
                            db.updateProfileMoney(winner, winner.getMoney()+winnings);

                        }

                        if (!clientList.isEmpty()){
                            try {
                                TimeUnit.SECONDS.sleep(1);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            broadcast("COINFLIPSETWINNER", game);

                        }
                    }
                }
            }
        });

        //coinflip.setDaemon(true);
        System.out.println("Starting coinflip thread..");
        coinflip.start();
        System.out.println("Coinflip Thread Started!");
    }

    private static void broadcast(Object msg){
        try{
            for(ConnectionToClient client : clientList){
                client.sendMessage(msg);
            }
        } catch (ConcurrentModificationException ignored){}

    }

    public static void broadcast(Object msg, Object ob1){
        try {
            for (ConnectionToClient client : clientList) {
                client.sendMessage(msg, ob1);
            }
        } catch (ConcurrentModificationException ignored){

        }
    }

    private static void setColour(RouletteRoll i) {
        //https://stackoverflow.com/a/5887745
        Random rand = new Random();
        Integer n = rand.nextInt(100) + 1;
        String colour = "";

        if (n <= 49){
            colour  = "red";
        } else if (n <= 98){
            colour = "black";
        } else {
            colour = "green";
        }

        RouletteRoll check = db.getRouletteRoll(i.getRollId());

        if(!clientList.isEmpty()){
            if(check.getRollResult() == null) {
                broadcast("ROULETTEGIVECOLOUR", colour);

            } else{
                broadcast("ROULETTEGIVECOLOUR", check.getRollResult());

            }
        }

        if (check.getRollResult() == null){
            db.updateRouletteRoll(i.getRollId(), colour);

        } else{
            colour = check.getRollResult();

        }

        ArrayList<RouletteBet> winners = db.getRouletteBets(i.getRollId(), colour);

        for(RouletteBet bet: winners){
            Float winnings;
            if (colour.equals("green")){
                winnings = bet.getMoneyBet() * 15;
            } else {
                winnings = bet.getMoneyBet() * 2;
            }
            Account ac = db.getAccount(bet.getUserId());
            Float newMoney = ac.getMoney() + winnings;
            db.updateProfileMoney(db.getAccount(bet.getUserId()), newMoney);

        }
        if(!clientList.isEmpty()){
            broadcast("MONEYREFRESH");
        }

    }

    private static class ConnectionToClient extends Thread{
        protected Socket socket;
        private ObjectOutputStream out = null;
        private ObjectInputStream in = null;
        private LinkedBlockingDeque<Object> messages;
        private Read read;

        public ConnectionToClient(Socket client){
            this.socket = client;
            messages = new LinkedBlockingDeque<>();


            try{
                in = new ObjectInputStream(socket.getInputStream());
                out = new ObjectOutputStream(socket.getOutputStream());

            } catch (IOException e) {
                return;
            }

            this.start();
            read = new Read(messages);
            read.start();

        }

        public class Read extends Thread{
            private LinkedBlockingDeque<Object> messages;

            public Read(LinkedBlockingDeque<Object> i){
                messages = i;
            }

            public void run(){
                while(true){
                    try{
                        Object obj = in.readObject();
                        messages.put(obj);

                    } catch(IOException | ClassNotFoundException | InterruptedException e){
                        clientList.remove(this);
                        this.stop();
                        try {
                            socket.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        System.out.println("CLIENT DROPPED");

                    }
                }
            }
        }

        public void sendMessage(Object msg){
            try{
                out.writeObject(msg);
                out.flush();

            } catch (IOException e){
                clientList.remove(this);
                this.read.stop();

                try {
                    socket.close();

                } catch (IOException ex) {
                    ex.printStackTrace();

                }
                System.out.println("CLIENT DROPPED");

            }
        }

        public void sendMessage(Object msg, Object ob1) {
            try{
                out.writeObject(msg);
                out.writeObject(ob1);
                out.flush();

            } catch (IOException e){
                clientList.remove(this);
                this.read.stop();

                try {
                    socket.close();

                } catch (IOException ex) {
                    ex.printStackTrace();

                }
                System.out.println("CLIENT DROPPED");

            }
        }

        public void run(){
            Object line;

            while (true) {
                try{
                    line = messages.take();

                    if (line.equals("QUIT")) {
                        System.out.println("CLIENT LEFT");
                        clientList.remove(this);
                        this.read.stop();
                        socket.close();
                        return;

                    }else if (line.equals("HELLO")) {
                        out.writeObject("Connected to server!");
                        out.flush();

                    }else if (line.equals("GETROULETTEROLL")) {
                        out.writeObject(db.getRouletteRoll());
                        out.flush();

                    }else if (line.equals("GETROULETTEROLLFROMID")) {
                        Integer id = (Integer) messages.take();
                        out.writeObject(db.getRouletteRoll(id));
                        out.flush();

                    } else if (line.equals("GETROULETTEROLLPREVIOUS")) {
                        ArrayList<RouletteRoll> e = db.getRouletteRollPrevious();
                        out.writeObject(e);
                        out.flush();

                    } else if (line.equals("GETROULETTEBET")) {
                        Integer i = (Integer) messages.take();
                        out.writeObject(db.getRouletteBet(i));
                        out.flush();

                    } else if (line.equals("GETROULETTEBETS")) {
                        out.writeObject(db.getRouletteBets((String) messages.take()));
                        out.flush();

                    }else if (line.equals("GETROULETTEBETS2")) {
                        Integer i = (Integer) messages.take();
                        String j = (String) messages.take();
                        out.writeObject(db.getRouletteBets(i, j));
                        out.flush();

                    }else if (line.equals("GETCOINFLIPGAME")) {
                        Integer i = (Integer) messages.take();
                        out.writeObject(db.getCoinflipGame(i));
                        out.flush();

                    }else if (line.equals("GETCOINFLIPGAMES")) {
                        String i = (String) messages.take();
                        out.writeObject(db.getCoinflipGames(i));
                        out.flush();

                    }else if (line.equals("GETLATESTCOINFLIPGAME")) {
                        out.writeObject(db.getLatestCoinflipGame((String) messages.take()));
                        out.flush();

                    }else if (line.equals("GETOPENCOINFLIPGAMES")) {
                        System.out.println("OPEN COINFLIP GAMES REQUESTED");
                        System.out.println("SENDING OPEN COINFLIP GAMES...");
                        ArrayList<CoinflipGame> alist = db.getOpenCoinflipGames();
                        out.writeObject(alist);
                        out.flush();
                        System.out.println("OPENCOINFLIPGAMES SENT!");

                    }else if (line.equals("GETACCOUNT")) {
                        out.writeObject(db.getAccount());
                        out.flush();

                    }else if (line.equals("GETACCOUNT2")) {
                        String name = (String) messages.take();
                        Account acc = db.getAccount(name);
                        out.writeObject(acc);
                        out.flush();

                    }else if (line.equals("GETCHATMESSAGES")) {
                        out.writeObject(db.getChatMessages());
                        out.flush();

                    }else if (line.equals("REGISTERACCOUNT")) {
                        Account i = (Account) messages.take();
                        db.registerAccount(i);

                    }else if (line.equals("CREATEROULETTEROLL")) {
                        System.out.println("CREATE ROULETTE ROLL RECEIVED");
                        System.out.println("SENDING RESPONSE...");
                        db.createRouletteRoll();

                    }else if (line.equals("CREATECOINFLIPGAME")) {
                        CoinflipGame i = (CoinflipGame) messages.take();
                        db.createCoinflipGame(i);
                        broadcast("COINFLIPREFRESH");

                    }else if (line.equals("CREATEROULETTEBET")) {
                        RouletteBet i = (RouletteBet) messages.take();
                        db.createRouletteBet(i);

                    }else if (line.equals("CREATECHATMESSAGE")) {
                        ChatMessage i = (ChatMessage) messages.take();
                        db.createChatMessage(i);

                    }else if (line.equals("UPDATEROULETTEROLL")) {
                        Integer i = (Integer) messages.take();
                        String colour = (String) messages.take();

                        db.updateRouletteRoll(i, colour);

                    }else if (line.equals("UPDATECOINFLIPPLAYERTWO")) {
                        String i = (String) messages.take();
                        CoinflipGame j = (CoinflipGame) messages.take();
                        db.updateCoinflipPlayerTwo(i, j);
                        broadcast("COINFLIPREFRESH");

                    }else if (line.equals("UPDATEPROFILEPICTURE")) {
                        Account i = (Account) messages.take();
                        byte[] j = (byte[]) messages.take();
                        db.updateProfilePicture(i,j);

                    }else if (line.equals("UPDATEPROFILEPASSWORD")) {
                        Account i = (Account) messages.take();
                        String j = (String) messages.take();
                        db.updateProfilePassword(i, j);

                    }else if (line.equals("UPDATEPROFILEMONEY")) {
                        Account i = (Account) messages.take();
                        Float j = (Float) messages.take();
                        db.updateProfileMoney(i,j);
                        broadcast("MONEYREFRESH");

                    }else if (line.equals("UPDATEPROFILETYPE")) {
                        Account i = (Account) messages.take();
                        String j = (String) messages.take();
                        db.updateProfileType(i,j);

                    }else if (line.equals("UPDATEPROFILEBAN")) {
                        Account i = (Account) messages.take();
                        Boolean j = (Boolean) messages.take();
                        db.updateProfileBan(i,j);

                    }else if (line.equals("DELETECHATMESSAGE")) {
                        ChatMessage i = (ChatMessage) messages.take();
                        Boolean j = (Boolean) messages.take();
                        db.deleteChatMessage(i,j);

                    }else if (line.equals("TIMEOUTACCOUNT")) {
                        Account i = (Account) messages.take();
                        db.timeoutAccount(i);

                    }else if (line.equals("CHATREFRESH")) {
                        broadcast("CHATREFRESH");

                    }else if (line.equals("FORCECOINFLIPWINNER")) {
                        CoinflipGame i = (CoinflipGame) messages.take();
                        Account j = (Account) messages.take();
                        db.forceCoinflipWinner(i,j);

                    }else if (line.equals("OVERRIDEROULETTECOLOUR")) {
                        RouletteRoll i = (RouletteRoll) messages.take();
                        String j = (String) messages.take();
                        db.overrideRouletteColour(i,j);

                    } else if (line.equals("ROULETTEBETREFRESH")){
                        broadcast("ROULETTEBETREFRESH");

                    } else{
                        System.out.println("COMMAND UNRECOGNISED -> " + line);
                        messages.put(line);

                    }
                } catch(IOException | InterruptedException e) {
                    e.printStackTrace();
                    return;

                }
            }
        }
    }
}
