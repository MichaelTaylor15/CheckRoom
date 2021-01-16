package Client;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.*;
import java.sql.*;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


/**
 * 聊天线程
 */
class ChatThreadWindow {
    static String name;
    JComboBox cb;
    DatagramSocket ds;//设置成员变量传参过去
    static JFrame f;
    JTextArea ta;
    private JTextField tf;
    static int total;// 在线人数统计

    public ChatThreadWindow(String name,DatagramSocket ds) throws SQLException {
        /*
         * 设置聊天室窗口界面
         */
        this.ds=ds;
        this.name=name;
        f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(600, 400);
        System.out.println("one");
        f.setTitle("聊天室" + " - " + name + "     当前在线人数:" + ++total);
        f.setLocation(300, 200);
        ta = new JTextArea();
        JScrollPane sp = new JScrollPane(ta);
        ta.setEditable(false);
        tf = new JTextField();

        cb = new JComboBox();
        cb.addItem("All");
        JButton jb = new JButton("私聊窗口");

        JPanel pl = new JPanel(new BorderLayout());//排列方式

        pl.add(cb);
        pl.add(jb, BorderLayout.WEST);
        JPanel p = new JPanel(new BorderLayout());
        p.add(pl, BorderLayout.WEST);

        p.add(tf);
        f.getContentPane().add(p, BorderLayout.SOUTH);
        f.getContentPane().add(sp);
        f.setVisible(true);//可视
        //聊天室界面生成后提示XXX进入聊天室，正在聊天室;
        //群聊功能
        //写在一个方法里面
        GetMessageThread getMessageThread=new GetMessageThread(this);//这里要吧文本区和文本框传过去
        getMessageThread.start();
        show_intoChatRoom();
    }
    //链接数据库，获取状态为online的用户信息，获取他的ip和端口，然后建立通信
    public void show_intoChatRoom() throws SQLException {
        String url = "jdbc:oracle:thin:@localhost:1521:orcl";
        //String url = "jdbc:oracle:thin:@localhost:1521:orclhc";
        String username_db="opts";//数据库的用户名
        String password_db="opts1234";

        try {
            Connection con = DriverManager.getConnection(url, username_db, password_db);
            String sql = "SELECT username,ip,port FROM users WHERE status='online'";
           // String sql = "SELECT username,ip,port FROM users WHERE status='online'";
            PreparedStatement pstmt = con.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String username = rs.getString("USERNAME");
                String ip=rs.getString("IP");
                int port=rs.getInt("PORT");
                //System.out.println("从数据库获取到的ip:"+ip);//这里获取到的ip是192.168.1.x，而使用的DategramPacket使用的是计算机名
                //System.out.println("从数据库获取到的port:"+port);//需要吧字符型的ip转为字节型的ip
                //获取端口后建立通信
                //使用UDP协议通信，需要ip和port
                byte []ipb=new byte[4];//用ipb来装字节型的ip
                String regex="\\.";//本身具有转义，双写
                String str[]=ip.split(regex);//按.分割后的到一个字符数组，然后把他们分别转为字节型，再装入字节数组
                //System.out.print("ip转为字节后：");
                for(int i=0;i<str.length;i++){
                    ipb[i]=(byte)Integer.parseInt(str[i]);
                    //System.out.print(ipb[i]);
                }//获得了字节数组形式的ip
                if(!(username.equals(name))){//若获取到的名字和登录的名字不一样则，发送XXX进入了聊天室的信息
                    total++;
                    String message=name+"进入了聊天室";//这里的name是和登录的用户名不一样的
                    byte m[]=message.getBytes();//获取byte类型
                    DatagramPacket dp=new DatagramPacket(m,m.length);//构造函数byte类型，创建接收包,里面要存放ip和port
                    //System.out.println(InetAddress.getByAddress(ipb));
                    dp.setAddress(InetAddress.getByAddress(ipb));//ipb为ip的字节形式
                    dp.setPort(port);
                    ds=new DatagramSocket();
                    ds.send(dp);//将设置好端口和ip的包发出去
                    //发送到获取信息的线程，用构造方法即可穿这里的socket过去
                }

            }
        }
        catch (SQLException | UnknownHostException | SocketException e){
                  e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}