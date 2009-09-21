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
				//port.Send(cmdPacket.ByteStream());
//				CommunicationHistoryData his=new CommunicationHistoryData(port,(byte)1);
//				his.startTime.year = 2009;
//				his.startTime.month = (byte)9;
//				his.startTime.day = (byte)9;
//				his.startTime.hour = 0;
//				his.startTime.minute = 0;
//				his.endTime.year = 2009;
//				his.endTime.month = (byte)9;
//				his.endTime.day = (byte)10;
//				his.endTime.hour = 0;
//				his.endTime.minute = 0;
//				his.Confirm();
//				his.ConfirmAnswer();
//				his.DataRequest();
//				HiLowData data = his.DataAnswer();
//				if (data!=null)
//					System.out.println(data.CSVString());
//				his.Confirmed.startNo+=1;
//				his.DataRequest();
//				data = his.DataAnswer();
//				if (data!=null)
//					System.out.println(data.CSVString());
			}
			catch(Exception e)
			{
				e.printStackTrace();
				if (port.socket.isClosed())
					return;
			}

			try{
				sleep(1000);
//				DataPacket packet=port.RecvPacket();
//				if (packet==null) continue;
//				if (packet.bValid && !face.isDisposed() && 
//						packet.getPacketType() == Command.CommandType.CurrentData) 
//				{
//					
//					face.data = new HiLowData(packet.ByteStream());
//					
//					face.getDisplay().asyncExec(new Runnable() {
//						public void run() {
//							if (!face.isDisposed())
//								face.setValue();
//						}
//					});
//
//				}

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
