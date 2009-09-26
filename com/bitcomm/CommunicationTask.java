package com.bitcomm;


public class CommunicationTask extends Thread {

	/*
	 * （非 Javadoc）
	 * 
	 * @see java.lang.Thread#run()
	 */
	CommunicationPort port; 
	MeterView face;
	boolean Stop;
	boolean Pause;
	boolean Paused;
	int nError;
	int nInterval;
	CommunicationTask(MeterView face,CommunicationPort port) {
		this.face = face;
		this.port = port;
		Stop = false;
		Pause = false;
		Paused = false;
		nError=0;
		nInterval = 600;
	}

	public void run() {

		if (!port.IsConnected())
		{
			try {
				port.Connect();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return;
			}
		}

		Command cmd = new Command(Command.CommandType.CurrentData);
		DataPacket cmdPacket = new DataPacket((byte) 1, cmd.ByteStream());

		while (!Stop && !face.isDisposed()) {
			
			try 
			{
				while(Pause){
					Paused = true;
					sleep(1000);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			Paused = false;
				
			try
			{
				port.Send(cmdPacket.ByteStream());
			}
			catch(Exception e)
			{
				e.printStackTrace();
				if (port.socket.isClosed())
					return;
			}

			try{
				DataPacket packet=port.RecvPacket();
				if (packet==null) 
					{
						nError++;
						continue;
					}
				
				
				if (packet.bValid && !face.isDisposed() && 
						packet.getPacketType() == Command.CommandType.CurrentData) 
				{
					nError=0;
					face.data = new HiLowData(packet.ByteStream());
					if (face.data!=null)
						face.data.Save();
					face.getDisplay().asyncExec(new Runnable() {
						public void run() {
							if (!face.isDisposed())
								face.setValue();
						}
					});

				}
				Paused = true;	
				for (int i =0 ;i<nInterval;i++)
				{
					sleep(1000);
					if (face.isDisposed())
						break;
				}
				Paused = false;
			} 
			catch (InterruptedException e) {
				// TODO 自动生成 catch 块
				e.printStackTrace();
				return;
			}
			catch (Exception e) {
				// TODO 自动生成 catch 块
				nError++;
				e.printStackTrace();
				return;
			}

		};
	}

}
