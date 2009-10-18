package com.bitcomm;

import java.io.IOException;
import java.net.SocketException;
import java.util.Calendar;

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
	long nReserveTime;

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
					face.showConnecting();
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

		Calendar calDoesTime = Calendar.getInstance();
		Calendar calHCTime = Calendar.getInstance();
		Calendar calNow ;
		calDoesTime.set(1900, 0,1);
		calHCTime.set(1900,0,1);
		Command cmd = new Command(Command.CommandType.CurrentData);
		DataPacket cmdPacket = new DataPacket((byte) face.nMachineNum, cmd
				.ByteStream());
		if (face.data == null)
			face.data = new DoesRateData();

		while (!Stop && !face.isDisposed()) {
			//wait other task ok
			while (Pause) {
				Paused = true;
				try {
					System.out.println("sleep for pause");
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (face.isDisposed())
					return;
			};

			Paused = false;
			calNow = Calendar.getInstance();
			if (!port.IsConnected()) 
			{
				try 
				{
					SetConnecting();
					System.out.println("connecting...");
					port.Connect();
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
					SetCommunicationError();

					if (nError==0) PromptErr(e.getMessage());
					nError++;
					Paused = true;
					try {
						System.out.println("sleep for re-connect");
						sleep(5000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					
					SetCommunicationError();
					continue;
				}
			}
			
			nError=0;
			
			if (calNow.getTimeInMillis()-calHCTime.getTimeInMillis() > 20000)
			{
				calHCTime.setTimeInMillis(calNow.getTimeInMillis());

				try 
				{
					System.out.println("health check");
					HealthCheck();
				}
				catch (SocketException se)
				{
					try {
						System.out.println("health check fail ,close socket.");
						port.Close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					SetCommunicationError();
					
					try {
						sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					continue;
				}
				catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			if (calNow.getTimeInMillis()-calDoesTime.getTimeInMillis() >
					nInterval*1000 || 
					calNow.get(Calendar.MINUTE)%(nInterval /60)==0)
			{
				calDoesTime.setTimeInMillis(calNow.getTimeInMillis());
				nReserveTime = calNow.getTimeInMillis() + nInterval*1000; 
				try 
				{
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

				try 
				{
					DataPacket packet = port.RecvPacket();
					if (packet == null) packet = port.RecvPacket();
					if (packet == null) packet = port.RecvPacket();	

					if (packet!=null && packet.bValid
							&& !face.isDisposed()
							&& packet.getPacketType() == Command.CommandType.CurrentData) {
						nError = 0;

						face.data.parse(packet.ByteStream());

						face.data.Save();
						if (!face.isDisposed())
						face.getDisplay().asyncExec(new Runnable() {
							public void run() {
								
									face.setValue();
							}
						});
					}
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
					continue;
				}
				backupSpectrumData();
			}
			
			Paused = true;
			try {
				System.out.println("sleep for next roll");
				sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		;
	}
	
	void backupSpectrumData()
	{
		CommunicationHistoryData his;
		his = new CommunicationHistoryData(port,(byte)face.nMachineNum,CommunicationHistoryData.Spectrum);
		long nlastTime = AlokaPanel.GetSettingLong("LastSpectrumDate");
		Calendar cal=Calendar.getInstance();
		his.endTime.setTime(cal);
		cal.setTimeInMillis(nlastTime);
		his.startTime.setTime(cal);
		
		int err=0;
		do{
			try {
				his.Confirm();
				his.ConfirmAnswer();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (his.Confirmed==null)
			{
				err++;
				if (err>3)
				{
					break;
				}
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
		}while(false);
		
		if (his.Confirmed!=null)
		{
			SpectrumData dataS=null;
			int i = 0;
			while( his.Confirmed.nCount>0)
			{
				if (Stop || face.isDisposed()) break;
				i++;
				try {
					his.DataRequest();
					dataS = his.DataAnswerSpectrum();
					if (dataS==null) dataS = his.DataAnswerSpectrum();
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				
				if (dataS!=null)
				{
					try {
						dataS.Save();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				else
				{
						break;
				}
				err=0;
				his.Confirmed.startNo++;
				his.Confirmed.nCount--;
				if (i>2) break;
			};
			boolean done;
			try {
				his.Terminate();
				done = his.GetAck();
				if (!done)
				{
					his.Terminate();
					done = his.GetAck();
				}
			} catch (Exception e) {
				e.printStackTrace();
				done=false;
			}
		}	
	}
}
