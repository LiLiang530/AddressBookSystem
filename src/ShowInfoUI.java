package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ShowInfoUI extends JFrame{
    private Connection con=null;
    private Statement stmt=null;
    private ResultSet rs=null;
    private String ID;
    private JLabel namelbl;
    private JLabel sexlbl;
    private JLabel birthdaylbl;
    private JLabel companylbl;
    private JLabel positionlbl;
    private JLabel addresslbl;
    private JTable table;
    private Color borderColor;
    private Color bankColor;
    private DefaultTableModel tableModel;
    private JTextField nametxt;
    private JTextField companytxt;
    private JTextField positiontxt;
    private JTextField addresstxt;
    private JTextField birthdaytxt;
    private JTextField sextxt;

    public ShowInfoUI(String id){
        this.ID=id;
        this.borderColor = new Color(109, 180, 233);
        this.bankColor = new Color(228, 254, 254);
        this.setDefaultCloseOperation(2);
        Toolkit tool = Toolkit.getDefaultToolkit();
        Dimension d = tool.getScreenSize();
        setSize(600, 450);
        setLocation((d.width - this.getWidth()) / 2, (d.height - this.getHeight()) / 2);
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        this.setContentPane(contentPane);
        this.setTableInfo(this.ID);
        this.table = new JTable(this.tableModel);
        JPanel panel = new JPanel();
        contentPane.add(panel, "Center");
        panel.setLayout(new GridLayout(0, 1, 0, 5));
        JPanel baseInfoPanel = null;
        baseInfoPanel = this.getShowBaseInfoPanel();
        this.setTitle("详细信息");
        baseInfoPanel.setBorder(BorderFactory.createTitledBorder("基本信息"));
        panel.add(baseInfoPanel);
        JPanel panel_2 = new JPanel();
        panel.add(panel_2);
        panel_2.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(this.table);
        panel_2.add(scrollPane);
        panel_2.setBorder(BorderFactory.createTitledBorder("通讯信息"));
        setVisible(true);
    }

    private JPanel getShowBaseInfoPanel() {
        JPanel baseInfoPanel = new JPanel();
        baseInfoPanel.setLayout(new GridLayout(1, 2, 15, 0));
        baseInfoPanel.setBorder(BorderFactory.createTitledBorder("基本信息"));
        JPanel left = new JPanel(new GridLayout(3, 2, 0, 15));
        baseInfoPanel.add(left);
        JPanel right = new JPanel(new GridLayout(3, 2, 0, 15));
        baseInfoPanel.add(right);
        this.namelbl = new ShowInfoUI.DefaultLabel("姓名：");
        left.add(this.namelbl);
        this.nametxt = new ShowInfoUI.UnEditField();
        left.add(this.nametxt);
        this.nametxt.setColumns(10);
        this.sexlbl = new ShowInfoUI.DefaultLabel("性别：");
        right.add(this.sexlbl);
        this.sextxt = new ShowInfoUI.UnEditField();
        right.add(this.sextxt);
        this.birthdaylbl = new ShowInfoUI.DefaultLabel("<html><center>出生日期：<br/>(yyyy-mm-dd)</center></html>");
        left.add(this.birthdaylbl);
        this.birthdaytxt = new ShowInfoUI.UnEditField();
        left.add(this.birthdaytxt);
        this.companylbl = new ShowInfoUI.DefaultLabel("工作单位：");
        right.add(this.companylbl);
        this.companytxt = new ShowInfoUI.UnEditField();
        right.add(this.companytxt);
        this.positionlbl = new ShowInfoUI.DefaultLabel("职位：");
        left.add(this.positionlbl);
        this.positiontxt = new ShowInfoUI.UnEditField();
        left.add(this.positiontxt);
        this.addresslbl = new ShowInfoUI.DefaultLabel("工作地址：");
        right.add(this.addresslbl);
        this.addresstxt = new ShowInfoUI.UnEditField();
        right.add(this.addresstxt);
        setInfo(this.ID);
        return baseInfoPanel;
    }

    public void setInfo(String id_db){
        try {
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                System.out.println("成功加载SQL驱动程序");
            } catch (Exception exw) {
                System.out.println("找不到SQL驱动程序");
            }
            try {
                con = DriverManager.getConnection
                        ("jdbc:sqlserver://localhost:1433;DatabaseName=address", "sa", "2ooo0530");
                System.out.println("数据库连接成功");
            } catch (Exception exy) {
                System.out.println("数据库连接失败");
            }
            stmt = con.createStatement();
            rs = stmt.executeQuery
                    ("select id,name,sex,birthday,company,position,address from info where id='"+id_db+"'");
            while(rs.next()) {
                String dbname=rs.getString("name");
                String dbsex=rs.getString("sex");
                String dbbthd=rs.getString("birthday");
                String dbcomp=rs.getString("company");
                String dbpost=rs.getString("position");
                String dbadrs=rs.getString("address");
                this.nametxt.setText(dbname);
                this.sextxt.setText(dbsex);
                this.birthdaytxt.setText(dbbthd);
                this.companytxt.setText(dbcomp);
                this.positiontxt.setText(dbpost);
                this.addresstxt.setText(dbadrs);
            }
            rs.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void setTableInfo(String id_db){
        try {
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                System.out.println("成功加载SQL驱动程序");
            } catch (Exception exw) {
                System.out.println("找不到SQL驱动程序");
            }
            try {
                con = DriverManager.getConnection
                        ("jdbc:sqlserver://localhost:1433;DatabaseName=address", "sa", "2ooo0530");
                System.out.println("数据库连接成功");
            } catch (Exception exy) {
                System.out.println("数据库连接失败");
            }
            stmt = con.createStatement();
            rs = stmt.executeQuery
                    ("select telephone,phone,qq,email from contact where (id='"+id_db+"' and validity='Y')");
            this.tableModel = new DefaultTableModel();
            tableModel.setColumnIdentifiers(new Object[]{"办公电话", "移动电话", "QQ","电子邮箱" });
            while(rs.next()) {
                String dbtph=rs.getString("telephone");
                String dbph=rs.getString("phone");
                String dbqq=rs.getString("qq");
                String dbemail=rs.getString("email");
                tableModel.addRow(new Object[]{dbtph,dbph,dbqq,dbemail});

            }
            rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    class DefaultLabel extends JLabel {
        public DefaultLabel() {
            this.init();
        }

        public DefaultLabel(String text) {
            super(text);
            this.init();
        }

        private void init() {
            this.setHorizontalAlignment(0);
            this.setBorder(BorderFactory.createLineBorder(ShowInfoUI.this.borderColor));
            this.setOpaque(true);
            this.setBackground(ShowInfoUI.this.bankColor);
            this.setFont(new Font("宋体", 1, 14));
        }
    }

    class UnEditField extends JTextField {
        public UnEditField() {
            this.setEditable(false);
            this.setBackground(Color.WHITE);
            this.setHorizontalAlignment(0);
            this.setBorder(BorderFactory.createLineBorder(ShowInfoUI.this.borderColor));
            this.setFont(new Font("宋体", 0, 14));
            this.setForeground(Color.BLACK);
        }
    }
}
