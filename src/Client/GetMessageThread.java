package Client;

import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class GetMessageThread extends Thread{
    //获取信息的多线程
    DatagramSocket ds;
    JTextArea ta;
    JComboBox cb;
    String name;
    public GetMessageThread(ChatThreadWindow ctw){//构造方法
        //System.out.println("!!!!!!!!!");
        this.cb=ctw.cb;
        this.ta=ctw.ta;
        this.ds=ctw.ds;//获取了已经设置好的ip和端口
    }
    public void run(){//获取消息的线程一直处于开的状态
        try{
            while(true){

                byte buff[]=new byte[1024];

                DatagramPacket dp=new DatagramPacket(buff,200);//接受的包
                ds.receive(dp);
                String message=new String(buff,0,dp.getLength());//message装载了其他端发来的信息，
                System.out.println("获取的信息："+message);

                //System.out.println("UDP接受的消息："+message);
                ta.append(message+"\n");//添加到文本区//XXX进入了聊天室

                String message2=null;
                if(message.endsWith("进入了聊天室")){//获取名字然后家到文本框之中
                    message2=message.replace("进入了聊天室","");//String 是一旦生成便不可改变的
                    System.out.println("加工后的信息:"+message2);
                }
                cb.addItem(message2);
                System.out.println(message2);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }
}
