package com.gf.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Socket又称“套接字”，应用程序通常通过“套接字”向网络发出请求或者应答网络请求
 *       Socket和ServerSocket类库位于java.net包中。ServerSocket用于服务器端，Socket是建立网络连接时使用的。连接成功时，应用程序两端都会产生一个？Socket实例，操作这个实例
 *       完成所需的回话。对于一个网络连接来说，套接字是平等的，不因为在服务器或在客户端而产生不同级别，不管Socket还是ServerSocket他们的工作都是通过Socketimpl类及其子类完成的。
 * 套接字之间的连接过程可以分为四个步骤：服务器监听，客服端请求服务器，服务器确认，客户端确认，进行通信。
 * (1)服务器监听：是服务器端套接字并不是定位具体的客户端套接字，而是出于等待连接的状态，实时监控网路状态
 * (2)客户端请求：是指由客服端的套接字提出连接请求，要连接的目标是服务器端的套接字。为此，客户端的套接字必须首先描述它要连接服务器的套接字，指出服务器套接字的地址和端口号，然后就向服务器端套接字提出连接请求。
 * (3)服务器端的连接确认：是指当服务器端套接字监听到或者说接收到客服端套接字的连接请求，它就响应客户端套接字的请求，建立一个新的线程，把服务器端套接字的描述发个客户端。
 * (4)客户端连接确认：一旦客户端确认了次描述，连接就建立好了。双方开始进行通信。而服务器端套接字继续处于监听状态，继续接收其他客户端的连接请求。
 * 
 * @author huanchu
 *
 */
public class Server {

	final static int PROT = 8765;
	
	public static void main(String[] args){
		
		ServerSocket server = null;
		try {
			server = new ServerSocket(PROT);
			System.out.println("server start ..");
			//进行阻塞
			Socket socket = server.accept();
			//创建一个线程执行客户端的任务
			new Thread(new ServerHandler(socket)).start();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(null != server){
				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			server = null;
		}
		
		
	}
	
}
