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
	int nHC_err;
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
		nHC_err = 0;
	}

	void HealthCheck() throws Exception {
		HealthCheckRequest hc = new HealthCheckRequest();
		hc.nStatus = 0;
		//hc.nStatus = face.data.nStatus;
		if (nHC_err > 4)
		{
			SetConnecting();
		    nHC_err=0;
		}
		
		DataPacket pk = new DataPacket((byte) face.nMachineNum, hc.ByteStream());
		port.Send(pk.ByteStream());

		DataPacket packet = port.RecvPacket();

		if (packet != null && packet.getPacketType() == CommandType.HealthCheck) {
			HealthCheckAnswer answer = new HealthCheckAnswer(packet
					.ByteStream());
			face.data.nStatus = answer.nStatus;
			face.statusDate.setTime(answer.date.getTime());
			//System.out.println(String.format("%X", face.data.nStatus));
			//System.out.println(answer.date.CSVString());
			face.getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (!face.isDisposed())
						face.setValue();
				}
			});
			nHC_err=0;
		}
		else
		{
			nHC_err++;
			System.out.println("Health Check return error");
			if (nHC_err>3) SetCommunicationError();
		}
	}

	void SetCommunicationError()
	{
		if (face.isDisposed()) return;
		face.getDisplay().asyncExec(new Runnable() {
			public void run() {
	
				if (!face.isDisposed())
					face.showOffLine();
			}
		});
	}
	
	void SetConnecting()
	{
		if (face.isDisposed()) return;
		face.getDisplay().asyncExec(new Runnable() {
			public void run() {
	
				if (!face.isDisposed())
					face.showConnecting();
			}
		});
	}
	
	void PromptErr(String strMsg1)
	{
		if (face.isDisposed()) return;
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
		
		byte[] a = cmdPacket.ByteStream();
		for(int i=0;i<a.length;i++)
		{
			System.out.printf("%x ", a[i]);
		}
		System.out.println();
		
		if (face.data == null)
			face.data = new DoesRateData();

		while (!Stop && !face.isDisposed()) {
			//wait other task is ready
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
					if (nError==0) PromptErr(e.getMessage());
					nError++;
					Paused = true;
					try {
						System.out.println("sleep for re-connect");
						sleep(2000);
						SetCommunicationError();
						sleep(3000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					
					
					continue;
				}
			}
			
			nError=0;
			if (face.data.cPT > 0) nInterval = Math.min(nInterval, face.data.cPT*60);
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
					nInterval*1000)
			{
				if (calNow.get(Calendar.MINUTE)%(nInterval/60)>0)
				{
					calNow.set(Calendar.MINUTE, calNow.get(Calendar.MINUTE)/(nInterval/60)*(nInterval/60));
				}
				
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
				catch (IOException ee)
				{
					ee.printStackTrace();
					try {
						port.Close();
					} catch (Exception e) {
						// TODO Auto-generated catch block
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
						
						calNow.setTimeInMillis(face.data.date.getTime().getTimeInMillis());
						face.statusDate.setTime(calNow);
						nReserveTime = calNow.getTimeInMillis() + nInterval*1000;
						calDoesTime.setTimeInMillis(face.data.date.getTime().getTimeInMillis());
						
						if (!face.isDisposed())
						{	
							
							face.getDisplay().asyncExec(new Runnable() {
								public void run() {
								
									face.setValue();
								}
							});
							backupSpectrumData();
						}
					}
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				
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
		if (AlokaPanel.backup!=null) return;
		System.out.println("backup spectrum data");
		CommunicationHistoryData his;
		his = new CommunicationHistoryData(port,(byte)face.nMachineNum,CommunicationHistoryData.Spectrum);
		long nlastTime = AlokaPanel.GetSettingLong("LastSpectrumDate");
		Calendar cal=Calendar.getInstance();
		his.endTime.setTime(cal);
		cal.setTimeInMillis(nlastTime);
		his.startTime.setTime(cal);
		
		do{
			try {
				his.Confirm();
				his.ConfirmAnswer();
			} catch (Exception e) {
				e.printStackTrace();
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
						System.out.println("spectrum data saved");
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
				his.Confirmed.startNo++;
				his.Confirmed.nCount--;
				if (i>1) break;
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
