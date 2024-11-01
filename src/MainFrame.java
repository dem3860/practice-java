import javax.swing.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        super();
        initialize();
    }

    void initialize(){
        setTitle("Main");
        setSize(600, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel label = new JLabel("ログイン成功!", SwingConstants.CENTER);
        add(label);
    }
}
