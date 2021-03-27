import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.*;
/**
 *
 *  服务端线程类
 */
public class Server {

    private FileServer fs;
    private Thread FileThread;
    private Thread MessageThread;
    static ServerSocket server = null;
    static Socket socket = null;
    static List<Socket> list = new ArrayList<Socket>();  // 存储客户端

    // 构造函数，在这里启动服务端
    public Server(int port) {
        //新建文件io类对象,用户数据库连接对象
        fs = new FileServer();
        //服务器文件传输线程
        FileThread=new Thread(new FileDeliver(fs));
        FileThread.start();

        try {
            server = new ServerSocket(8090);  // 服务器端套接字（只能建立一次）
            // 等待连接并开启相应线程
            while (true) {
                socket = server.accept();  // 等待连接
                list.add(socket);  // 添加当前客户端到列表
                // 在服务器端对客户端开启相应的线程
                MessageThread= new Thread(new ServerMessageThread(socket));
                MessageThread.start();
            }

        }catch (IOException e1) {
            e1.printStackTrace();  // 出现异常则打印出异常的位置
        }
    }

}


/**
 * 文件io类
 */
    class FileServer {
    private static final String addr_C1 = ".\\fileTest\\ServerFileC1";
    private static final String addr_C2 = ".\\fileTest\\ServerFileC2";
    private static final String addr_C3 = ".\\fileTest\\ServerFileC3";

    /**
     * 获取文件对象
     * @param fileName: 文件名
     * @param authority: 权限
     * @return: 文件对象
     */
    public File getFile(String fileName, String authority) {

        File file = null;
        String[] addrs = this.getAddr(authority);
        for (int i=0; i<3; i++) {
            if(addrs[i] != null) {
                file = new File(addrs[i]+"//"+fileName);
                if(file.exists()) {
                    break;
                }
            }
        }

        return file;
    }

    /**
     * 新增文件对象
     * @param fileName: 文件名
     * @param authority: 权限
     * @return: 文件对象
     */
    public File newFile(String fileName, String authority) {
        File directory = new File(this.getAddr(authority)[0]);
        if (!directory.exists()) {
            directory.mkdir();
        }
        File file = new File(directory.getAbsolutePath() + File.separatorChar + fileName);
        return file;
    }

    /**
     * 获取所有文件名列表
     * @param authority: 权限
     * @return: 文件名列表
     */
    public String[] allFiles(String authority) {
        ArrayList<String> nameList = new ArrayList<String>();
        String[] addrs = this.getAddr(authority);
        for (int i=0; i<addrs.length; i++) {
            if (addrs[i] != null){
                String[] buff = new File(addrs[i]).list();
                nameList.addAll( Arrays.asList(buff));
            }
        }

        return nameList.toArray(new String[0]);
    }

    /**
     * 获取相应权限文件夹的地址
     * @param authority: 权限
     * @return: 相应权限文件夹的地址的数组
     * 注：当前权限级别所对应文件夹永远在第0位
     */
    private String[] getAddr(String authority) {
        String[] addr = new String[]{null, null, null};
        switch (authority){
            case "C1" :
                addr = new String[]{addr_C1, null, null};
                break;
            case "C2" :
                addr = new String[]{addr_C2, addr_C1, null};
                break;
            case "C3" :
                addr = new String[]{addr_C3, addr_C2, addr_C1};
                break;
        }
        return addr;
    }
}
/**
 * 服务线程文件传输线程 端口号：8080
 * 客户端连接处理线程
 * 当主调函数中的accept方法捕捉到客户端发来socket连接请求时，新建该线程
 * 此线程中首先判断客户端发来的指令类型，然后提供相应的服务。
 */
    class FileDeliver implements Runnable {

    private DataInputStream dis;
    private DataOutputStream dos;
    private ServerSocket FserverSocket;
    private static final int  F_SERVER_PORT = 8080;
    private FileServer fs;           // 文件io类
    private Socket sc;
    User ServerUer ;
    private static DecimalFormat df = null;

    private String authority;

    private static final String addr = ".\\fileTest\\ServerFile";

    public FileDeliver(FileServer fs){
        this.fs = fs;
        ServerUer = new User();
    }

    /**
     * 控制函数
     * 判断客户端传来的指令类型，调用相应的指令处理函数
     * 此函数中应建立好数据传输管道，指令处理函数直接使用创建好的管道
     */
    @Override
    public void run() {
        //文件传输服务器端套接字（只能建立一次）
            try {
                FserverSocket =new ServerSocket(F_SERVER_PORT);
                while(true) {
                    sc = FserverSocket.accept();
                    dis = new DataInputStream(sc.getInputStream());
                    dos = new DataOutputStream(sc.getOutputStream());

                    // 监听客户机的指令
                    String commandWithID = dis.readUTF();
                    String command = commandWithID.split(" ")[0];
                    String ID = commandWithID.split(" ")[1];

                    // 判断用户等级
                    int id = Integer.parseInt(ID);
                    authority = ServerUer.getSeurity(id);
                    System.out.println(ID + "连接服务器，权限为" + authority);

                    // 判断指令内容
                    if (command.equals("UPLOAD")) {
                        // 接收上传文件
                        upLoad();
                    } else if (command.equals("DOWNLOAD")) {
                        // 发放下载文件
                        downLoad();
                    } else if (command.equals("DELETE")) {
                        // 删除文件
                        delete();
                    } else if (command.equals("ALLFILES")) {
                        // 查看全部课件文件
                        allFiles();
                    } else if (command.equals("APPLY")) {
                        // 申请变更权限
                        applyAuthority(ID);
                    }else {
                        System.out.println("无效指令");
                    }

                }

            } catch (IOException IOe) {
                IOe.printStackTrace();
            }

    }

    /**
     * 指令处理函数
     * 接收客户端的上传文件
     */
    private void upLoad() {
        // 接收上传文件
        System.out.println("收到上传文件请求");
        try{
            // 文件名和长度
            String fileName = dis.readUTF();
            long fileLength = dis.readLong();

            // 通过文件io类获取file对象
            File file = fs.newFile(fileName, authority);
            FileOutputStream fos = new FileOutputStream(file);

            // 开始接收文件
            System.out.println("======== 开始接收文件 ========");
            byte[] bytes = new byte[1024];
            int length = 0;
            long progress = 0;
            while ((length = dis.read(bytes, 0, bytes.length)) != -1) {
                fos.write(bytes, 0, length);
                fos.flush();
                progress += length;
            }
            System.out.println();
            System.out.println("======== 文件接收成功 [File Name：" + fileName + "] [Size：" + getFormatFileSize(fileLength) + "] ========");
            dis.close();
            dos.close();
            fos.close();

        } catch (Exception e) {

        }
    }

    /**
     * 指令处理函数
     * 向客户端下载文件
     */
    private void downLoad() {
        System.out.println("收到下载文件请求");
        try {

            String fileName = dis.readUTF();

            // 通过文件io类获取File对象
            File file = fs.getFile(fileName, authority);

            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);

                // 发送"文件名合法"消息
                dos.writeUTF("SUCCESS");
                dos.flush();

                // 文件长度
                dos.writeLong(file.length());
                dos.flush();

                // 开始传输文件
                System.out.println("======== 开始下发文件 ========");
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
                System.out.println("======== 文件下发成功 ========");
                dis.close();
                dos.close();
                fis.close();
            } else {
                // 发送"文件名非法"消息
                dos.writeUTF("FAIL");
                dos.flush();
                System.out.println("客户端发送的文件名无效");
            }

        } catch (Exception e) {

        }
    }

    /**
     * 指令处理函数
     * 删除文件
     */
    private void delete() {
        System.out.println("收到删除文件请求");
        try {

            String fileName = dis.readUTF();

            // 通过文件io类获取File对象
            File file = fs.getFile(fileName, authority);

            if (file.exists()) {

                file.delete();

                // 发送"删除成功"消息
                dos.writeUTF("SUCCESS");
                dos.flush();

                dis.close();
                dos.close();
            } else {
                // 发送"文件名非法"消息
                dos.writeUTF("FAIL");
                dos.flush();
                System.out.println("客户端发送的文件名无效");
            }

        } catch (Exception e) {

        }
    }

    /**
     * 指令处理函数
     * 返回服务端的全部文件名
     */
    private void allFiles() {
        String[] nameList = fs.allFiles(authority);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(sc.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(sc.getInputStream());

            oos.writeObject(nameList);
            oos.flush();
            oos.close();
        } catch (IOException IOe) {
            IOe.printStackTrace();
        }
    }
    /**
     * 指令处理函数
     * 更改权限
     * @param ID: 申请客户端的ID
     */
    private void applyAuthority(String ID) {

        try {
            DataOutputStream dos = new DataOutputStream(sc.getOutputStream());
            DataInputStream dis = new DataInputStream(sc.getInputStream());

            // 获取待申请的新权限等级
            String newAuthority = dis.readUTF();

            System.out.print(ID+"正在申请权限："+newAuthority+" 通过?Y/N:");

            //int isApply = JOptionPane.showConfirmDialog(null, ID+"正在申请权限："+newAuthority+" 通过?", "提示", JOptionPane.YES_NO_CANCEL_OPTION);
            int isApply = 0;
            if(isApply==0) {
                // 操作数据库
                //am.updateAuthority(ID, newAuthority);
                int id = Integer.parseInt(ID);
                ServerUer.UpdateSeurity(id, newAuthority);

                dos.writeUTF("APPROVE");
                dos.flush();
                System.out.println("已批准");
            } else{
                dos.writeUTF("REJECT");
                dos.flush();
                System.out.println("已驳回");
            }

            dos.close();
            dis.close();
        } catch (IOException IOe) {
            IOe.printStackTrace();
        }
    }


    /**
     * 服务函数
     * 格式化文件大小
     * @param length: 文件长度（字节数）
     * @return: 格式化进度
     */
    private String getFormatFileSize(long length) {
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
/**
 * 服务器消息发送线程 端口号：8090
 * 用于服务器端读取客户端的信息，并把信息发送给所有客户端
 */
    class ServerMessageThread implements Runnable{

    Socket nowSocket = null;
    BufferedReader in =null;
    PrintWriter out = null;

    public   ServerMessageThread(Socket s){
        this.nowSocket = s;
    }
    public void run() {

            try {
                in = new BufferedReader(new InputStreamReader(nowSocket.getInputStream()));  // 输入流
                // 获取客户端信息并把信息发送给所有客户端
                while (true) {
                    String str = in.readLine();
                    // 发送给所有客户端
                    for(Socket socket: Server.list) {
                        out = new PrintWriter(socket.getOutputStream());  // 对每个客户端新建相应的socket套接字
                        if(socket == nowSocket) {  // 发送给当前客户端
                            out.println("(你)" + str);
                        }
                        else {  // 发送给其它客户端
                            out.println(str);
                        }
                        out.flush();  // 清空out中的缓存
                    }
                }
            } catch (IOException IOe) {
                IOe.printStackTrace();
            }

    }



}