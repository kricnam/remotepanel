package com.bitcomm;

import org.eclipse.swt.SWT;

public class BackupHilowData extends Thread {
	BackupView UI;
	CommunicationPort port;
	byte MachineNum;
	DateTime start;
	DateTime end;
	BackupHilowData(BackupView UI,CommunicationPort port,byte MachineNum,
			DateTime start,DateTime end)
	{
		this.UI=UI;
		this.port=port;
		this.MachineNum=MachineNum;
		this.start = start;
		this.end = end;
	}
	@Override
	public void run() 
	{
		
		
		CommunicationHistoryData his;
		his = new CommunicationHistoryData(port,MachineNum);
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
			HiLowData data;
			final int sum=his.Confirmed.nCount;
			int p=0;
			while( his.Confirmed.nCount>0)
			{
				p++;
				his.DataRequest();
				Print("Request...\n");
				data = his.DataAnswer();
				Print("Request answered\n");
				
				if (data!=null)
				{
					data.Save();
					Print(data.CSVString()+"\n");
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
