package Client;

import Util.MD5;
import oracle.net.jdbc.TNSAddress.Address;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class LoginThread extends Thread {
    private JFrame loginf;

    private JTextField t;

    public void run() {
        /*
         * 设置登录界面
         */
        loginf = new JFrame();
        loginf.setResizable(false);
        loginf.setLocation(300, 200);
        loginf.setSize(400, 150);
        loginf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginf.setTitle("聊天室" + " - 登录");

        t = new JTextField("Version " + "1.1.0" + "        By liwei");
        t.setHorizontalAlignment(JTextField.CENTER);
        t.setEditable(false);
        loginf.getContentPane().add(t, BorderLayout.SOUTH);

        JPanel loginp = new JPanel(new GridLayout(3, 2));
        loginf.getContentPane().add(loginp);

        JTextField t1 = new JTextField("登录名:");
        t1.setHorizontalAlignment(JTextField.CENTER);
        t1.setEditable(false);
        loginp.add(t1);

        final JTextField loginname = new JTextField("liwei");//默认名字
        loginname.setHorizontalAlignment(JTextField.CENTER);
        loginp.add(loginname);

        JTextField t2 = new JTextField("密码:");
        t2.setHorizontalAlignment(JTextField.CENTER);
        t2.setEditable(false);
        loginp.add(t2);

        final JTextField loginPassword = new JTextField("lw1234");//默认密码
        loginPassword.setHorizontalAlignment(JTextField.CENTER);
        loginp.add(loginPassword);
        /*
         * 监听退出按钮(匿名内部类)
         */
        JButton b1 = new JButton("退  出");
        loginp.add(b1);
        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        final JButton b2 = new JButton("登  录");
        loginp.add(b2);

        loginf.setVisible(true);
        /**
         * 监听器,监听"登录"Button的点击和TextField的回车
         */
        class ButtonListener implements ActionListener{

            public void actionPerformed(ActionEvent event){
                String username=loginname.getText();//获取的是用户输入的名字和密码0
                String password=loginPassword.getText();

                String url="jdbc:oracle:thin:@localhost:1521:orcl";//连接数据库的账号密码
                String username_db="opts";
                String password_db="opts1234";
                try {
                    //System.out.println("AAAAAA");
                    Connection con= DriverManager.getConnection(url,username_db,password_db);//特定与数据库连接
                    String sql="select PASSWORD FROM users WHERE username=?";
                    PreparedStatement pstmt=con.prepareStatement(sql);
                    pstmt.setString(1,username);
                    ResultSet rs=pstmt.executeQuery();
                    //System.out.println(rs.next());
                    if(rs.next()){

                        String endcodePassword=rs.getString("PASSWORD");
                        if(MD5.checkpassword(password,endcodePassword)){
                            System.out.println("登陆成功");//登录成功就进入聊天室


                            //获取本机ip和开设端口：
                            InetAddress address=InetAddress.getLocalHost();//获取本机地址
                            System.out.println("本机ip地址:"+address);//上面已经连接过数据库了这里直接使用就好了
                            ServerSocket socket=new ServerSocket(8888);//开设端口
                            //传入数据库，sql语句更改;
                            String sql1 = "UPDATE users SET ip=?,port=8888 WHERE username=?";
                            //String sql1 = "UPDATE users SET ip=? , port=8888 WHERE username=?";
                            pstmt=con.prepareStatement(sql1);
                            pstmt.setString(1, address.getHostAddress());
                            pstmt.setString(2, username);
                            pstmt.executeQuery();
                            //pstmt.setString(3, String.valueOf(socket));
                            loginf.setVisible(false);//ip和端口录入数据库后关闭窗口进入聊天房间
                            ChatThreadWindow_Example login2=new ChatThreadWindow_Example();
                        }
                        else
                            System.out.println("登录失败");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        //登录键、用户名、框和密码框都设置事件
        ButtonListener buttonListener=new ButtonListener();
        b2.addActionListener(buttonListener);
        loginname.addActionListener(buttonListener);
        loginPassword.addActionListener(buttonListener);
    }

}