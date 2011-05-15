package com.bitcomm;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPServer implements Runnable {

	private boolean running;

	private Selector selector;
	String writeMsg;

	SelectionKey ssckey;
	ExecutorService exec;

	public TCPServer() {

		running = true;
		exec = Executors.newCachedThreadPool();
	}
	
	public void stop()
	{
		running = false;
		selector.wakeup();
	}

	public void init() {
		try {
			selector = Selector.open();
			ServerSocketChannel ssc = ServerSocketChannel.open();
			ssc.configureBlocking(false);
			ssc.socket().bind(new InetSocketAddress(9998));
			ssckey = ssc.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("server is starting..." + new Date());

		} catch (IOException ex) {
			Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null,
					ex);
			ex.printStackTrace();
		}
	}

	//
	// public static void main(String[] args){
	// TCPServer server=new TCPServer();
	// new Thread(server).start();
	//
	// }
	
	public void execute() {

		try {
			while (running) 
			{
				int num = selector.select();
				if (num > 0)
				{
					Iterator it = selector.selectedKeys().iterator();
					while (it.hasNext()) {
						SelectionKey key = (SelectionKey) it.next();
						it.remove();
						if (!key.isValid())
						{
							continue;
						}
						if (key.isAcceptable()) {
							System.out.println("isAcceptable");
							getConn(key);
						} else if (key.isReadable()) {
							//System.out.println("isReadable");
							try
							{
								readMsg(key);
							}
							catch(IOException ex)
							{
								ex.printStackTrace();
								key.cancel();								
							}
						} else if (key.isValid() && key.isWritable()) {
							if (writeMsg != null) {
								//System.out.println("isWritable");
								try
								{
									writeMsg(key);
								}
								catch(IOException ex)
								{
									ex.printStackTrace();
									key.cancel();								
								}
							}
						}
						else
							break;
					}

				}
				Thread.yield();
				//System.out.println("+++++");
			}
			System.out.println("service shut down");
		} 
		catch (IOException ex) {
			Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null,
					ex);
			ex.printStackTrace();
		}
	}

	private void getConn(SelectionKey key) throws IOException {
		ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
		SocketChannel sc = ssc.accept();
		sc.configureBlocking(false);
		sc.register(selector, SelectionKey.OP_READ);
		sc.socket().setSoTimeout(30);
		sc.socket().setKeepAlive(true);
		// sc.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
		System.out.println("build connection :"
				+ sc.socket().getRemoteSocketAddress());

	}

	private void readMsg(SelectionKey key) throws IOException {
		StringBuffer sb = new StringBuffer();

		SocketChannel sc = (SocketChannel) key.channel();
		if (!sc.socket().isConnected())
		{
			key.cancel();
			sc.close();
			sc.socket().close();
			return;
		}
		
		//System.out.print(sc.socket().getRemoteSocketAddress() + " ");
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		buffer.clear();
		int len = 0;

		while ((len = sc.read(buffer)) > 0) {
			buffer.flip();
			sb.append(new String(buffer.array(), 0, len));
		}
		if (sb.length() > 0)
			System.out.println("get from client:" + sb.toString());
		else
			return;
		
		if (sb.toString().trim().toLowerCase().equals("quit")) {
			sc.write(ByteBuffer.wrap("BYE".getBytes()));
			System.out.println("client is closed "
					+ sc.socket().getRemoteSocketAddress());
			key.cancel();
			sc.close();
			sc.socket().close();

		} 
		else 
		{
			String toMsg = sc.socket().getRemoteSocketAddress() + " said:"
					+ sb.toString();
			System.out.println(toMsg);
			toMsg = sb.toString();
			Iterator it = key.selector().keys().iterator();
			while (it.hasNext()) 
			{
				SelectionKey skey = (SelectionKey) it.next();
				if (!skey.isValid()) continue;
				if (skey != key && skey != ssckey) {
					MyWriter myWriter = new MyWriter(skey, toMsg);
					exec.execute(myWriter);
				}

			}

			/*
			 * 
			 * key.attach(toMsg);
			 * key.interestOps(key.interestOps()|SelectionKey.OP_WRITE);
			 */
			/*
			 * Iterator it=key.selector().keys().iterator();
			 * while(it.hasNext()){ SelectionKey skey=it.next();
			 * if(skey!=key&&skey!=ssckey){ if(skey.attachment()!=null){ String
			 * str=(String) skey.attachment(); skey.attach(str+toMsg); }else{
			 * skey.attach(toMsg); }
			 * skey.interestOps(skey.interestOps()|SelectionKey.OP_WRITE); } }
			 * selector.wakeup();//可有可无
			 */
		}

	}

	class MyWriter implements Runnable {
		SelectionKey key;
		String msg;

		public MyWriter(SelectionKey key, String msg) {
			this.key = key;
			this.msg = msg;
		}

		public void run() {
			try {
				
				SocketChannel client = (SocketChannel) key.channel();
				System.out.println("send to "+ client.socket().getRemoteSocketAddress().toString());
				if (key.isValid())
					client.write(ByteBuffer.wrap(msg.getBytes()));
				else
				{
					client.close();
					client.socket().close();
				}
				Thread.yield();
			} catch (IOException ex) {
				ex.printStackTrace();
				Logger.getLogger(MyWriter.class.getName()).log(Level.SEVERE,
						null, ex);
				key.cancel();
				
			}
		}
	}

	public void run() {
		init();
		execute();
	}

	private void writeMsg(SelectionKey key) throws IOException {

		System.out.println("++++enter write+++");
		SocketChannel sc = (SocketChannel) key.channel();
		String str = (String) key.attachment();

		sc.write(ByteBuffer.wrap(str.getBytes()));
		key.interestOps(SelectionKey.OP_READ);
	}
}