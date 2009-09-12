package com.bitcomm;

import gnu.java.security.util.ByteBufferOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

public class CommunicationTask extends Thread {

	/*
	 * （非 Javadoc）
	 * 
	 * @see java.lang.Thread#run()
	 */
	String strServer;

	int nPort;

	MeterView face;

	boolean Stop;

	byte[] buffer;

	int pos;

	CommunicationTask(MeterView face) {
		this.face = face;
		Stop = false;
		buffer = new byte[6000];
		pos = 0;
	}

	public void run() {
		// TODO 自动生成方法存根
		Socket socket;
		// ByteArrayInputStream input = new
		// ByteArrayInputStream(buffer.array());
		InputStream input;
		OutputStream output;
		try {
			socket = new Socket(strServer, nPort);
			socket.setSoTimeout(5000);
			input = socket.getInputStream();
			output = socket.getOutputStream();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return;
		}

		Command cmd = new Command(Command.CommandType.CurrentData);
		DataPacket cmdPacket = new DataPacket((byte) 1, cmd.ByteStream());
		
		pos = 0;// buffer.clear();
		while (!Stop || !face.isDisposed()) {
				try
				{
					output.write(cmdPacket.ByteStream());
				}
				catch(Exception e)
				{
					e.printStackTrace();
					if (socket.isClosed())
						return;
				}
				
				pos=0;
				buffer[0]=0;

				int ch=-1;
				
				do {
					try
					{
						ch = input.read();
					}
					catch (SocketTimeoutException se) 
					{
						System.out.println("time out");
						if (buffer[0] != DataPacket.SOH)
							pos = 0;
						break;
					}
					catch(IOException e)
					{
						e.printStackTrace();
						if (socket.isInputShutdown())
							return;
					}
					
					if (ch < 0 ) break;
					
					if (pos >= buffer.length) pos = 0;
					
					if (pos == 0 && ch != DataPacket.SOH) 	continue;
					
					
					if (ch == DataPacket.SOH && buffer[0] != DataPacket.SOH) 
					{
						pos = 0;// buffer.clear();
						buffer[pos++] = (byte) ch;// buffer.put((byte) ch);
					} 
					else   
					{
						buffer[pos++] = (byte) ch;// buffer.put((byte) ch);

						if (pos > 6) 
						{
							DataPacket packet = new DataPacket(buffer, pos);
							if (packet.bValid && !face.isDisposed()) {
								face.data = new HiLowData(packet.ByteStream());

								face.getDisplay().asyncExec(new Runnable() {
									public void run() {
										if (face.isDisposed())
											return;
										face.setValue();
									}
								});

								pos = 0;// buffer.clear();
							} 
							else if (packet.start  > 3)
							{
								for (int i=0;i< pos;i++)
								{
									buffer[i]=buffer[packet.start-3 +i];
								}
								pos = pos - packet.start + 3;
							}
							packet = null;
						}

					}
				}
				while(true);
				
				if (pos == 0)
					try {
						sleep(10000);
					} catch (InterruptedException e) {
						// TODO 自动生成 catch 块
						e.printStackTrace();
					}

			};
			
			try {
				socket.close();
			} catch (IOException e) {
				// TODO 自动生成 catch 块
				e.printStackTrace();
			}
		}


}
