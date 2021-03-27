import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * 服务器界面
 */
public class ServerUI extends JFrame {
    Server serverLogic;
    int lwidth = 200,
            lheight = 60,
            fwidth = 100,
            fheight = 30;
    JButton startButton = new JButton("启动");
    JButton mangeFileButton = new JButton("管理文件库");
    JButton manageAcountButton  = new JButton("管理账号");
    static JTextArea docArea = new JTextArea(1,1);

    JTextField serverField = new JTextField(40);
    JTextField portField = new JTextField(40);
    JLabel serverJLabel = new JLabel("服务器");
    JLabel portJLabel = new JLabel("端口");

    static  boolean  isStart = false;

    Container containerS = getContentPane();

    public ServerUI(){
        setLayout(null);
        setTitle("局域网共享系统服务端 ");
        setBounds(600, 150, 640, 600);//设置客户端大小

        startButton.setFont(new Font("黑体",Font.BOLD,15));
        startButton.setBounds(300,30,100, 50);
        serverJLabel.setFont(new Font("黑体",Font.BOLD,15));
        portJLabel.setFont(new Font("黑体",Font.BOLD,15));
        serverJLabel.setBounds(10, 0, lwidth, lheight);
        portJLabel.setBounds(10,45,lwidth,lheight);
        JScrollPane docJS = new JScrollPane(docArea);//聊天框滑动条
        docArea.setEditable(false);
        docJS.setBounds(10, 120, 600, 280);
        serverField.setBounds(120, 20, fwidth, fheight);
        portField.setBounds(120, 65, fwidth, fheight);

        serverField.setText("127.0.0.1");
        portField.setText("8080");
        containerS.add(docJS);
        containerS.add(startButton);
        containerS.add(serverField);
        containerS.add(portField);
        containerS.add(serverJLabel);
        containerS.add(portJLabel);

        setVisible(true);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(isStart) {
                    JOptionPane.showMessageDialog(null, "服务器已经启动","错误",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int port;
                try {
                    try {
                        port=Integer.parseInt(portField.getText());
                    }catch(Exception exception) {
                        throw new Exception("端口号出错");
                    }
                    if(port<=0) {
                        throw new Exception("端口号出错");
                    }
                    System.out.println("端口：" + port+" 服务器正在运行中..." );
                    docArea.append("本地服务器：127.0.0.1  端口：" + port+"    服务器正在运行中..." );
                    JOptionPane.showMessageDialog(null, "服务器启动成功");
                    start(port);
                }catch (Exception exc) {
                    JOptionPane.showMessageDialog(null, "错误",exc.getMessage(),JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
    void start(int port){
        serverLogic=new Server(port);
    }
    public static void main(String[] args) {
        new ServerUI();
    }
}
