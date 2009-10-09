package com.bitcomm;

import java.net.SocketException;

import com.bitcomm.Command.CommandType;

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

	CommunicationTask(MeterView face, CommunicationPort port) {
		this.face = face;
		this.port = port;
		Stop = false;
		Pause = false;
		Paused = false;
		nError = 0;
		nInterval = 600;
	}

	void HealthCheck() throws Exception {
		HealthCheckRequest hc = new HealthCheckRequest();
		hc.nStatus = 0;
		//hc.nStatus = face.data.nStatus;
		DataPacket pk = new DataPacket((byte) face.nMachineNum, hc.ByteStream());
		port.Send(pk.ByteStream());

		DataPacket packet = port.RecvPacket();

		if (packet != null && packet.getPacketType() == CommandType.HealthCheck) {
			HealthCheckAnswer answer = new HealthCheckAnswer(packet
					.ByteStream());
			face.data.nStatus = answer.nStatus;
			//System.out.println(String.format("%X", face.data.nStatus));
			//System.out.println(answer.date.CSVString());
			face.getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (!face.isDisposed())
						face.setValue();
				}
			});
		}
		else
			SetCommunicationError();
	}

	void SetCommunicationError()
	{
		face.getDisplay().asyncExec(new Runnable() {
			public void run() {
	
				if (!face.isDisposed())
					face.showOffLine();
			}
		});
	}
	
	void SetConnecting()
	{
		face.getDisplay().asyncExec(new Runnable() {
			public void run() {
	
				if (!face.isDisposed())
					face.showConnect();
			}
		});
	}
	
	void PromptErr(String strMsg1)
	{
		final String strMsg = strMsg1;
		face.getDisplay().asyncExec(new Runnable() {
			public void run() {
	
				if (!face.isDisposed())
					AlokaPanel.MessageBox("Network Error", strMsg);
			}
		});
	}
	public void run() {

		int nSleepCount = 0;

		Command cmd = new Command(Command.CommandType.CurrentData);
		DataPacket cmdPacket = new DataPacket((byte) face.nMachineNum, cmd
				.ByteStream());
		if (face.data == null)
			face.data = new DoesRateData();

		while (!Stop && !face.isDisposed()) {

			try {
				while (Pause) {
					Paused = true;
					sleep(1000);
				};

				Paused = false;

				if (!port.IsConnected()) 
				{
					try {
						port.Connect();
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
						SetCommunicationError();
						
						if (nError==0) PromptErr(e.getMessage());
						nError++;
						sleep(5000);
						SetConnecting();
						continue;
					}
				}
				nError=0;
				if (nSleepCount%20==0)
				{

					try {
						HealthCheck();
					}
					catch (SocketException se)
					{
						try {
							System.out.println("health check fail ,close.");
							port.Close();
						} catch (Exception e) {
							e.printStackTrace();
						}
						SetCommunicationError();
						sleep(5000);
						continue;
					}
					catch (Exception e1) {

						e1.printStackTrace();
					}
				}

				if (nSleepCount%nInterval==0)
				{

					try {
						port.Send(cmdPacket.ByteStream());
					}
					catch(SocketException se)
					{
						try {
							port.Close();
						} catch (Exception e) {
							e.printStackTrace();
						}
						continue;
					}
					catch (Exception e) 
					{
						e.printStackTrace();
						if (port.socket.isClosed())
							continue;
					}

					try {
						DataPacket packet = port.RecvPacket();
						if (packet == null) {
							nError++;
							continue;
						}

						if (packet.bValid
								&& !face.isDisposed()
								&& packet.getPacketType() == Command.CommandType.CurrentData) {
							nError = 0;

							face.data.parse(packet.ByteStream());

							face.data.Save();
							face.getDisplay().asyncExec(new Runnable() {
								public void run() {
									if (!face.isDisposed())
										face.setValue();
								}
							});

						}

					} 
					catch (Exception e) {

						nError++;
						e.printStackTrace();
						return;
					}
				}
				Paused = true;
				{
					sleep(1000);

					if (nSleepCount++ > nInterval)
						nSleepCount = 0;
					if (face.isDisposed())
						break;
				}
				Paused = false;
			} catch (InterruptedException e) {

				e.printStackTrace();
				continue;
			}

		}
		;
	}
}
