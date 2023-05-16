package model;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.*;
import java.util.ArrayList;

public class DataConnectionClient {
    private String ip = "jdbc:mysql://localhost:3306/casino";
    private String username = "root";
    private String password = "";

    public Connection connect(){
        try {
            Connection connection = DriverManager.getConnection(ip, username, null);
            return connection;
        } catch (SQLException e){
            throw new IllegalStateException("Cannot connect to the database!", e);
        }
    }

    //MAKE SURE CLOSE CONNECTION ON ALL WHEN DONE

    public RouletteRoll getRouletteRoll() {
        Connection conn = this.connect();
        try(Statement stmt = conn.createStatement()) {
            //https://www.tutorialspoint.com/get-the-last-record-from-a-table-in-mysql-database-with-java
            ResultSet rs = stmt.executeQuery("SELECT * FROM tblRouletteRoll ORDER BY id DESC LIMIT 1;");
            ArrayList<RouletteBet> bets = new ArrayList<>();

            if (rs.next()) {
                Integer id = rs.getInt(1);
                String rollResult = rs.getString(2);

                RouletteRoll a = new RouletteRoll(id, rollResult);
                conn.close();
                return a;

            } else {
                RouletteRoll a = new RouletteRoll(-1, "");
                conn.close();
                return a;
            }
        }  catch (SQLException exc) {
            exc.printStackTrace();
            return new RouletteRoll();

        } finally {
            try{conn.close();} catch (SQLException e) {}
        }
    }

    public RouletteRoll getRouletteRoll(Integer i) {
        Connection conn = this.connect();
        try(Statement stmt = conn.createStatement()) {
            //https://www.tutorialspoint.com/get-the-last-record-from-a-table-in-mysql-database-with-java
            ResultSet rs = stmt.executeQuery("SELECT * FROM tblRouletteRoll WHERE id = '"+i+"'");

            if (rs.next()) {
                Integer id = rs.getInt(1);
                String rollResult = rs.getString(2);

                RouletteRoll a = new RouletteRoll(id, rollResult);
                conn.close();
                return a;

            } else {
                RouletteRoll a = new RouletteRoll(-1, "");
                conn.close();
                return a;
            }
        }  catch (SQLException exc) {
            exc.printStackTrace();
            return new RouletteRoll();

        } finally {
            try{conn.close();} catch (SQLException e) {}
        }
    }

    public ArrayList<RouletteRoll> getRouletteRollPrevious() {
        ArrayList<RouletteRoll> rolls = new ArrayList<>();
        Connection conn = this.connect();

        try(Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM tblRouletteRoll ORDER BY id DESC LIMIT 10;");

            if (rs.next()){
                do{
                    Integer id = rs.getInt(1);
                    String rollResult = rs.getString(2);

                    RouletteRoll r = new RouletteRoll(id, rollResult);
                    rolls.add(r);
                } while(rs.next());
            }
            conn.close();
            return rolls;

        } catch (SQLException exc) {
            exc.printStackTrace();
            return new ArrayList<RouletteRoll>();

        } finally {
            try{conn.close();} catch (SQLException e) {}
        }
    }

    public RouletteBet getRouletteBet(Integer i) {
        Connection conn = this.connect();
        try(Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM tblRouletteBet WHERE id = " + i);

            if (rs.first()) {
                Integer id = rs.getInt(1);
                Integer rollId = rs.getInt(2);
                String userId = rs.getString(3);
                Float moneyBet = rs.getFloat(4);
                String colour = rs.getString(5);

                RouletteBet a = new RouletteBet(id, rollId, userId, moneyBet, colour);
                conn.close();
                return a;

            } else {
                RouletteBet a = new RouletteBet(-1, -1, "", new Float(0), "");
                conn.close();
                return a;
            }
        } catch (SQLException e){
            e.printStackTrace();
            return new RouletteBet(-1, -1, "", new Float(0), "");

        } finally {
            try{conn.close();} catch (SQLException e) {}
        }
    }

