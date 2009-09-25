package com.bitcomm;

import org.eclipse.swt.SWT;

public class TrendDrawTask extends Thread {
	TrendView UI;
	CommunicationPort port;
	byte MachineNum;
	DateTime start;
	DateTime end;
	MeterView meter;
	
	TrendDrawTask(TrendView UI,CommunicationPort port,byte MachineNum,
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
		his = new CommunicationHistoryData(port,MachineNum,CommunicationHistoryData.DoseRate);
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
		
		meter.Pause(true);
		try {
			his.Confirm();
			
			his.ConfirmAnswer();
			
			HiLowData data=null;
			final int sum=his.Confirmed.nCount;
			UI.graph.Data = new double[1][sum];
			UI.graph.ResetData();
			int p=0;
			while( his.Confirmed.nCount>0)
			{
				p++;
				his.DataRequest();
				
					data = his.DataAnswerDoseRate();
					if (data==null) his.DataAnswerDoseRate();
					if (data==null) his.DataAnswerDoseRate();
					if (data==null) his.DataAnswerDoseRate();
								
				if (data!=null)
				{
					DrawUI(data);
				}
				
				his.Confirmed.startNo++;
				his.Confirmed.nCount--;
				
			}
			boolean done;
			his.Terminate();
			done = his.GetAck();
			if (!done)
			{
				his.Terminate();
				done = his.GetAck();
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
		meter.Pause(false);
	}
	
	void DrawUI(HiLowData data)
	{
		final double val = data.nNaIValue/10.0;
		if (UI.isDisposed()) return;
	
		UI.getDisplay().asyncExec(new Runnable(){

			public void run() {
				if (!UI.isDisposed())
				{
					
					UI.graph.setData(val,0);
					//UI.graph.setAutoTransform();
					UI.graph.redraw();
				}
			}
		});
	}


}
