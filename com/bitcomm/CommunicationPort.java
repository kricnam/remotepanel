package com.bitcomm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class CommunicationPort {
	String strServer;
	int nPort;
	boolean Stop;
	byte[] buffer;
	protected int pos;
	protected Socket socket;
	InputStream input;
	OutputStream output;

	CommunicationPort() {
		pos = 0;
	}

	CommunicationPort(String strS,int nPort) {
		pos= 0;
		strServer = strS;
		this.nPort = nPort; 
	}

	public void Connect(String strServer,int nPort) throws Exception
	{
		this.strServer = strServer;
		this.nPort = nPort;
		Connect();
	}
	boolean IsConnected()
	{
		if (socket==null) return false;
		return !socket.isClosed();
	}

	public void Connect() throws Exception
	{
		if (socket!=null && !socket.isClosed())
			return;
		socket = null;
		socket = new Socket(strServer, nPort);
		socket.setSoTimeout(5000);
		input = socket.getInputStream();
		output = socket.getOutputStream();
	}

	public void Close() throws Exception
	{
			if (socket!=null)
			{
				socket.close();
				//System.out.println(socket.isClosed());
				
			}
	}

	public void Send(byte[]buff) throws Exception
	{
		output.write(buff);
	}

	Command GetAck() throws IOException
	{
		pos=0;
		if (buffer == null) buffer = new byte[6000];
		buffer[0]=0;

		int ch=-1;
		
		do{
			try
			{
				ch = input.read();
			}
			catch (SocketTimeoutException se) 
			{
				if (buffer[0] != DataPacket.ACK ||buffer[0] != DataPacket.NAK)
					pos = 0;
				//se.printStackTrace();
				ch = -1;
				//throw se;
			}
			if (ch < 0 ) break;
			if (pos==0 && (ch == DataPacket.ACK || ch == DataPacket.NAK))
			{
				buffer[pos++] = (byte) ch;
				continue;
			}
			
			if (buffer[0]==DataPacket.ACK || buffer[0]==DataPacket.NAK)
			{
				buffer[pos++] = (byte) ch;
			}
			
			if (pos==4)
			{
				Command cmd = new Command(buffer,2);
				return cmd;
			}
			
		}while(true);
		return null;
		
	}
	
	public DataPacket RecvPacket() throws Exception
	{
		pos=0;
		if (buffer == null) buffer = new byte[6000];
		buffer[0]=0;

		int ch=-1;
		
		do{
			try
			{
				ch = input.read();
			}
			catch (SocketTimeoutException se) 
			{
				if (buffer[0] != DataPacket.SOH)
					pos = 0;
				//se.printStackTrace();
				ch = -1;
				//throw se;
			}
			//System.out.println(String.valueOf(pos)+":"+String.valueOf(ch));
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

				if (pos > 4096 || ch ==DataPacket.EOT) 
				{
					DataPacket packet = new DataPacket(buffer, pos);
					if (packet.bValid) {
						pos = 0;// buffer.clear();
						return packet;
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
		return null;
	}

}
