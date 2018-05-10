package com.gf.nio2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.Test;

public class TestBlockingNIO2 {

	@Test
	public void client() throws IOException{
		//获取通道
		SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));
		FileChannel inChannel = FileChannel.open(Paths.get("1.png"), StandardOpenOption.READ);
		
		//分配指定大小的缓冲区
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		while(inChannel.read(buf) != -1){
			buf.flip();
			sChannel.write(buf);
			buf.clear();
		}
		
		//通知服务器端，数据传输完毕
		sChannel.shutdownOutput();
		
		//接收服务器端的反馈
		int len = 0;
		while((len = sChannel.read(buf)) != -1){
			buf.flip();
			System.out.println(new String(buf.array(),0,len));
			buf.clear();
		}
		
		inChannel.close();
		sChannel.close();
		
		
		
	}
	
	@Test
	public void server() throws IOException{
		
		//获取通道
		ServerSocketChannel ssChannel = ServerSocketChannel.open();
		FileChannel outChannel = FileChannel.open(Paths.get("2.png"), StandardOpenOption.WRITE,StandardOpenOption.CREATE);
		
		//绑定连接
		ssChannel.bind(new InetSocketAddress(9898));
		
		//获取客户端连接的通道
		SocketChannel sChannel = ssChannel.accept();
		
		//分配指定大小的缓冲区
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		//接受客户端数据，并保存到本地
		while(sChannel.read(buf) != -1){
			buf.flip();
			outChannel.write(buf);
			buf.clear();
		}
		
		//发送数据给客户端
		buf.put("服务器端接受数据成功".getBytes());
		buf.flip();
		sChannel.write(buf);
		
		sChannel.close();
		outChannel.close();
		ssChannel.close();
		
	}
	
}
