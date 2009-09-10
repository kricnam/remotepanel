package com.bitcomm;

import gnu.java.security.util.ByteBufferOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;


public class CommunicationTask extends Thread {

	/* （非 Javadoc）
	 * @see java.lang.Thread#run()
	 */
	String strServer;
	int nPort;
	MeterView face;
	boolean Stop;
	ByteBuffer buffer; 
	CommunicationTask(MeterView face)
	{
		this.face = face;
		Stop = false;
		buffer = ByteBuffer.allocateDirect(6000);
	}
	public void run() {
		// TODO 自动生成方法存根
		Socket socket;
		ByteArrayInputStream input;
		ByteArrayOutputStream output;
		try
		{
			socket = new Socket(strServer,nPort);
			input = (ByteArrayInputStream)socket.getInputStream();
			output = (ByteArrayOutputStream)socket.getOutputStream();
		}
		catch (Exception e) {
			e.printStackTrace();
			return;
		}
		Command cmd = new Command(Command.CommandType.CurrentData);
		DataPacket cmdPacket = new DataPacket((byte)1,cmd.ByteStream());
		while(!Stop)
		{
			try
			{
				output.write(cmdPacket.ByteStream());
				
				if (input.available() > 0)
				{
					byte ch = (byte)input.read();
					if (ch == DataPacket.SOH)
					{
						if (buffer.get(0)!=DataPacket.SOH)
							buffer.clear();
						buffer.put(ch);
					}
					else
					{
						DataPacket packet = new DataPacket(buffer.array());
						if (packet.bValid) 
						{
							face.data = new HiLowData(packet.ByteStream());
							face.redraw();
							buffer.clear();
						}
						
					}
				}
				sleep(5000);
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return;
			}
			
		};
		
	}
	

}
