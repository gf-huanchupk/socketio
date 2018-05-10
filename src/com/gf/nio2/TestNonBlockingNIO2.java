package com.gf.nio2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

import org.junit.Test;

/**
 * Java NIO 中的DatagramChannel是一个能收发UDP包的通道
 * 
 * 操作步骤：①打开DatagramChannel通道 ②接/发送数据
 * 
 * @author huanchu
 *
 */
public class TestNonBlockingNIO2 {

	@Test
	public void send() throws IOException{
		
		//获取通道
		DatagramChannel dc = DatagramChannel.open();
		
		//切换成非阻塞模式
		dc.configureBlocking(false);
		
		//分配指定大小的缓冲
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		Scanner scan = new Scanner(System.in);
		
		while(scan.hasNext()){
			String str = scan.next();
			buf.put((new Date().toString() + ":\n" + str).getBytes());
			buf.flip();
			dc.send(buf, new InetSocketAddress("127.0.0.1",9898));
			buf.clear();
		}
		
		dc.close();
		
	}
	
	@Test
	public void receive() throws IOException{
		
		//创建通道
		DatagramChannel dc = DatagramChannel.open();
		
		//切换成非阻塞
		dc.configureBlocking(false);
		
		//绑定连接
		dc.bind(new InetSocketAddress(9898));
		
		//获取选择器
		Selector selector = Selector.open();
		
		//将通道注册到选择器上，并且指定 监听 “读” 事件
		dc.register(selector,SelectionKey.OP_READ);
		
		while(selector.select() > 0){
			Iterator<SelectionKey> it = selector.selectedKeys().iterator();
			
			while(it.hasNext()){
				SelectionKey sk = it.next();
				
				//判断事件状态是否是 “可读”
				if(sk.isReadable()){
					ByteBuffer buf = ByteBuffer.allocate(1024);
					
					dc.receive(buf);
					
					buf.flip();
					System.out.println(new String(buf.array(),0,buf.limit()));
					buf.clear();
				}
			}
			
			it.remove();
			
		}
		
	}
	
	
}
