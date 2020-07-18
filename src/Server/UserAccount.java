package Server;

import java.util.ArrayList;
import java.util.List;

/**
 * Server
 *
 * @Created by Long - StudentID : 18120455
 * @Date 10/07/2020 - 9:58 AM
 * @Description
 **/
public class UserAccount {

    private String userName;
    private String password;
    private final List<HandleRequestThread> handleThreads = new ArrayList<HandleRequestThread>();

    private static final String REGEX = "/";

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserAccount(String userName, String password) {                                                                                                                                                                                                                                                                                            
        this.userName = userName;
        this.password = password;
    }

    public static UserAccount parseStringToUser(String line) {

        String[] temp = line.split(REGEX);
        return new UserAccount(temp[0], temp[1]);
    }

    public List<HandleRequestThread> getHandleThreads() {
        return handleThreads;
    }

    public static String getREGEX() {
        return REGEX;
    }

    @Override
    public String toString() {
        return userName + REGEX + password;
    }
}
