package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.awt.*;

public class LoginUI extends JFrame implements ActionListener{
    private JPanel contentPane;
    private JLabel idlbl;
    private JLabel pswlbl;
    private JTextField idt;
    private JTextField pswt;
    private JButton logbtn;
    private Connection con=null;
    private Statement stmt=null;
    private ResultSet rs=null;
    private boolean if_success=false;
    private boolean if_admin=false;

    public LoginUI(){
        setTitle("登录通讯录系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300,200);
        Toolkit tool=Toolkit.getDefaultToolkit();
        Dimension d=tool.getScreenSize();
        setLocation((d.width-getWidth())/2,(d.height-getHeight())/2);
        contentPane=new JPanel();
        setContentPane(contentPane);
        contentPane.setBorder(new EmptyBorder(5,5,5,5));
        contentPane.setLayout(new BorderLayout());
        idlbl=new JLabel("用户名：");
        pswlbl=new JLabel("密码：");
        idt=new JTextField(15);
        pswt=new JPasswordField(15);
        logbtn=new JButton("登录");
        JPanel p1=new JPanel();
        Box boxh=Box.createHorizontalBox();
        Box boxv1=Box.createVerticalBox();
        Box boxv2=Box.createVerticalBox();
        p1.setLayout(new FlowLayout());
        boxv1.add(Box.createVerticalStrut(20));
        boxv1.add(idlbl);
        boxv2.add(Box.createVerticalStrut(20));
        boxv2.add(idt);
        boxv1.add(Box.createVerticalStrut(20));
        boxv1.add(pswlbl);
        boxv2.add(Box.createVerticalStrut(20));
        boxv2.add(pswt);
        boxh.add(boxv1);
        boxh.add(Box.createHorizontalStrut(10));
        boxh.add(boxv2);
        p1.add(boxh);
        contentPane.add(p1,BorderLayout.CENTER);
        JPanel p2=new JPanel();
        p2.add(logbtn,BorderLayout.CENTER);
        contentPane.add(p2,BorderLayout.SOUTH);
        setVisible(true);
        logbtn.addActionListener(this);
    }

    public void actionPerformed(ActionEvent arg0){
        String jid = idt.getText();
        String jpsw = pswt.getText();
        try {
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                System.out.println("成功加载SQL驱动程序");
            } catch (Exception exw) {
                System.out.println("找不到SQL驱动程序");
            }
            try {
                con = DriverManager.getConnection
                        ("jdbc:sqlserver://localhost:1433;DatabaseName=address","sa","2ooo0530");
                System.out.println("数据库连接成功");
            } catch (Exception exy) {
                System.out.println("数据库连接失败");
            }
            stmt = con.createStatement();
            String sqlStr ="select id,psw from userinfo where id='"+jid+"'";
            rs = stmt.executeQuery(sqlStr);
            while (rs.next()) {
                if (jid.equals(rs.getString("id")) && jpsw.equals(rs.getString("psw"))) {
                    if_success = true;
                }
                if ("admin".equals(rs.getString("id"))&&if_success){
                    if_admin = true;
                }
            }
            rs.close();
            if (if_success == true) {
                MainUI mframe = new MainUI(if_admin);
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "用户名、密码错误，登录失败！");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String args[]){
        LoginUI lframe= new LoginUI();
    }
}
