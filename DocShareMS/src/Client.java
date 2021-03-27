import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.text.DecimalFormat;
/**
 * 客户端线程类
 */
public class Client {
}

class DocumentThread extends Thread{

    private static Socket sc;
    private static final int SERVER_PORT = 8080;
    private static final String CONNSTR = "127.0.0.1";
    private static final String addr = ".\\fileTest\\ClientFile";
    private static DecimalFormat df = null;
    private static int ID;
    static User ClientUser = new User();

    public DocumentThread(int id ){
        this.ID=id;
    }

    public  void run(){

    }
    /**
     * 控制函数
     * 启动客户端，循环监听指令
     * 运行Start后，将进入循环监听指令的状态。
     * 当从输入流检测到了有效指令，就与服务器建立socket连接，并调用相关函数完成指令内容。
     * 指令执行完毕后，指令处理函数应自动关闭socket连接，程序将将恢复循环监听的状态。
     */

    public  void open (String[] s) {
        try {
            String[] command = s;

            if (command[0].equals("UPLOAD")) {
                // 上传文件
                sc = new Socket(CONNSTR, SERVER_PORT);
                upLoad(sc,command[1]);
            }
            else if(command[0].equals("DOWNLOAD")) {
                // 下载文件
                sc = new Socket(CONNSTR, SERVER_PORT);
                downLoad(sc, command[1]);
            }
            else if(command[0].equals("DELETE")) {
                // 删除文件
                sc = new Socket(CONNSTR, SERVER_PORT);
                delete(sc, command[1]);
            }
            else if(command[0].equals("ALLFILES")) {
                // 查看全部可见文件名
                sc = new Socket(CONNSTR, SERVER_PORT);
                allFiles(sc);
            }
            else if(command[0].equals("APPLY")) {
                // 申请变更权限
                sc = new Socket(CONNSTR, SERVER_PORT);
                applyAuthority(sc, command[1]);
            }else if(command[0].equals("SEARCH")) {
                //查看数据库中当前ID对应的权限
                searchAuthority();
            }else if(command[0].equals("SEND")){
                //向服务器发送信息
                sc = new Socket(CONNSTR, SERVER_PORT);
                sendMessage(sc, command[1]);
            }else if(command[0].equals("MESSAGE")){
                //接收服务器发送信息
                sc = new Socket(CONNSTR, SERVER_PORT);
                getMessage(sc);
            }
            else {
                System.out.println("无效指令");
            }

        } catch (IOException IOe) {
            IOe.printStackTrace();
        }
    }

