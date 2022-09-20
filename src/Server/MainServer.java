package Server;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainServer {

    static int port = 1024;
    int check = 1;

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(port);
            System.out.println("Сервер запущен. Ожидание подключения клиента...");
            while (true) {
                Socket socket = server.accept();
                ServerThread thread = new ServerThread(socket);
                thread.start();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}

class ServerThread extends Thread {
    final private PrintStream os; // передача
    final private BufferedReader is; // прием
    final private InetAddress addr;
    private Server.MyDBConnection mdbc;
    private java.sql.Statement stmt;
    String action;
    static int ClientsNum = 0;

    public ServerThread(Socket s) throws IOException {
        os = new PrintStream(s.getOutputStream());
        is = new BufferedReader(new InputStreamReader(s.getInputStream()));
        addr = s.getInetAddress();
    }

    public void run() {
        ClientsNum++;
        try {
            mdbc = new Server.MyDBConnection();
            mdbc.init();
            Connection conn = mdbc.getMyConnection();
            stmt = conn.createStatement();
            try {
                action = is.readLine();
                ResultSet rs = null;
                while(!action.equals("exit")) {
                    switch(action) {
                        case "adduser": {
                            System.out.println("Клиент регистрируется");
                            stmt.executeUpdate("INSERT INTO users(Username, Password, surname, client_name, " +
                                    "middle_name, birth, place_of_birth, city_id, address, home_tel, mob_tel, email, " +
                                    "workplace, work_position, marital_status_id, citizenship_id, disability_id, " +
                                    "isRetired, income, military, passport_country, passport_date, identity_num, " +
                                    "passport) VALUES ("
                                    + quotate(is.readLine()) + ", " // юсернейм
                                    + quotate(is.readLine()) + ", " // пароль
                                    + quotate(is.readLine()) + ", " // фамилия
                                    + quotate(is.readLine()) + ", " // имя
                                    + quotate(is.readLine()) + ", " // отчество
                                    + "'" + intoDate(is.readLine()) + "'" + ", " // дата рождения
                                    + quotate(is.readLine()) + ", " // место рождения
                                    + quotate(is.readLine()) + ", " // город проживания
                                    + quotate(is.readLine()) + ", " // адрес
                                    + quotate(is.readLine()) + ", " // домашний телефон
                                    + quotate(is.readLine()) + ", " // мобильный телефон
                                    + quotate(is.readLine()) + ", " // электронная почта
                                    + quotate(is.readLine()) + ", " // место работы
                                    + quotate(is.readLine()) + ", " // должность
                                    + quotate(is.readLine()) + ", " // семейное положение
                                    + quotate(is.readLine()) + ", " // гражданство
                                    + quotate(is.readLine()) + ", " // инвалидность
                                    + quotate(is.readLine()) + ", " // пенсия
                                    + "'" + intoDouble(is.readLine()) + "'" + ", " // доход
                                    + quotate(is.readLine()) + ", " // военнобязанный
                                    + quotate(is.readLine()) + ", " // страна выдачи паспорта
                                    + "'" + intoDate(is.readLine()) + "'" + ", " // дата выдачи паспорта
                                    + quotate(is.readLine()) + ", " // идентификационный номер
                                    + quotate(is.readLine()) + "); "); // паспорт
                            System.out.println("Клиент зарегистрирован");
                            break;
                        }
                        case "addDep": {
                            System.out.println("Депозит регистрируется");
                            int done = stmt.executeUpdate("INSERT INTO mydeposites(mytype, docNum, currency, dateBegin," +
                                    "dateEnd, term, summ, proc, UserID, FIO) VALUES ("
                                    + quotate(is.readLine()) + ", " // вид
                                    + quotate(is.readLine()) + ", " // номер документа
                                    + quotate(is.readLine()) + ", " // валюта
                                    + "'" + intoDate(is.readLine()) + "'" + ", " // дата начала
                                    + "'" + intoDate(is.readLine()) + "'" + ", " // дата окончания
                                    + "'" + intoInt(is.readLine()) + "'" + ", " // срок
                                    + "'" + intoDouble(is.readLine()) + "'" + ", " // сумма
                                    + "'" + intoInt(is.readLine()) + "'" + ", " // процент
                                    + "'" + intoInt(is.readLine()) + "'" + ", "  // айди клиента
                                    + quotate(is.readLine()) + "); "); // фио
                            System.out.println("Депозит зарегистрирован");
                            if (done==1) {
                                break;
                            }
                        }
                        case "addAccount":{
                            String act = "active"; String pass = "passive"; String actpass = "active-passive";
                            String tek = "3014"; String cred = "2400";

                            System.out.println("Создается текущий счет");
                            int done = stmt.executeUpdate("INSERT INTO myaccount(kod, isActive, db, cr, sl, UserID) VALUES ("
                                    + quotate(tek) + ", "
                                    + quotate(pass) + ", "
                                    + "'" + intoDouble(is.readLine()) + "', '0', '0', "
                                    + quotate(is.readLine()) + "); "); // айди клиента
                            System.out.println("Текущий счет зарегистрированы");
                            if (done == 1) {
                                break;
                            }
                        }
                        case "addAccount2":{
                            String act = "active"; String pass = "passive"; String actpass = "active-passive";
                            String tek = "3014"; String cred = "2400";

                            System.out.println("Создается процентный счет");
                            int done = stmt.executeUpdate("INSERT INTO myaccount(kod, isActive, db, cr, sl, UserID) VALUES ("
                                    + quotate(cred) + ", "
                                    + quotate(pass) + ", '0', '0', '0',"
                                    + quotate(is.readLine()) + "); ");
                            System.out.println("Процентный счет зарегистрированы");
                            if (done == 1) {
                                break;
                            }
                        }
                        case "gettable": {
                            System.out.println("Получение таблицы депозитов...");
                            int ID = Integer.parseInt(is.readLine());
                            System.out.println("ID: " + ID);
                            int count = 0;
                            rs = stmt.executeQuery("SELECT * FROM mydeposites WHERE UserID = " + ID);
                            while(rs.next()) {
                                count++;
                            }
                            System.out.println("Количество депозитов клиента: " + count);
                            os.println(count);
                            rs = stmt.executeQuery("SELECT * FROM mydeposites WHERE UserID = " + ID);
                            while(rs.next()) {
                                os.println(rs.getString("DepID").toString());
                                os.println(rs.getString("mytype").toString());
                                os.println(rs.getString("docNum").toString());
                                os.println(rs.getString("currency").toString());
                                os.println(rs.getString("dateBegin").toString());
                                os.println(rs.getString("dateEnd").toString());
                                os.println(rs.getString("term").toString());
                                os.println(rs.getString("summ").toString());
                                os.println(rs.getString("proc").toString());
                                os.println(rs.getString("UserID").toString());
                                os.println(rs.getString("FIO").toString());
                            }
                            System.out.println("Таблица депозитов получена");
                            break;
                        }
                        case "loginExists":{
                            System.out.println("Проверяем, существует ли пользователь с таким логином");
                            int countLog = 0;
                            String login = is.readLine();
                            System.out.println(login);
                            rs = stmt.executeQuery("SELECT * FROM users WHERE Username = '" + login + "';");
                            while(rs.next()) {
                                countLog++;
                            }
                            System.out.println("Клиентов с таким логином: " + countLog);
                            os.println(countLog);
                            break;
                        }
                        case "idExists":{
                            System.out.println("Проверяем, существует ли пользователь с таким логином");
                            int countLog = 0;
                            String login = is.readLine();
                            System.out.println(login);
                            rs = stmt.executeQuery("SELECT * FROM users WHERE ID = '" + login + "';");
                            while(rs.next()) {
                                countLog++;
                            }
                            System.out.println("Клиентов с таким логином: " + countLog);
                            os.println(countLog);
                            break;
                        }
                        case "deleteUser": {
                            System.out.print("Удаление клиента...");
                            int IDdel = Integer.parseInt(is.readLine());
                            System.out.println("ID: " + IDdel);
                            int done = stmt.executeUpdate("DELETE FROM users WHERE ID = " + IDdel + ";");
                            System.out.print("Клиент удалён...");
                            if (done == 1) {
                                break;
                            }
                        }
                        case "closeDay":{
                            int done = 0; int done1 = 0;
                            System.out.println("Закрытие банковского дня...");
                            int countUserId = -1;
                            rs = stmt.executeQuery("SELECT UserID FROM myaccount;");
                            ArrayList<String> arrayList = new ArrayList<>();
                            while(rs.next()) {
                                System.out.println(countUserId);
                                countUserId++;
                                String s = rs.getString("UserID").toString();
                                arrayList.add(countUserId, s);
                            }
                            countUserId = countUserId/2;
                            System.out.println("Кол-во клиентов со счетами " + countUserId);

                            String mysumm = ""; String myproc = ""; double mysummD = 0; int myprocInt = 0;
                            double procRes = 0; int j = 2;
                            int dlina = arrayList.size();
                            System.out.println("dlina mss" + dlina);
                            for (j = 2; j < dlina; j = j + 2) {
                                String myID = arrayList.get(j).trim();
                                System.out.println(myID);
                                rs = stmt.executeQuery("SELECT * FROM mydeposites WHERE UserID = '" + myID + "';");
                                while(rs.next()) {
                                    mysumm = rs.getString("summ").toString();
                                }
                                rs = stmt.executeQuery("SELECT * FROM mydeposites WHERE UserID = '" + myID + "';");
                                while(rs.next()) {
                                    myproc = rs.getString("proc").toString();
                                }
                                mysummD = new Double(mysumm);
                                myprocInt = Integer.parseInt(myproc);
                                procRes = (myprocInt * 0.01) * mysummD;
                                done = stmt.executeUpdate("UPDATE myaccount SET db = db - '" + procRes + "' WHERE accountID = 1;");
                                String kod = "2400";
                                done1 = stmt.executeUpdate("UPDATE myaccount SET cr = cr + '" + procRes + "' " +
                                        "WHERE UserID = '" + myID + "' AND kod = '" + kod + "';");
                            }
                            System.out.println("Банковский день закрыт");
                            if (done == 1 && done1 == 1) {
                                break;
                            }
                        }
                        case "getusers": {
                            System.out.println("Получение таблицы клиентов...");
                            int count = 0;
                            rs = stmt.executeQuery("SELECT * FROM users");
                            while(rs.next()) {
                                count++;
                            }
                            System.out.println("Количество клиентов в банке: " + count);
                            os.println(count);
                            rs = stmt.executeQuery("SELECT * FROM users ORDER BY surname");

                            while(rs.next()) {
                                os.println(rs.getString("ID").toString());
                                os.println(rs.getString("Username").toString());
                                os.println(rs.getString("Password").toString());
                                os.println(rs.getString("surname").toString());
                                os.println(rs.getString("client_name").toString());
                                os.println(rs.getString("middle_name").toString());
                                os.println(rs.getString("birth").toString());
                                os.println(rs.getString("place_of_birth").toString());
                                os.println(rs.getString("city_id").toString());
                                os.println(rs.getString("address").toString());
                                os.println(rs.getString("home_tel").toString());
                                os.println(rs.getString("mob_tel").toString());
                                os.println(rs.getString("email").toString());
                                os.println(rs.getString("workplace").toString());
                                os.println(rs.getString("work_position").toString());
                                os.println(rs.getString("marital_status_id").toString());
                                os.println(rs.getString("citizenship_id").toString());
                                os.println(rs.getString("disability_id").toString());
                                os.println(rs.getString("isRetired").toString());
                                os.println(rs.getString("income").toString());
                                os.println(rs.getString("military").toString());
                                os.println(rs.getString("passport_country").toString());
                                os.println(rs.getString("passport_date").toString());
                                os.println(rs.getString("identity_num").toString());
                                os.println(rs.getString("passport").toString()); // 24
                            }
                            System.out.println("Таблица клиентов получена");
                            break;
                        }
                        case "getaccounts": {
                            System.out.println("Получение таблицы счетов...");
                            int count = 0;
                            rs = stmt.executeQuery("SELECT * FROM myaccount");
                            while(rs.next()) {
                                count++;
                            }
                            System.out.println("Количество счетов в банке: " + count);
                            os.println(count);
                            rs = stmt.executeQuery("SELECT * FROM myaccount");

                            while(rs.next()) {
                                String db = rs.getString("db").toString();
                                db = new BigDecimal(db).toPlainString();
                                os.println(rs.getString("accountID").toString());
                                os.println(rs.getString("kod").toString());
                                os.println(rs.getString("isActive").toString());
                                os.println(db);
                                os.println(rs.getString("cr").toString());
                                os.println(rs.getString("UserID").toString()); // 6
                            }
                            System.out.println("Таблица счетов получена");
                            break;
                        }
                        case "test": {
                            System.out.print("Тестируемся...");
                            os.println("ok");
                            break;
                        }
                        case "login": {
                            try {
                                System.out.print("Клиент входит...");
                                String login = is.readLine();
                                String pass = is.readLine();
                                rs = stmt.executeQuery("SELECT Password FROM users WHERE Username = " + quotate(login) );
                                rs.next();
                                String truePass = rs.getString(1);
                                if(truePass.equals(pass)) {
                                    os.println("ok");
                                    rs = stmt.executeQuery("SELECT ID FROM users WHERE Username = "
                                            + quotate(login));
                                    rs.next();
                                    System.out.println(rs.getString(1));
                                    os.println(rs.getString(1));
                                    System.out.print("Клиент вошёл");
                                }
                                else os.println("no");
                            }
                            catch(SQLException e){ os.println("no"); }
                            break;
                        }
                    }
                    action = is.readLine();
                }
                System.out.println("Отключение клиента " + addr.getHostName());
            }catch (IOException e) {
                System.err.println("Отключение по ошибке");
                System.out.println(e);
            }
        }
        catch(SQLException e){System.out.println(e);}
        finally {
            System.out.println("В finally");
            disconnect();
            ClientsNum--;
            try {
                mdbc.close(stmt.getResultSet());
                mdbc.destroy();
            }
            catch (SQLException ex){System.out.println(ex);}
        }
    }

    public void disconnect() {
        try {
            if (os != null) {
                os.close();
            }
            if (is != null) {
                is.close();
            }
            System.out.println(addr.getHostName() + " отключается...");
        } catch (IOException e) { System.out.println(e);
        } finally {
            this.interrupt();
        }
    }

    public String quotate(String content) {
        return "'" + content + "'";
    }

    public Date intoDate (String dateS) {
        String pattern = "yyyy/MM/dd";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Date date = null;
        java.sql.Date sqlDate = null;
        try {
            date = format.parse(dateS);
            sqlDate = new java.sql.Date(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sqlDate;
    }

    /*public Boolean intoBoolean (String booleanS) {
        if (booleanS.equals("true")) {
            return true;
        }
        else {
            return false;
        }
    }*/

    public double intoDouble (String doubleS) {
        double d = Double.parseDouble(doubleS);
        return d;
    }

    public int intoInt (String intS) {
        int i = Integer.parseInt(intS);
        return i;
    }
}