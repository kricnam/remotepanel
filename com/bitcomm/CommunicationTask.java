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

	CommunicationTask(MeterView face,CommunicationPort port) {
		this.face = face;
		this.port = port;
		Stop = false;
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
				port.Send(cmdPacket.ByteStream());
			}
			catch(Exception e)
			{
				e.printStackTrace();
				if (port.socket.isClosed())
					return;
			}

			try{
				sleep(1000);
				DataPacket packet=port.RecvPacket();
				if (packet==null) continue;
				if (packet.bValid && !face.isDisposed() && 
						packet.getPacketType() == Command.CommandType.CurrentData) 
				{
					
					face.data = new HiLowData(packet.ByteStream());
					
					face.getDisplay().asyncExec(new Runnable() {
						public void run() {
							if (!face.isDisposed())
								face.setValue();
						}
					});

				}

				sleep(600000);
			} 
			catch (InterruptedException e) {
				// TODO 自动生成 catch 块
				e.printStackTrace();
				return;
			}
			catch (Exception e) {
				// TODO 自动生成 catch 块
				e.printStackTrace();
				return;
			}

		};
	}

}
