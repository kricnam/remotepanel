package com.bitcomm;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

public class TrendDrawTask extends Thread {
	TrendView UI;
	CommunicationPort port;
	byte MachineNum;
	DateTime start;
	DateTime end;
	MeterView meter;
	int Index;
	int total;
	TrendDrawTask(int index,int total,TrendView UI,CommunicationPort port,byte MachineNum,
			DateTime start,DateTime end)
	{
		this.UI=UI;
		this.port=port;
		this.MachineNum=MachineNum;
		this.start = start;
		this.end = end;
		Index= index;
		this.total = total;
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
			
			DoesRateData data=null;
			final int sum=his.Confirmed.nCount;
			if (Index==0)
			{
				UI.graph.Data = new double[total][sum];
				UI.graph.strScaleX = new String[sum];
				UI.graph.ResetData();
			}
			int p=0;
			while( his.Confirmed.nCount>0)
			{
				if (UI.isDisposed()) break;
				p++;
				his.DataRequest();
				
					data = his.DataAnswerDoseRate();
					if (data==null) his.DataAnswerDoseRate();
					if (data==null) his.DataAnswerDoseRate();
					if (data==null) his.DataAnswerDoseRate();
								
				if (data!=null)
				{
					//data.Save();
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
			
			final StringWriter sw = new StringWriter();
		    PrintWriter pw = new PrintWriter(sw);
			sw.append(e.toString()+"\r\n");
			e.printStackTrace(pw);
			if (!UI.isDisposed())
				UI.getDisplay().asyncExec(new Runnable(){

				public void run() {
					if (!UI.isDisposed())
					{
						MessageBox box = new MessageBox(UI.getShell(), SWT.ICON_ERROR);
						box.setMessage(sw.toString());
						box.setText("Communication Error");
						
						box.open();
					}
				}
				
			});
		}
		
		if (!UI.isDisposed())
			UI.getDisplay().asyncExec(new Runnable(){

			public void run() {
				if (!UI.isDisposed())
					UI.setCursor(null);
				UI.butStart.setEnabled(true);
				UI.butPrint.setEnabled(true);
			}
			
		});
		meter.Pause(false);
	}
	
	void DrawUI(DoesRateData data)
	{
		final double val ;
		final String strT ;
		if (UI.isDisposed()) return;
		if (data==null) return;
		val = data.getDoesRatenGy();
		
		strT = data.date.toStringShortDate()+"\r\n"+data.date.toStringTime();
		UI.getDisplay().asyncExec(new Runnable(){

			public void run() {
				if (!UI.isDisposed())
				{
					UI.graph.setData(val,Index,strT);
					
					if (Index==0) UI.graph.redraw();
					else UI.update();
				}
			}
		});
	}


}
