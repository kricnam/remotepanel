package com.bitcomm;

import org.eclipse.swt.SWT;

public class BackupDataTask extends Thread {
	BackupView UI;
	CommunicationPort port;
	byte MachineNum;
	DateTime start;
	DateTime end;
	int DataType;
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
					
				}
				
			}
			
		});
		
		
		try {
			his.Confirm();
			Print("Confirm data...\n");
			his.ConfirmAnswer();
			Print("Confirm answered.\n");
			HiLowData data=null;
			SpectrumData dataS=null;
			final int sum=his.Confirmed.nCount;
			int p=0;
			while( his.Confirmed.nCount>0)
			{
				p++;
				his.DataRequest();
				Print("Request...\n");
				if (DataType == CommunicationHistoryData.DoseRate)
				{
					data = his.DataAnswerDoseRate();
				}
				else
				{
					dataS = his.DataAnswerSpectrum();
					if (dataS==null) dataS = his.DataAnswerSpectrum();
					if (dataS==null) dataS = his.DataAnswerSpectrum();
					if (dataS==null) dataS = his.DataAnswerSpectrum();
				}
				
				Print("Request answered\n");
				
				if (data!=null)
				{
					data.Save();
					Print(data.CSVString()+"\n");
				}
				if (dataS!=null)
				{
					dataS.Save();
				}
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
				
			}
			

		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (!UI.isDisposed())
			UI.getDisplay().asyncExec(new Runnable(){

			public void run() {
				if (!UI.isDisposed())
					UI.setCursor(null);
				
			}
			
		});
	}
	
	void Print(String str)
	{
		final String strTmp;
		strTmp = str;
		if (UI.isDisposed()) return;
		UI.getDisplay().asyncExec(new Runnable(){

			public void run() {
				if (!UI.isDisposed())
				{
					UI.console.append(strTmp);
				}
			}
		});
	}

}
