package com.bitcomm;

import java.io.IOException;
import java.util.Calendar;
import java.util.Formatter;

import org.eclipse.swt.SWT;

public class BackupDataTask extends Thread {
	BackupView UI;
	CommunicationPort port;
	byte MachineNum;
	DateTime start;
	DateTime end;
	int DataType;
	MeterView meter;
	BackupDataTask(int DataType,BackupView UI,CommunicationPort port,byte MachineNum,
			DateTime start,DateTime end)
	{
		this.UI=UI;
		this.port=port;
		this.MachineNum=MachineNum;
		this.start = start;
		this.end = end;
		this.DataType = DataType;
	}
	@Override
	public void run() 
	{
		CommunicationHistoryData his;
		his = new CommunicationHistoryData(port,MachineNum,DataType);
		his.startTime = start;
		his.endTime = end;
		UI.getDisplay().asyncExec(new Runnable(){

			public void run() {
				if (!UI.isDisposed())
				{
					UI.setCursor(UI.display.getSystemCursor(SWT.CURSOR_WAIT));
					UI.butStart.setEnabled(false);
				}
				
			}
			
		});
		
		int err=0;
		
		do{
			try {
				Print("Confirm data...\n");
				his.Confirm();
				Print("Confirm answered.\n");
				his.ConfirmAnswer();
			} catch (Exception e) {
				Print(e.getMessage());
				e.printStackTrace();
			}
			
			if (his.Confirmed==null)
			{
				err++;
				Print("Error read comfirm data.");
				if (err>3)
				{
					Print("Please try to download late.\n");
					break;
				}
				Print("try again...\n");
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
			final int sum=his.Confirmed.nCount;
			Print("Total "+String.valueOf(sum)+" records will be read.\n");
			int p=0;
			DoesRateData data=null;
			SpectrumData dataS=null;
			Calendar calNow = Calendar.getInstance();
			Calendar calLast = Calendar.getInstance();
			
			int total = his.Confirmed.nCount;
			String strLeft ="";
			while( his.Confirmed.nCount>0)
			{
				if (UI.bCancel) break;
				if (UI.isDisposed()) break;
				if (calNow.getTimeInMillis()>meter.getReserveTime())
				{
					meter.Pause(false);
					while(meter.isPaused())
					{
						try {
							sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					};
					while(!meter.isPaused())
					{
						try {
							sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					};
					meter.Pause(true);
				}
					
				if (calNow.after(calLast))
				{
					long mili = calNow.getTimeInMillis() - calLast.getTimeInMillis();
					mili = (mili / (total - his.Confirmed.nCount)) * his.Confirmed.nCount;
					mili = mili /1000;
					StringBuilder sb = new StringBuilder();
					Formatter formatter = new Formatter(sb);
					formatter.format("need time %d:%02d\n", mili/60,mili%60);
					strLeft = sb.toString();
					
 				}
				setMsg(String.valueOf(his.Confirmed.nCount)+" records left." + strLeft);
				try {
					Print("Request...\n");
					his.DataRequest();
					if (DataType == CommunicationHistoryData.DoseRate)
					{
						data = his.DataAnswerDoseRate();
						if (data==null) his.DataAnswerDoseRate();
						if (data==null) his.DataAnswerDoseRate();
					}
					else
					{
						dataS = his.DataAnswerSpectrum();
						if (dataS==null) dataS = his.DataAnswerSpectrum();
						if (dataS==null) dataS = his.DataAnswerSpectrum();
					}

				} 
				catch (Exception e) 
				{
					Print(e.getMessage());
					e.printStackTrace();
				}
				
				Print("Request answered\n");
				if (data!=null)
				{
					try {
						data.Save();
						Print(data.CSVString()+"\n");
					} catch (IOException e) {
						Print(e.getMessage()+"\n");
						e.printStackTrace();
					} catch (Exception e) {
						Print(e.getMessage()+"\n");
						e.printStackTrace();
					}
				}
				
				if (dataS!=null)
				{
					Print("Save Data No."+String.valueOf(dataS.DataNum)+
							" for "+ dataS.dateEnd.toStringDate()+ " " +
							dataS.dateEnd.toStringTime()+"\n");
					try {
						dataS.Save();
					} catch (IOException e) {
						Print(e.getMessage()+"\n");
						e.printStackTrace();
					} catch (Exception e) {
						Print(e.getMessage()+"\n");
						e.printStackTrace();
					}
				}
				
				if (data==null && dataS==null)
				{
					err++;
					if (err<3) 
					{
						Print("Data error,try again.\n");
						continue;
					}
					else
					{
						Print("Communication Error, Please try to download backup data late.\n");
						break;
					}
				}
				
				err=0;
				p++;
				final int q = p;
				if (!UI.isDisposed())
				{
					UI.display.asyncExec(new Runnable(){
						public void run() {
							UI.prograss.setSelection(q*100/sum);
						}
					});
				}
				
				his.Confirmed.startNo++;
				his.Confirmed.nCount--;
				calNow = Calendar.getInstance();
			};
			setMsg("");
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
			Print("terminate notify ack="+String.valueOf(done)+"\n");
			Print("Done\n");
		}	

		if (!UI.isDisposed())
			UI.getDisplay().asyncExec(new Runnable(){

			public void run() {
				if (!UI.isDisposed())
				{
					UI.setCursor(null);
					UI.butStart.setEnabled(true);
					UI.prograss.setSelection(0);
				}
			}
			
		});
		
		meter.Pause(false);
	}
	
	void Print(String str)
	{
		final String strTmp;
		strTmp = str;
		if (UI.isDisposed()) return;
		if (strTmp==null) return;
		UI.getDisplay().asyncExec(new Runnable(){

			public void run() {
				if (!UI.isDisposed())
				{
					
						UI.console.append(strTmp);
				}
			}
		});
	}
	
	void setMsg(String str)
	{
		final String strTmp;
		strTmp = str;
		if (UI.isDisposed()) return;
		if (strTmp==null) return;
		UI.getDisplay().asyncExec(new Runnable(){
			public void run() {
				if (!UI.isDisposed())
				{
						UI.lblMsg.setText(strTmp);
				}
			}
		});
	}

}
