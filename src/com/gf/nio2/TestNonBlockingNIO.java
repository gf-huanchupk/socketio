package com.gf.nio2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

import org.junit.Test;

/*
 * 一、使用 NIO 完成网络通信的三个核心：
 * 
 * 1. 通道（Channel）：负责连接
 * 		
 * 	   java.nio.channels.Channel 接口：
 * 			|--SelectableChannel
 * 				|--SocketChannel
 * 				|--ServerSocketChannel
 * 				|--DatagramChannel
 * 
 * 				|--Pipe.SinkChannel
 * 				|--Pipe.SourceChannel
 * 
 * 2. 缓冲区（Buffer）：负责数据的存取
 * 
 * 3. 选择器（Selector）：是 SelectableChannel 的多路复用器。用于监控 SelectableChannel 的 IO 状况
 * 
 */
public class TestNonBlockingNIO {

	@Test
	public void client() throws IOException{
		//1. 获取通道
		SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));
		
		//2. 切换成非阻塞模式
		sChannel.configureBlocking(false);
		
		//3. 分配指定大小的缓冲区
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		//4. 发送数据给服务器端
		Scanner scan = new Scanner(System.in);
		
		while(scan.hasNext()){
			String str = scan.next();
			buf.put((new Date().toString() + "\n" +str ).getBytes());
			buf.flip();
			sChannel.write(buf);
			buf.clear();
			
		}
		
		//5. 关闭通道
		sChannel.close();
		
		
	}
	
	@Test
	public void server() throws IOException{
		//1. 获取通道
		ServerSocketChannel ssChannel = ServerSocketChannel.open();
		
		//2. 切换成非阻塞式
		ssChannel.configureBlocking(false);
		
		//3. 绑定连接
		ssChannel.bind(new InetSocketAddress(9898));
		
		//4. 获取选择器
		Selector selector = Selector.open();
		
		//5. 将通道注册到选择器上，并且指定 “监听接收事情”
		ssChannel.register(selector, SelectionKey.OP_ACCEPT);
		
		//6. 轮询式的获取选择器啥上已经 “准备就绪的事件”
		while(0 < selector.select()){
			//7. 获取当前选择器中所有注册的 “选择键（已就绪的监听事件）”
			Iterator<SelectionKey> it = selector.selectedKeys().iterator();
			
			while(it.hasNext()){
				//8. 获取准备 “就绪” 的事件
				SelectionKey sk = it.next();
				//9. 判断具体是什么事件准备就绪
				if(sk.isAcceptable()){
					//10. 若“接收就绪”，获取客户端连接
					SocketChannel sChannel = ssChannel.accept();
					//11. 切换非阻塞模式
					sChannel.configureBlocking(false);
					//12. 将通道注册到选择器上
					sChannel.register(selector, SelectionKey.OP_READ);
				}else if(sk.isReadable()){
					//13. 获取当前选择器上“读就绪”状态通道
					SocketChannel sChannel = (SocketChannel) sk.channel();
					//14. 读取数据
					ByteBuffer buf = ByteBuffer.allocate(1024);
					
					int len = 0;
					while((len = sChannel.read(buf)) != -1){
						buf.flip();
						System.out.println(new String(buf.array() , 0 , len));
						buf.clear();
					}
					
				}
				
				//15. 取消选择键 SelectionKey
				it.remove();
				
			}
			
		}
		
	}
	
}