    /**
     * 指令处理函数
     * 上传文件到服务器
     * @param sc: 建立连接后的socket对象
     */
    private  void upLoad(Socket sc,String fileName) {
        try {
            // 上传文件
            DataInputStream dis = new DataInputStream(sc.getInputStream());
            DataOutputStream dos = new DataOutputStream(sc.getOutputStream());

            dos.writeUTF("UPLOAD"+" "+ID);
            dos.flush();

            File file = new File(fileName);
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);

                // 文件名和长度
                dos.writeUTF(file.getName());
                dos.flush();
                dos.writeLong(file.length());
                dos.flush();

                // 开始传输文件
                System.out.println("======== 开始上传文件 ========");
                byte[] bytes = new byte[1024];
                int length = 0;
                long progress = 0;
                while ((length = fis.read(bytes, 0, bytes.length)) != -1) {
                    dos.write(bytes, 0, length);
                    dos.flush();
                    progress += length;
                    System.out.print("| " + (100 * progress / file.length()) + "% |");
                }
                System.out.println();
                System.out.println("======== 文件上传成功 ========");
                dis.close();
                dos.close();
                fis.close();
            } else {
                System.out.println("没有找到文件，请检查文件名");
            }
        } catch (IOException IOe) {
            IOe.printStackTrace();
        } finally {
            try {
                sc.close();
            } catch (IOException IOe) {
                IOe.printStackTrace();
            }
        }
    }

    /**
     * 指令处理函数
     * 从服务器下载文件
     * @param sc: 建立连接后的socket对象
     */
    private  void downLoad(Socket sc, String fileName) {
        try {
            DataInputStream dis = new DataInputStream(sc.getInputStream());
            DataOutputStream dos = new DataOutputStream(sc.getOutputStream());

            dos.writeUTF("DOWNLOAD"+" "+ID);
            dos.flush();

            // 向服务器请求文件名，判断文件名是否合法
            dos.writeUTF(fileName);
            dos.flush();

            if (dis.readUTF().equals("SUCCESS")) {

                // 文件长度
                long fileLength = dis.readLong();

                File directory = new File(addr);
                if (!directory.exists()) {
                    directory.mkdir();
                }
                File file = new File(directory.getAbsolutePath() + File.separatorChar + fileName);
                FileOutputStream fos = new FileOutputStream(file);

                // 开始接收文件
                System.out.println("======== 开始下载文件 ========");
                byte[] bytes = new byte[1024];
                int length = 0;
                long progress = 0;
                while ((length = dis.read(bytes, 0, bytes.length)) != -1) {
                    fos.write(bytes, 0, length);
                    fos.flush();
                    progress += length;
                    System.out.print("| " + (100 * progress / file.length()) + "% |");
                }
                System.out.println();
                System.out.println("======== 文件接收成功 [File Name：" + fileName + "] [Size：" + getFormatFileSize(fileLength) + "] ========");
                dis.close();
                dos.close();
                fos.close();
            } else {
                System.out.println("服务器中无此文件");
            }

        } catch (IOException IOe) {
            IOe.printStackTrace();
        }
    }

    /**
     * 指令处理函数
     * 从删除服务器中文件
     * @param sc: 建立连接后的socket对象
     */
    private  void delete(Socket sc, String fileName) {
        try {
            DataInputStream dis = new DataInputStream(sc.getInputStream());
            DataOutputStream dos = new DataOutputStream(sc.getOutputStream());

            dos.writeUTF("DELETE"+" "+ID);
            dos.flush();

            // 向服务器请求文件名，判断文件名是否合法
            dos.writeUTF(fileName);
            dos.flush();

            if (dis.readUTF().equals("SUCCESS")) {
                System.out.println("删除成功");
            } else {
                System.out.println("服务器中无此文件");
            }
            dis.close();
            dos.close();

        } catch (IOException IOe) {
            IOe.printStackTrace();
        }
    }

    /**
     * 指令处理函数
     * 查看服务器端所有文件名
     * @param sc: 建立连接后的socket对象
     */
    private  void allFiles(Socket sc) {
        try {

            DataOutputStream dos = new DataOutputStream(sc.getOutputStream());
            DataInputStream dis = new DataInputStream(sc.getInputStream());

            dos.writeUTF("ALLFILES"+" "+ID);
            dos.flush();

            ObjectInputStream ois = new ObjectInputStream(sc.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(sc.getOutputStream());

            String [] nameList = (String[])ois.readObject();

            ClientUI.FilenameList=nameList;

            for(int i=0; i<nameList.length; i++) {
                //System.out.println(nameList[i]);
            }

            ois.close();
            oos.close();
            dos.close();
            dis.close();

        } catch (IOException | ClassNotFoundException IOe) {
            IOe.printStackTrace();
        }
    }

    /**
     * 指令处理函数
     * 更改权限
     * @param sc: 建立连接后的socket对象
     */
    private  void applyAuthority(Socket sc, String Authority) {
        try {
            DataOutputStream dos = new DataOutputStream(sc.getOutputStream());
            DataInputStream dis = new DataInputStream(sc.getInputStream());

            dos.writeUTF("APPLY"+" "+ID);
            dos.flush();

            // 上传要申请的权限级别
            dos.writeUTF(Authority);
            dos.flush();

            if (dis.readUTF().equals("APPROVE")) {
                ClientUI.chatArea.append("\n您已被授权为" + ClientUI.authority + "权限等级");
            }else{
                ClientUI.chatArea.append("\n"+"权限申请被驳回");
            }

            dos.close();
            dis.close();

        } catch (IOException IOe) {
            IOe.printStackTrace();
        }
    }
    /**
     * 指令处理函数
     * 发送消息
     * @param sc: 建立连接后的socket对象
     */
    private  void sendMessage(Socket sc, String Message) {
        try {
            DataOutputStream dos = new DataOutputStream(sc.getOutputStream());
            DataInputStream dis = new DataInputStream(sc.getInputStream());

            dos.writeUTF("SEND"+" "+ID);
            dos.flush();

            // 上传消息
            dos.writeUTF(Message);
            dos.flush();
            if (Message.length() > 0) {
                ClientUI.chatArea.append("\n" + ClientUser.getNameString(ID)+":"+Message);
            }

            System.out.println("发送消息"+Message);

            dos.close();
            dis.close();

        } catch (IOException IOe) {
            IOe.printStackTrace();
        }
    }

    private  void getMessage(Socket sc) {
        try {
            DataOutputStream dos = new DataOutputStream(sc.getOutputStream());
            DataInputStream dis = new DataInputStream(sc.getInputStream());

            dos.writeUTF("MESSAGE" + " " + ID);
            dos.flush();

            String newMessage = dis.readUTF();
            if (newMessage.length() > 0) {
                ClientUI.chatArea.append("\n" + newMessage);
            }

            dos.close();
            dis.close();

        } catch (IOException IOe) {
            IOe.printStackTrace();
        }
    }

    /**
     * 指令处理函数
     * 查看当前客户端ID对应的数据库中权限等级
     */
    private  void searchAuthority() {
        ClientUI.chatArea.append("您当前的权限为"+ClientUser.getSeurity(ID));
    }

    /**
     * 服务函数
     * 格式化文件大小
     * @param length: 文件长度（字节数）
     * @return: 格式化进度
     */
    private  String getFormatFileSize(long length) {
        double size = ((double) length) / (1 << 30);
        if (size >= 1) {
            return df.format(size) + "GB";
        }
        size = ((double) length) / (1 << 20);
        if (size >= 1) {
            return df.format(size) + "MB";
        }
        size = ((double) length) / (1 << 10);
        if (size >= 1) {
            return df.format(size) + "KB";
        }
        return length + "B";
    }

}

