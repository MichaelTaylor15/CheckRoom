package Client;

import Util.MD5;
import lianjie.LoginThread_Example;

//聊天室客户端
public class Client {
    //主方法:启动登录线程
    public static void main(String[] args) throws Exception {
        Thread login = new LoginThread();
        login.start();
        //System.out.println(MD5.encoderByMd5("wjz1234"));
        //ChatThreadWindow_Example login2=new ChatThreadWindow_Example();
        //System.out.println( MD5.encoderByMd5("41564"));
        //System.out.println(MD5.encoderByMd5("lw1234"));
    }
}