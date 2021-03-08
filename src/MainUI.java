package UI;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainUI extends JFrame{
    private JTable table;
    private DefaultTableModel model;
    private JPanel contentPane;
    private JButton srch_btn;
    private JComboBox<String> srch_cmb;
    private JTextField srch_t;
    private Connection con=null;
    private Statement stmt=null;
    private ResultSet rs=null;
    private boolean if_Admin=false;
    private JButton upd_btn;
    private JButton del_btn;
    private JButton add_btn;

    public MainUI(boolean b){
        setTitle("通讯录系统");
        if(b){
            setAdmin();
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Toolkit tool = Toolkit.getDefaultToolkit();
        Dimension d = tool.getScreenSize();
        setSize(800, 600);
        setLocation((d.width - this.getWidth()) / 2, (d.height - this.getHeight()) / 2);
        init();

        validate();
        addAction();
        setVisible(true);
    }

    public void init(){
        this.contentPane = new JPanel();
        this.contentPane.setBorder(new EmptyBorder(5,5,5,5));
        this.contentPane.setLayout(new BorderLayout());
        this.setContentPane(this.contentPane);
        JPanel surchPanel = new JPanel();
        FlowLayout surchLayout = new FlowLayout();
        surchLayout.setAlignment(0);
        surchPanel.setLayout(surchLayout);
        this.contentPane.add(surchPanel, "North");
        this.srch_cmb = new JComboBox();
        surchPanel.add(this.srch_cmb);
        String[] comboBoxData = new String[]{"姓名","ID"};
        DefaultComboBoxModel surchComboBoxModel = new DefaultComboBoxModel(comboBoxData);
        this.srch_cmb.setModel(surchComboBoxModel);
        this.srch_t = new JTextField();
        surchPanel.add(this.srch_t);
        this.srch_t.setColumns(10);
        this.srch_btn = new JButton("搜索");
        surchPanel.add(this.srch_btn);
        Container tablePanel = new JPanel();
        this.contentPane.add(tablePanel, "Center");
        tablePanel.setLayout(new BorderLayout());
        initTable();
        /*this.table = new JTable(model){
            public boolean isCellEditable(int row, int column)
            {
                return false;}//表格不允许被编辑
        };
        this.table.setToolTipText("鼠标双击查看详细信息");*/
        JScrollPane scrollPane = new JScrollPane(this.table);
        tablePanel.add(scrollPane, "Center");
        JLabel tipslbl = new JLabel("提示：双击鼠标查看提示信息。");
        tablePanel.add(tipslbl, "South");
        this.upd_btn=new JButton("修改");
        this.del_btn=new JButton("删除");
        this.add_btn=new JButton("添加");
        JPanel bottomPanel=new JPanel();
        FlowLayout bottomLayout=new FlowLayout();
        bottomLayout.setHgap(20);
        bottomLayout.setAlignment(FlowLayout.RIGHT);
        bottomPanel.setLayout(bottomLayout);
        bottomPanel.add(upd_btn);
        bottomPanel.add(del_btn);
        bottomPanel.add(add_btn);
        this.contentPane.add(bottomPanel,"South");
    }

    private void addAction(){
        table.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
                if (e.getClickCount()==2){
                    String idstr=(String) table.getValueAt(table.getSelectedRow(),0);
                    ShowInfoUI sframe=new ShowInfoUI(idstr);
                }
            }
        });
        upd_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index=table.getSelectedRow();
                if(if_Admin){
                    if(index>-1) {
                        String idstr = (String) table.getValueAt(index,0);
                        UpdInfoUI uframe=new UpdInfoUI(idstr,MainUI.this);
                    }
                }
                else {
                    JOptionPane.showMessageDialog(MainUI.this,"您不是管理员，无法修改！");
                }
            }
        });
        del_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index=table.getSelectedRow();
                if(if_Admin){
                    if (index>-1){
                        int comfirm=JOptionPane.showConfirmDialog(MainUI.this,"是否删除"+
                            table.getValueAt(index,1)+"?","注意！",JOptionPane.YES_NO_OPTION);
                        if(comfirm==JOptionPane.YES_OPTION){
                            String idstr=(String) table.getValueAt(index,0);
                            model.removeRow(table.getSelectedRow());
                            deleteData(idstr);
                        }
                    }
                }
                else {
                    JOptionPane.showMessageDialog(MainUI.this,"您不是管理员，无法删除！");
                }
            }
        });
        add_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(if_Admin) {
                    AddInfoUI aframe = new AddInfoUI(MainUI.this);
                }
                else {
                    JOptionPane.showMessageDialog(MainUI.this,"您不是管理员，无法添加！");
                }
            }
        });
        srch_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int srchint=srch_cmb.getSelectedIndex();
                if (srchint ==1){
                    String idstr=srch_t.getText();
                    if(isIDFound(idstr)){
                        ShowInfoUI sframe=new ShowInfoUI(idstr);
                    }
                    else{
                        JOptionPane.showMessageDialog(MainUI.this,"未找到ID为"+idstr+"的联系人！");
                    }
                }
                else {
                    String namestr=srch_t.getText();
                    String idstr=ifNameFound(namestr);
                    if(idstr.equals("")){
                        JOptionPane.showMessageDialog(MainUI.this,"未找到名字为"+namestr+"的联系人！");
                    }
                    else{
                        ShowInfoUI sframe=new ShowInfoUI(idstr);
                    }
                }

            }
        });
    }

    public void initTable() {
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
            rs = stmt.executeQuery("select id,name,sex,birthday,company,position,address from info");
            model=new DefaultTableModel();
            model.setColumnIdentifiers(new Object[]{"ID","姓名","性别","出生日期","工作单位","职位","地址"});
            while(rs.next()) {
                String dbid=rs.getString("id");
                String dbname=rs.getString("name");
                String dbsex=rs.getString("sex");
                String dbbthd=rs.getString("birthday");
                String dbcomp=rs.getString("company");
                String dbpost=rs.getString("position");
                String dbadrs=rs.getString("address");
                model.addRow(new Object[]{dbid,dbname,dbsex,dbbthd,dbcomp,dbpost,dbadrs});
            }
            rs.close();
            this.table = new JTable(model){
                public boolean isCellEditable(int row, int column)
                {
                    return false;}//表格不允许被编辑
            };
            this.table.setToolTipText("鼠标双击查看详细信息");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void setAdmin(){
        if_Admin=true;
    }

    public boolean returnAdmin(){
        return this.if_Admin;
    }

    public void deleteData(String id){
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
            stmt.executeUpdate("delete from info where id='"+id+"'");
            JOptionPane.showMessageDialog(this,"删除成功！");
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public boolean isIDFound(String id){
        boolean if_found=false;
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
            rs=stmt.executeQuery("select * from info where id='"+id+"'");
            if(rs.next()){
                if_found=true;
            }
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return if_found;
    }

    public String ifNameFound(String name){
        String id_db="";
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
            rs=stmt.executeQuery("select * from info where name='"+name+"'");
            if(rs.next()){
                id_db=rs.getString("id");
            }
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return id_db;
    }
}