class MessageThread extends Thread{

    private static final int SERVER_PORT = 8090;
    private static final String CONNSTR = "127.0.0.1";

    static Socket mySocket = null;  // 一定要加上static，否则新建线程时会清空
    static JTextArea textInput;
    static JTextArea textShow;
    static BufferedReader in = null;
    static PrintWriter out = null;
    int ID;
    static User ClientUser = new User();

    static String userName;

    public  MessageThread(int id){
        try {
            mySocket = new Socket(CONNSTR, SERVER_PORT);  // 客户端套接字
            textInput = ClientUI.writeArea;
            textShow = ClientUI.chatArea;
            this.ID = id;
            this.userName = ClientUser.getNameString(id);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run(){
        try {
            in = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));  // 输入流
            out = new PrintWriter(mySocket.getOutputStream());  // 输出流
            out.println("用户【" + userName + "】进入聊天室！");  // 发送用户名给服务器
            out.flush();  // 清空缓冲区out中的数据
            while (true) {
                String str = in.readLine();  // 获取服务端发送的信息
                textShow.append(str + '\n');  // 添加进聊天客户端的文本区域
                textShow.setCaretPosition(textShow.getDocument().getLength());  // 设置滚动条在最下面
            }
        }catch (IOException IOe) {
            IOe.printStackTrace();
        }
    }
    /**
     *客户端发送消息
     */
    public void Message() {
        String str = textInput.getText();
        // 文本框内容为空
        if("".equals(str)) {
            textInput.grabFocus();  // 设置焦点（可行）
            return;
        }
        out.println(userName + "说：" + str);  // 输出给服务端
        out.flush();  // 清空缓冲区out中的数据

        textInput.setText("");  // 清空文本框
        textInput.grabFocus();  // 设置焦点（可行）
    }
}
