import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
/**
 * 登录界面和注册界面
 */

@SuppressWarnings("serial")
public class InitialPage extends JFrame {

	User loginUser = new User();
	ClientUI clientPage;

	JTextField userIdText = new JTextField();
	JPasswordField userPasswordText = new JPasswordField();
	JButton buttonEnroll = new JButton("登录");
	JButton buttonregister = new JButton("注册");

	Container containerE = getContentPane();

	public InitialPage() {

		setLayout(null);
		setVisible(true);
		setBounds(600, 320, 500, 400);
		setTitle("局域网文件共享系统");
		setDefaultCloseOperation(HIDE_ON_CLOSE);

		JLabel nameLabel = new JLabel("账号:");
		JLabel passWordLabel = new JLabel("密码:");


		nameLabel.setBounds(80, 20, 120, 60);
		nameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
		passWordLabel.setBounds(80, 100, 120, 60);
		passWordLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
		buttonregister.setBounds(80, 200, 120, 60);
		buttonEnroll.setBounds(280, 200, 120, 60);
		userIdText.setBounds(200, 30, 200, 40);
		userPasswordText.setBounds(200, 120, 200, 40);

		containerE.add(userIdText);
		containerE.add(userPasswordText);
		containerE.add(buttonEnroll);
		containerE.add(buttonregister);
		containerE.add(nameLabel);
		containerE.add(passWordLabel);

		buttonregister.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				new Register();
			}
		});
		buttonEnroll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String IdString = userIdText.getText();
				int id = Integer.parseInt(IdString);
				String passWordInputString = new String (userPasswordText.getPassword());
				String passWordValidateString = loginUser.getValid(id);
				if (passWordValidateString.trim().equals(passWordInputString.trim())) {
					setVisible(false);
					JOptionPane.showMessageDialog(null, "恭喜您登录成功！","消息",JOptionPane.INFORMATION_MESSAGE);
					String nameString = loginUser.getNameString(id);
					System.out.println(nameString);
					clientPage = new ClientUI(id);
				}
				else {
					JOptionPane.showMessageDialog(null, "对不起您的用户名或密码错误！","错误",JOptionPane.ERROR_MESSAGE);
				}
			}
		});

	}

	public static void main(String[] args) {
		InitialPage page = new InitialPage();
	}

	class Register extends JFrame{

		User registerUser = new User();

		JTextField nameIdField =new JTextField();
		JPasswordField passwordField =new JPasswordField();
		JPasswordField repasswordField =new JPasswordField();
		JTextField nameField =new JTextField();
		JTextField identityField =new JTextField("提示：学生输入1、助教输入2、教授输入3");

		JLabel nameIdJLabel = new JLabel("学号/职工号:");
		JLabel passwordJLabel = new JLabel("密码:");
		JLabel repasswordJLabel = new JLabel("再次确认密码:");
		JLabel nameJLabel = new JLabel("姓名:");
		JLabel identityJLabel  = new JLabel("身份:");

		JButton registerButton = new JButton("注册");
		JButton returnButton = new JButton("返回");

		JPanel jPanel =new JPanel();

		Container containerR = getContentPane();

		public Register() {
			setLayout(null);
			setVisible(true);
			setTitle("注册你的账号");
			setBounds(750, 320, 600, 500);

			registerButton.setBounds(100, 300, 120, 60);
			returnButton.setBounds(350, 300, 120, 60);


			nameIdField.setBounds(150, 30, 260, 40);
			passwordField.setBounds(150, 80, 260, 40);
			repasswordField.setBounds(150, 130, 260, 40);
			nameField.setBounds(150, 180, 260, 40);
			identityField.setBounds(150, 230, 260, 40);


			nameIdJLabel.setBounds(10, 20, 200, 60);
			nameIdJLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
			passwordJLabel.setBounds(10,70,200,60);
			passwordJLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
			repasswordJLabel.setBounds(10, 120, 200, 60);
			repasswordJLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
			nameJLabel.setBounds(10, 170, 200, 60);
			nameJLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
			identityJLabel.setBounds(10, 220, 600, 60);
			identityJLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));


			containerR.add(registerButton);
			containerR.add(returnButton);
			containerR.add(nameIdField);
			containerR.add(passwordField);
			containerR.add(repasswordField);
			containerR.add(nameField);
			containerR.add(identityField);
			containerR.add(nameIdJLabel);
			containerR.add(passwordJLabel);
			containerR.add(repasswordJLabel);
			containerR.add(nameJLabel);
			containerR.add(identityJLabel);

			returnButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
					InitialPage.this.setEnabled(true);
					InitialPage.this.setVisible(true);
				}
			});
			registerButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String IdString = nameIdField.getText();
					String passWordString = new String (passwordField.getPassword());
					String rePassWordString = new String (passwordField.getPassword());
					String NameString = nameField.getText();
					String securityString = identityField.getText();
					int id = Integer.parseInt(IdString);
					String security = "C1";
					if (passWordString.trim().equals( rePassWordString.trim())) {
						registerUser.addUser(NameString, passWordString, id, security);
						setVisible(false);
						JOptionPane.showMessageDialog(null, "恭喜您注册成功！","消息",JOptionPane.INFORMATION_MESSAGE);
					}else {
						JOptionPane.showMessageDialog(null, "前后两次密码输入不一致","错误",JOptionPane.ERROR_MESSAGE);
					}
					setVisible(false);
					InitialPage.this.setEnabled(true);
					InitialPage.this.setVisible(true);
				}
			});

		}
	}
}
