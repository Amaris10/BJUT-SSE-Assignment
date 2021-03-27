import javafx.stage.FileChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

/**
 * 客户端界面和文件库界面
 */
public class ClientUI extends JFrame {
    int bwidth = 100,
            bheight = 30,
            y = 370;
    //定义按键
    JButton sentButton = new JButton("发送");//发送消息
    JButton docLibButton = new JButton("文件库系统");//打开文件库
    JButton ApplyButton = new JButton("申请权限");//申请高级权限
    JButton searchButton = new JButton("查询权限");//查询权限
    JButton shareDocButton = new JButton("共享文件");//共享文件
    JFileChooser fileChooser = new JFileChooser(); //文件选择类

    JComboBox<String> comboBox = new JComboBox<>();


    static JTextArea chatArea = new JTextArea(1,1);//对话框
    static JTextArea writeArea = new JTextArea(1,1);//输入文字框
    JTextField filenameField = new JTextField(200);//发送文件（显示文件名）框


    JLabel chatJLabel = new JLabel("聊天窗口");//聊天窗口标签
    JLabel writeJLabel = new JLabel("写消息");//写消息标签

    Container containerC = getContentPane();
    
    File selectedFile;
    DocumentThread clientLogic;
    MessageThread messageThread;
    static int ID;
    static String authority="";
    static String [] FilenameList = new String[100];

    public ClientUI(int id){

        clientLogic=new DocumentThread(id);
        messageThread = new MessageThread(id);
        this.ID=id;

        setLayout(null);
        setTitle("局域网共享系统客户端");
        setBounds(600, 150, 640, 600);//设置客户端大小

        chatJLabel.setBounds(10, 50, 100, 20);
        JScrollPane chatJS = new JScrollPane(chatArea);//聊天框滑动条
        chatArea.setEditable(false);
        filenameField.setEditable(false);
        chatJS.setBounds(10, 80, 600, 280);

        containerC.add(chatJS);
        containerC.add(chatJLabel);

        comboBox.addItem("一级权限");
        comboBox.addItem("二级权限");
        comboBox.addItem("三级权限");
        comboBox.setBounds(380, 15, 120, bheight);

        containerC.add(comboBox);

        writeJLabel.setBounds(10, 420, 100, 20);
        JScrollPane writeJS = new JScrollPane(writeArea);
        writeJS.setBounds(10, 440, 600, 80);

        containerC.add(writeJS);
        containerC.add(writeJLabel);


        ApplyButton.setBounds(510, 15, bwidth, bheight);
        searchButton.setBounds(510,50 , bwidth, bheight);
        sentButton.setBounds(510, 410, bwidth, bheight);
        shareDocButton.setBounds(10, y, bwidth, bheight);
        docLibButton.setBounds(10 , 15, bwidth, bheight);
        filenameField.setBounds(110,y,300,bheight);

        containerC.add(sentButton);
        containerC.add(docLibButton);
        containerC.add(searchButton);
        containerC.add(ApplyButton);
        containerC.add(shareDocButton);
        containerC.add(filenameField);

        setVisible(true);

        clientLogic.start();
        messageThread.start();

        sentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                messageThread.Message();
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s[]={"SEARCH"};
                clientLogic.open(s);
            }
        });

        ApplyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(comboBox.getSelectedIndex());
                if(comboBox.getSelectedIndex()==0){
                   authority= "C1";
                }else if(comboBox.getSelectedIndex()==1){
                    authority= "C2";
                }else if(comboBox.getSelectedIndex()==2){
                    authority= "C3";
                }
                System.out.println(comboBox.getSelectedItem());
                String s[]={"APPLY",authority};
                clientLogic.open(s);
            }
        });

        docLibButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DocLib();
            }
        });

        filenameField.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int i = fileChooser.showOpenDialog(null);// 显示文件选择对话框
                if (i == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();// 获得选中的文件对象
                    System.out.println(selectedFile.getAbsolutePath());
                    filenameField.setText(selectedFile.getName());// 显示选中文件的名称
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        shareDocButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s[]={"UPLOAD",selectedFile.getAbsolutePath()};
                clientLogic.open(s);
                filenameField.setText(null);
            }
        });

        sentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

    }

    class DocLib extends  JFrame{
        public DocLib(){
            setBounds(700, 300,400,280);
            setTitle("文件库");
            setDefaultCloseOperation(HIDE_ON_CLOSE);
            Container container = getContentPane();
            container.setLayout(null);
            String s[]={"ALLFILES"};
            clientLogic.open(s);
            JList<String> jl = new JList<>(FilenameList);

            JScrollPane js = new JScrollPane(jl);
            js.setBounds(10,10,300,200);
            container.add(js);
            JButton downLoadbtn = new JButton("下载");
            downLoadbtn.setBounds(310,80,70,25);
            JButton deletebtn = new JButton("删除");
            deletebtn.setBounds(310,120,70,25);

            container.add(deletebtn);
            container.add(downLoadbtn);

            downLoadbtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    java.util.List<String> values = jl.getSelectedValuesList();
                    String Select="";
                    for(String tmp:values){
                        Select=tmp;
                    }
                    String s[]={"DOWNLOAD",Select};
                   clientLogic.open(s);
                    System.out.println(Select+""+"下载成功");
                    JOptionPane.showMessageDialog(null, Select+""+"下载成功");

                }
            });
            deletebtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    java.util.List<String> values = jl.getSelectedValuesList();
                    String Select="";
                    for(String tmp:values){
                        Select=tmp;
                    }
                    for(String tmp:FilenameList){
                        if(Select==tmp){
                            tmp=null;
                        }
                    }
                    String s[]={"DELETE",Select};
                    clientLogic.open(s);
                    System.out.println(Select+""+"已经删除");
                    JOptionPane.showMessageDialog(null, Select+""+"已经删除");
                    setVisible(false);
                }
            });

            setVisible(true);
        }
    }


}
