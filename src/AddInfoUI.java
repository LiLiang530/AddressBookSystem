package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AddInfoUI extends JFrame {
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
    private JComboBox<String> sexCombo;
    private JButton addRow_btn;
    private JButton delRow_btn;
    private JButton save_btn;
    private JButton undo_btn;
    private MainUI fframe;
    private boolean if_admin;

    public AddInfoUI(MainUI faframe){
        this.ID=getID();
        this.fframe=faframe;
        this.if_admin =fframe.returnAdmin();
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
        this.tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new Object[]{"办公电话", "移动电话", "QQ","电子邮箱" ,"有效性"});
        this.table = new JTable(this.tableModel);
        JPanel panel = new JPanel();
        contentPane.add(panel, "Center");
        panel.setLayout(new GridLayout(0, 1, 0, 5));
        JPanel baseInfoPanel = null;
        baseInfoPanel = this.getUpdateBaseInfoPanel();
        this.setTitle("添加联系人信息");
        baseInfoPanel.setBorder(BorderFactory.createTitledBorder("基本信息"));
        panel.add(baseInfoPanel);
        JPanel panel_2 = new JPanel();
        panel.add(panel_2);
        panel_2.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(this.table);
        panel_2.add(scrollPane);
        panel_2.setBorder(BorderFactory.createTitledBorder("通讯信息"));
        JPanel southPanel=new JPanel();
        southPanel.setLayout(new GridLayout(1,2));
        contentPane.add(southPanel,"South");
        FlowLayout bottomLayout=new FlowLayout();
        bottomLayout.setHgap(20);
        bottomLayout.setAlignment(FlowLayout.CENTER);
        JPanel lbPanel=new JPanel();
        lbPanel.setLayout(bottomLayout);
        JPanel rbPanel=new JPanel();
        rbPanel.setLayout(bottomLayout);
        addRow_btn=new JButton("增加行");
        delRow_btn=new JButton("删除行");
        lbPanel.add(addRow_btn);
        lbPanel.add(delRow_btn);
        save_btn=new JButton("保存");
        undo_btn=new JButton("取消");
        rbPanel.add(save_btn);
        rbPanel.add(undo_btn);
        southPanel.add(lbPanel);
        southPanel.add(rbPanel);
        addAction();
        setVisible(true);
    }

    public JPanel getUpdateBaseInfoPanel(){
        JPanel baseInfoPanel = new JPanel();
        baseInfoPanel.setLayout(new GridLayout(3, 4, 5, 15));
        this.namelbl = new AddInfoUI.DefaultLabel("姓名：");
        baseInfoPanel.add(this.namelbl);
        this.nametxt = new JTextField();
        baseInfoPanel.add(this.nametxt);
        this.nametxt.setColumns(10);
        this.sexlbl = new AddInfoUI.DefaultLabel("性别：");
        baseInfoPanel.add(this.sexlbl);
        this.sexCombo = new JComboBox(new DefaultComboBoxModel(new String[]{"male", "female"}));
        baseInfoPanel.add(this.sexCombo);
        this.birthdaylbl = new AddInfoUI.DefaultLabel("<html><center>出生日期：<br/>(yyyy-mm-dd)</center></html>");
        baseInfoPanel.add(this.birthdaylbl);
        this.birthdaytxt = new JTextField();
        baseInfoPanel.add(this.birthdaytxt);
        this.companylbl = new AddInfoUI.DefaultLabel("工作单位：");
        baseInfoPanel.add(this.companylbl);
        this.companytxt = new JTextField();
        baseInfoPanel.add(this.companytxt);
        this.positionlbl = new AddInfoUI.DefaultLabel("职位：");
        baseInfoPanel.add(this.positionlbl);
        this.positiontxt = new JTextField();
        baseInfoPanel.add(this.positiontxt);
        this.addresslbl = new AddInfoUI.DefaultLabel("工作地址：");
        baseInfoPanel.add(this.addresslbl);
        this.addresstxt = new JTextField();
        baseInfoPanel.add(this.addresstxt);
        return baseInfoPanel;
    }

    public void disposed(){
        super.dispose();
        fframe.dispose();
        MainUI mframe=new MainUI(this.if_admin);
    }

    public void addAction(){
        undo_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        addRow_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String add[]={"","","","","Y"};
                tableModel.addRow(add);
            }
        });
        delRow_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selRow=table.getSelectedRow();
                if(selRow>-1){
                    tableModel.removeRow(selRow);
                }
            }
        });
        save_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (table.isEditing()){
                    table.getCellEditor().stopCellEditing();
                }
                if(checkInfo()){
                    updateData();
                    JOptionPane.showMessageDialog(AddInfoUI.this,"保存成功！");
                    disposed();
                }
            }
        });
    }

    public void updateData(){
        try {
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                System.out.println("成功加载SQL驱动程序");
            } catch (Exception exw) {
                System.out.println("找不到SQL驱动程序");
            }
            try {
                con = DriverManager.getConnection
                        ("jdbc:sqlserver://localhost:1433;DatabaseName=address", "sa", "xxxxxxxx");
                System.out.println("数据库连接成功");
            } catch (Exception exy) {
                System.out.println("数据库连接失败");
            }
            stmt = con.createStatement();

            String dbname=nametxt.getText();
            String dbsex=(String) sexCombo.getSelectedItem();
            String dbbthd=birthdaytxt.getText();
            String dbcomp=companytxt.getText();
            String dbpost=positiontxt.getText();
            String dbadrs=addresstxt.getText();
            stmt.executeUpdate
                    ("insert into info values('"+this.ID+"','"+dbname+"','"+dbsex+"','"+dbbthd+
                            "','"+dbcomp+"','"+dbpost+"','"+dbadrs+ "')");
            int count =table.getRowCount();
            stmt.executeUpdate("delete from contact where id='"+this.ID+"'");
            for (int i=0;i<count;i++){
                String dbtph=(String) table.getValueAt(i,0);
                String dbph=(String) table.getValueAt(i,1);
                String dbqq=(String) table.getValueAt(i,2);
                String dbemail=(String) table.getValueAt(i,3);
                String dbvalid=(String) table.getValueAt(i,4);
                stmt.executeUpdate("insert into contact values('"+this.ID+"','"+dbtph+"','"+dbph+"','"+
                        dbqq+"','"+dbemail+"','"+dbvalid+"')");
            }
            stmt.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public String getID(){
        String idstr="-1";
        try {
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                System.out.println("成功加载SQL驱动程序");
            } catch (Exception exw) {
                System.out.println("找不到SQL驱动程序");
            }
            try {
                con = DriverManager.getConnection
                        ("jdbc:sqlserver://localhost:1433;DatabaseName=address", "sa", "xxxxxxxx");
                System.out.println("数据库连接成功");
            } catch (Exception exy) {
                System.out.println("数据库连接失败");
            }
            stmt = con.createStatement();
            rs = stmt.executeQuery
                    ("select MAX(id) from info");
            while(rs.next()) {
                idstr=rs.getString("");
            }
            rs.close();

        } catch (SQLException ex) {
            ex.printStackTrace();

        }
        int idint=Integer.parseInt(idstr)+1;
        idstr=String.valueOf(idint);
        System.out.println(idstr);
        return idstr;
    }

    public boolean checkInfo(){
        boolean b=true;
        StringBuilder sb=new StringBuilder();
        String name=nametxt.getText();
        if ("".equals(name)||null==name){
            b=false;
            sb.append("姓名不能为空！\n");
        }
        String regex="[0-9]{4}-[0-9]{2}-[0-9]{2}";
        String birth=birthdaytxt.getText();
        if (!(birth.matches(regex)||birth.equals(""))){
            b=false;
            sb.append("日期格式错误！\n");
        }
        int count=tableModel.getRowCount();
        for (int i=0;i<count;i++){
            String telph=(String) tableModel.getValueAt(i,0);
            String ph=(String) tableModel.getValueAt(i,1);
            String qq=(String) tableModel.getValueAt(i,2);
            String avail=(String) tableModel.getValueAt(i,4);
            if (!(telph.matches("[0-9]+")||telph.equals(""))){
                b=false;
                sb.append("办公电话存在错误内容！\n");
            }
            if(!(ph.matches("[0-9]+")||ph.equals(""))){
                b=false;
                sb.append("移动电话存在错误内容！\n");
            }
            if(!(qq.matches("[0-9]+")||qq.equals(""))){
                b=false;
                sb.append("QQ号码存在错误内容！\n");
            }
            if(!(avail.equals("Y")||avail.equals("N"))){
                b=false;
                sb.append("有效性只能输入Y或者N！");
            }
        }
        if(!b){
            JOptionPane.showMessageDialog(null, sb.toString());
        }
        return b;
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
            this.setBorder(BorderFactory.createLineBorder(AddInfoUI.this.borderColor));
            this.setOpaque(true);
            this.setBackground(AddInfoUI.this.bankColor);
            this.setFont(new Font("宋体", 1, 14));
        }
    }
}