    public ArrayList<RouletteBet> getRouletteBets(String i) {
        Connection conn = this.connect();
        ArrayList<RouletteBet> bets = new ArrayList<>();

        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery("SELECT * FROM tblRouletteBet WHERE userId = '" + i + "'");


            if (rs.next()){
                do{
                    Integer id = rs.getInt(1);
                    Integer rollId = rs.getInt(2);
                    String userId = rs.getString(3);
                    Float moneyBet = rs.getFloat(4);
                    String colour = rs.getString(5);

                    RouletteBet a = new RouletteBet(id, rollId, userId, moneyBet, colour);
                    bets.add(a);
                } while(rs.next());
            }
            conn.close();
            return bets;

        }catch(SQLException e){
            e.printStackTrace();
            return bets;

        } finally {
            try{conn.close();} catch (SQLException e) {}
        }
    }

    public ArrayList<RouletteBet> getRouletteBets(Integer i, String j) {
        Connection conn = this.connect();

        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery("SELECT * FROM tblRouletteBet WHERE rollId = '" + i + "' AND colour = '" + j + "'");
            ArrayList<RouletteBet> bets = new ArrayList<>();

            if (rs.next()){
                do{
                    Integer id = rs.getInt(1);
                    Integer rollId = rs.getInt(2);
                    String userId = rs.getString(3);
                    Float moneyBet = rs.getFloat(4);
                    String colour = rs.getString(5);

                    RouletteBet a = new RouletteBet(id, rollId, userId, moneyBet, colour);
                    bets.add(a);
                } while(rs.next());
            }
            conn.close();
            return bets;

        } catch (SQLException exc) {
            exc.printStackTrace();
            return new ArrayList<RouletteBet>();

        } finally {
            try{conn.close();} catch (SQLException e) {}
        }
    }

    public CoinflipGame getCoinflipGame(Integer id){
        Connection conn = this.connect();

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM tblCoinflipGame WHERE id = '" + id + "'");
            if (rs.next()){
                return new CoinflipGame(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getFloat(4),
                        rs.getString(5),
                        rs.getBoolean(6)
                );
            }
            return new CoinflipGame();

        } catch (SQLException exc){
            exc.printStackTrace();
            return new CoinflipGame();

        } finally {
            try{conn.close();} catch (SQLException e) {}
        }
    }

    public ArrayList<CoinflipGame> getCoinflipGames(String i) {
        Connection conn = this.connect();
        ArrayList<CoinflipGame> games = new ArrayList<>();

        try(Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM tblCoinflipGame WHERE winner = '"+i+"'");

            if (rs.next()) {
                do {
                    Integer id = rs.getInt(1);
                    String playerOneId = rs.getString(2);
                    String playerTwoId = rs.getString(3);
                    Float bet = rs.getFloat(4);
                    String winner = rs.getString(5);
                    Boolean done = rs.getBoolean(6);

                    CoinflipGame a = new CoinflipGame(id, playerOneId, playerTwoId, bet, winner, done);
                    games.add(a);
                } while (rs.next());
            }
            conn.close();
            return games;

        } catch(SQLException exc){
            exc.printStackTrace();
            return games;

        } finally {
            try{conn.close();} catch (SQLException e) {}
        }
    }

    public CoinflipGame getLatestCoinflipGame(String id) {
        Connection conn = this.connect();

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM tblCoinflipGame WHERE playerOneId = '" + id + "' ORDER BY id DESC LIMIT 1");
            conn.close(); //////////////////////////////////
            if (rs.next()){
                return new CoinflipGame(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getFloat(4),
                        rs.getString(5),
                        rs.getBoolean(6)
                );
            }
            return new CoinflipGame();

        } catch (SQLException exc){
            exc.printStackTrace();
            return new CoinflipGame();

        } finally {
            try{conn.close();} catch (SQLException e) {}
        }
    }

    public ArrayList<CoinflipGame> getOpenCoinflipGames() {
        Connection conn = this.connect();
        ArrayList<CoinflipGame> games = new ArrayList<>();

        try(Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM tblCoinflipGame WHERE done = FALSE");

            if (rs.next()) {
                do {
                    Integer id = rs.getInt(1);
                    String playerOneId = rs.getString(2);
                    String playerTwoId = rs.getString(3);
                    Float bet = rs.getFloat(4);
                    String winner = rs.getString(5);
                    Boolean done = rs.getBoolean(6);

                    CoinflipGame a = new CoinflipGame(id, playerOneId, playerTwoId, bet, winner, done);
                    games.add(a);
                } while (rs.next());
            }
            conn.close();
            return games;

        } catch(SQLException exc){
            exc.printStackTrace();
            return games;

        } finally {
            try{conn.close();} catch (SQLException e) {}
        }
    }

    public ArrayList<CoinflipGame> getFullCoinflipGames() {
        Connection conn = this.connect();
        ArrayList<CoinflipGame> games = new ArrayList<>();

        try(Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM tblCoinflipGame WHERE done = FALSE AND playerTwoId IS NOT NULL");

            if (rs.next()) {
                do {
                    Integer id = rs.getInt(1);
                    String playerOneId = rs.getString(2);
                    String playerTwoId = rs.getString(3);
                    Float bet = rs.getFloat(4);
                    String winner = rs.getString(5);
                    Boolean done = rs.getBoolean(6);

                    CoinflipGame a = new CoinflipGame(id, playerOneId, playerTwoId, bet, winner, done);
                    games.add(a);
                } while (rs.next());
            }
            conn.close();
            return games;

        } catch(SQLException exc){
            exc.printStackTrace();
            return games;

        } finally {
            try{conn.close();} catch (SQLException e) {}
        }
    }

    public ArrayList<Account> getAccount() {
        //https://alvinalexander.com/java/edu/pj/jdbc/jdbc0003/
        Connection conn = this.connect();
        ArrayList<Account> accounts = new ArrayList<>();
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery("SELECT * FROM tblAccount");


            if (rs.next()){
                do{
                    String username = rs.getString(1);
                    String password = rs.getString(2);
                    Blob profilePicture = rs.getBlob(3);
                    String accountType = rs.getString(4);
                    Boolean ban = rs.getBoolean(5);
                    Timestamp timeout = rs.getTimestamp(6);
                    Float money = rs.getFloat(7);
                    Account a = new Account(username, password, profilePicture.getBytes(1, (int) profilePicture.length()), accountType, ban, timeout, money);
                    accounts.add(a);
                } while(rs.next());
            }
            conn.close();
            return accounts;

        } catch(SQLException e){
            e.printStackTrace();
            return accounts;

        } finally {
            try{conn.close();} catch (SQLException e) {}
        }
    }

    public Account getAccount(String i) {
        Connection conn = this.connect();

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM tblAccount WHERE username = '" + i +"'");

            if (rs.next()) {
                String username = rs.getString(1);
                String password = rs.getString(2);
                Blob profilePicture = rs.getBlob(3);
                String accountType = rs.getString(4);
                Boolean ban = rs.getBoolean(5);
                Timestamp timeout = rs.getTimestamp(6);
                Float money = rs.getFloat(7);
                Account a = new Account(username, password, profilePicture.getBytes(1, (int) profilePicture.length()), accountType, ban, timeout, money);
                conn.close();
                return a;

            } else {
                Account a = new Account("", "", null, "", false, null, new Float(0));
                conn.close();
                return a;

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Account a = new Account("", "", null, "", false, null, new Float(0));
            return a;

        } finally {
            try{conn.close();} catch (SQLException e) {}
        }
    }


    public ArrayList<ChatMessage> getChatMessages() {
        Connection conn = this.connect();
        ArrayList<ChatMessage> messages = new ArrayList<>();

        try(Statement stmt = conn.createStatement()){
            //LIMIT HOW MANY MESSAGES CAN BE SEEN / ORDER ACCOUNTED
            //ResultSet rs = stmt.executeQuery("SELECT * FROM tblChatMessage ORDER BY id DESC, message ASC LIMIT 24");
            //SELECT * FROM (SELECT * FROM tblChatMessage ORDER BY id DESC LIMIT 24) as a ORDER BY a.id ASC
            ResultSet rs = stmt.executeQuery("SELECT * FROM (SELECT * FROM tblChatMessage ORDER BY id DESC LIMIT 23) as a ORDER BY a.id ASC");

            if (rs.next()){
                do{
                    Integer id = rs.getInt(1);
                    String userId = rs.getString(2);
                    String message = rs.getString(3);
                    Date date = rs.getDate(4);
                    Boolean removed = rs.getBoolean(5);

                    ChatMessage a = new ChatMessage(id, userId, message, date, removed);
                    messages.add(a);
                } while(rs.next());
                conn.close();
                return messages;

            }
            conn.close();
            return messages;

        } catch(SQLException exc){
            exc.printStackTrace();
            return messages;

        } finally {
            try{conn.close();} catch (SQLException e) {}
        }
    }

    public void registerAccount(Account i){
        Connection conn = this.connect();
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO tblAccount (username, password, profilePicture, accountType) VALUES (?,?,?,?)")) {
            Blob a = new SerialBlob(i.getProfilePicture());
            stmt.setString(1, i.getUsername());
            stmt.setString(2, i.getPassword());
            stmt.setBlob(3, a);
            stmt.setString(4, i.getAccountType());

            stmt.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();

        } finally {
            try{conn.close();} catch (SQLException e) {}

        }
    }

    public boolean createRouletteRoll() {
        Connection conn = this.connect();
        try (Statement stmt = conn.createStatement()){
            stmt.executeUpdate("INSERT INTO tblRouletteRoll (rollResult) VALUE (NULL)");
            conn.close();
            return true;

        } catch (SQLException exc) {
            exc.printStackTrace();
            return false;

        } finally {
            try{conn.close();} catch (SQLException e) {}
        }

    }

    public Boolean createCoinflipGame(CoinflipGame i) {
        Connection conn = this.connect();
        try(Statement stmt = conn.createStatement()){
            stmt.executeUpdate("INSERT INTO tblCoinflipGame (playerOneId, bet) VALUES ('" + i.getPlayerOneId() + "','" + i.getBet() + "')");
            conn.close(); //////////////////////////////////////////////////
            return true;

        } catch(SQLException exc){
            exc.printStackTrace();
            return false;

        } finally {
            try{conn.close();} catch (SQLException e) {}
        }
    }

    public boolean createRouletteBet(RouletteBet i) {
        Connection conn = this.connect();
        //https://stackoverflow.com/a/54113976
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("INSERT INTO tblRouletteBet (rollId, userId, moneyBet, colour) VALUES ('"
                    +i.getRollId()+"','"+i.getUserId()+"','"+i.getMoneyBet()+"','"+i.getColour()+"')");
            conn.close(); ////////////////////////////////////////////
            return true;

        } catch (SQLException exc) {
            exc.printStackTrace();
            return false;

        } finally {
            try{conn.close();} catch (SQLException e) {}
        }
    }

    public void createChatMessage(ChatMessage i) {
        Connection conn = this.connect();
        //https://stackoverflow.com/a/3350935
        Timestamp sqlDate = new Timestamp(i.getDate().getTime());
        System.out.println(i.getDate().getTime() + "");

        try(Statement stmt = conn.createStatement()){
            stmt.executeUpdate("INSERT INTO tblChatMessage (userId, message, date) VALUES ('"
                    +i.getUserId()+"','"+i.getMessage()+"','" + sqlDate + "')");
            conn.close();

        } catch (SQLException exc){
            exc.printStackTrace();

        } finally {
            try{conn.close();} catch (SQLException e) {}
        }
    }

    public void updateRouletteRoll(Integer id, String colour) {
        Connection conn = this.connect();
        try (Statement stmt = conn.createStatement()){
            stmt.executeUpdate("UPDATE tblRouletteRoll SET rollResult = '"+colour+"' WHERE id = '" + id + "'");
            conn.close();

        } catch (SQLException exc) {
            exc.printStackTrace();

        } finally {
            try{conn.close();} catch (SQLException e) {}
        }
    }

    public void updateCoinflipPlayerTwo(String playerTwoId, CoinflipGame game){
        Connection conn = this.connect();
        try (Statement stmt = conn.createStatement()){
            stmt.executeUpdate("UPDATE tblCoinflipGame SET playerTwoId = '"+playerTwoId+"' WHERE id = '" + game.getId() + "'");
            conn.close(); ////////////////////////////////////////

        } catch (SQLException exc) {
            exc.printStackTrace();

        } finally {
            try{conn.close();} catch (SQLException e) {}
        }
    }

    public void updateCoinflipDone(CoinflipGame game){
        Connection conn = this.connect();
        try (Statement stmt = conn.createStatement()){
            stmt.executeUpdate("UPDATE tblCoinflipGame SET done = TRUE WHERE id = '" + game.getId() + "'");
            conn.close();

        } catch (SQLException exc) {
            exc.printStackTrace();

        } finally {
            try{conn.close();} catch (SQLException e) {}
        }
    }

    public void updateProfilePicture(Account i, byte[] profilePicture) {
        Connection conn = this.connect();

        //https://alvinalexander.com/blog/post/jdbc/sample-jdbc-preparedstatement-sql-update/
        //https://stackoverflow.com/questions/53365680/uploading-an-image-to-a-database-with-javafx
        try(PreparedStatement ps = conn.prepareStatement("UPDATE tblAccount SET profilePicture = ? WHERE username = ?")){
            Blob a = new SerialBlob(profilePicture);
            System.out.println(a);
            ps.setBlob(1, a);
            ps.setString(2, i.getUsername());

            ps.executeUpdate();

        } catch(SQLException e){
            e.printStackTrace();

        } finally {
            try{conn.close();} catch (SQLException e) {}
        }
    }

    public void updateProfilePassword(Account i, String newPassword) {
        Connection conn = this.connect();
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("UPDATE tblAccount SET password = '" + newPassword + "' WHERE username = '" + i.getUsername() + "'");
            conn.close();

        } catch (SQLException exc){
            exc.printStackTrace();

        } finally {
            try{conn.close();} catch (SQLException e) {}
        }
    }

    public void updateProfileMoney(Account i, Float newMoney) {
        Connection conn = this.connect();
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("UPDATE tblAccount SET money = '"+newMoney+"' WHERE username = '"+i.getUsername()+"'");
            conn.close(); ////////////////////////////////

        } catch (SQLException exc) {
            exc.printStackTrace();

        } finally {
            try{conn.close();} catch (SQLException e) {}
        }
    }

    public void updateProfileType(Account i, String j) {
        Connection conn = this.connect();
        try (Statement stmt = conn.createStatement()){
            stmt.executeUpdate("UPDATE tblAccount SET accountType = '"+j+"' WHERE username = '"+i.getUsername()+"'");
            conn.close();

        } catch (SQLException e){
            e.printStackTrace();

        } finally {
            try{conn.close();} catch (SQLException e) {}
        }

    }

    public void updateProfileBan(Account i, Boolean newBan) {
        Connection conn = this.connect();
        try (Statement stmt = conn.createStatement()){
            if (newBan){
                stmt.executeUpdate("UPDATE tblAccount SET ban = '"+1+"' WHERE username = '"+i.getUsername()+"'");
            } else {
                stmt.executeUpdate("UPDATE tblAccount SET ban = '"+0+"' WHERE username = '"+i.getUsername()+"'");
            }
            conn.close();

        } catch(SQLException e){
            e.printStackTrace();

        } finally {
            try{conn.close();} catch (SQLException e) {}
        }
    }

    public void deleteChatMessage(ChatMessage i, Boolean newRemove) {
        Connection conn = this.connect();

        try (Statement stmt = conn.createStatement()){
            if (newRemove){
            stmt.executeUpdate("UPDATE tblChatMessage SET removed = '"+1+"' WHERE id = '"+i.getId()+"'");

            } else {
                stmt.executeUpdate("UPDATE tblChatMessage SET removed = '"+0+"' WHERE id = '"+i.getId()+"'");

            }
            conn.close();

        } catch (SQLException e){
            e.printStackTrace();

        } finally {
            try{conn.close();} catch (SQLException e) {}
        }
    }

    public void timeoutAccount(Account i) {
        Connection conn = this.connect();
        java.util.Date date = new java.util.Date();
        date.setMinutes(date.getMinutes() + 30);
        Timestamp sqlDate = new Timestamp((date.getTime()));

        try(Statement stmt = conn.createStatement()){
            stmt.executeUpdate("UPDATE tblaccount SET timeout = '"+sqlDate+"' WHERE username = '"+i.getUsername()+"'");
            conn.close();

        } catch(SQLException e){
            e.printStackTrace();

        } finally {
            try{conn.close();} catch (SQLException e) {}
        }
    }

    public void forceCoinflipWinner(CoinflipGame i, Account j) {
        Connection conn = this.connect();
        try (Statement stmt = conn.createStatement()){
            stmt.executeUpdate("UPDATE tblCoinflipGame SET winner = '"+j.getUsername()+"' WHERE id = '"+i.getId()+"'");
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            try{conn.close();} catch (SQLException e) {}
        }
    }

    public void overrideRouletteColour(RouletteRoll i, String j){
        Connection conn = this.connect();

        try (Statement stmt = conn.createStatement()){
            stmt.executeUpdate("UPDATE tblRouletteRoll SET rollResult = '"+j+"' WHERE id = '"+i.getRollId()+"'");
            conn.close();

        } catch (SQLException e){
            e.printStackTrace();

        } finally {
            try{conn.close();} catch (SQLException e) {}
        }
    }
}
