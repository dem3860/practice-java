import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {

    public LoginFrame(){
        super();
        initialize();
    }

    void initialize() {
        JPanel contentPane;
        JTextField email;
        JButton loginButton;
        JPasswordField passwordField;
        JLabel emailLabel,passwordLabel;

        setTitle("Login");
        setSize(600, 400);
        // "x"印をおされたら、Frameを終了して
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // ログインボタン作成
        loginButton = new JButton();
        loginButton.setText("Login");

        // パスワードフィールド作成
        passwordLabel = new JLabel("Password");
        passwordField = new JPasswordField();

        // テキストフィールド作成
        emailLabel = new JLabel("Email");
        email = new JTextField();

        // ログインボタンにリスナーを登録
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               String passwordInput = new String(passwordField.getPassword());
                String emailInput = email.getText();
                if (passwordInput.equals("password")&& emailInput.equals("example@example.com")) {
                    System.out.println("Login Success");
                    MainFrame mainFrame = new MainFrame();
                    mainFrame.setVisible(true);
                    dispose();
                } else {
                    System.out.println("Login Failed");
                }
            }
        });

        // パネルを作成
        contentPane = new JPanel(new GridLayout(3, 3, 10, 10));
        contentPane.add(passwordLabel);
        contentPane.add(passwordField);
        contentPane.add(emailLabel);
        contentPane.add(email);
        contentPane.add(loginButton);
        this.setContentPane(contentPane);
    }

        public static void main(String[] args) {
            // 詳しくは、次回以降解説するが、
            // main から GUI に仕事を依頼するときは、invokeLater
            // (後でやっといてね指示)を使うのが流儀。
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    new LoginFrame().setVisible(true);
                }
            });
        }
    }
