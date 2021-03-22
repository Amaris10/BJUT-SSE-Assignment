import java.sql.*;
/*
 * 用户对象类
 */
public class User {

    private String name;
    private String ip;
    String driver = "jdbc:mysql://127.0.0.1:3306/docshare";
    String userSql = "root";
    String passwordSql = "2000";
    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;

    public User(String name,String ip) {
        this.name = name;
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName (String name) {
        this.name=name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp (String ip) {
        this.ip=ip;
    }

    void close() {
        if(resultSet!=null) {
            try {
                resultSet.close();
            } catch (SQLException e) {

                e.printStackTrace();
            }
        }
        if(statement!=null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(connection!=null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    User(){

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("加载数据库失败");
            e.printStackTrace();
        }finally {
            close();
        }
    }
    //返回学号对应密码进行验证用户
    String getValid(int id) {
        String valid = null;
        try {
            connection = DriverManager.getConnection(driver,userSql,passwordSql);
            statement = connection.createStatement();
            resultSet=statement.executeQuery("select passw from users where studentid ='"+id+"'");
            if (resultSet.next()) {
                valid = resultSet.getString("passw");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close();
        }
        return valid;
    }
    //根据学号查询用户名
    String getNameString(int id) {
        String valid = null;
        try {
            connection = DriverManager.getConnection(driver,userSql,passwordSql);
            statement = connection.createStatement();
            resultSet=statement.executeQuery("select username from users where studentid ='"+id+"'");
            if (resultSet.next()) {
                valid = resultSet.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close();
        }
        return valid;
    }
    //返回用户权限等级
    String getSeurity(int id) {
        String valid = "";
        try {
            connection = DriverManager.getConnection(driver,userSql,passwordSql);
            statement = connection.createStatement();
            resultSet=statement.executeQuery("select securityflag from users where studentid ='"+id+"'");
            if (resultSet.next()) {
                valid = resultSet.getString("securityflag");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close();
        }
        return valid;
    }
    //修改用户权限等级
    void UpdateSeurity(int id,String newAuthority) {
        try {
            connection = DriverManager.getConnection(driver,userSql,passwordSql);
            statement = connection.createStatement();
            int result=statement.executeUpdate("update users set securityflag='"+newAuthority+"' where studentid='"+id+"'  ");
            System.out.println(result);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close();
        }
    }
    //判断注册用户是否已经被注册
    int getEnrolled(int id) {
        int valid = 0;
        try {
            connection = DriverManager.getConnection(driver,userSql,passwordSql);
            statement = connection.createStatement();
            resultSet=statement.executeQuery("select passw from users where studentid ='"+id+"'");
            if (resultSet.next()) {
                valid = 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close();
        }
        return valid;
    }
    //插入新注册用户信息
    void addUser(String name,String password,int id,String security) {
        try {
            connection = DriverManager.getConnection(driver,userSql,passwordSql);
            statement = connection.createStatement();
            int result=statement.executeUpdate("insert into users(username,passw,securityflag,studentid) values ('"+name+"','"+password+"','"+security+"','"+id+"')");
            System.out.println(result);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close();
        }
    }

}

