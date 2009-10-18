package com.bitcomm;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;

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
		UI.getDisplay().asyncExec(new Runnable(){
			public void run() {
				if (!UI.isDisposed())
				{
					UI.setCursor(UI.display.getSystemCursor(SWT.CURSOR_WAIT));
				}
			}
		});
		
		DoesRateFile localFile;
		int nDataNum =0;
		int PT = (meter.data.cPT==0)?10:meter.data.cPT;
		
		nDataNum =(int) (end.getTime().getTimeInMillis()/(60000*PT)
			- start.getTime().getTimeInMillis()/(60000*PT))+1;
		if (start.minute%PT>0)
			nDataNum-=1;
		
		DateTime dataStart = new DateTime();
		Calendar  cal = start.getTime();
		long time = cal.getTimeInMillis()/(60000*PT);
		if (start.minute%PT>0)
		{
			//System.out.println("not mul");
			time+=1;
		}
		cal.setTimeInMillis(time*60000*PT);
		dataStart.setTime(cal);
		//System.out.println(dataStart.CSVString());
		//System.out.println(String.valueOf(nDataNum));
		//System.out.println(String.valueOf(PT));
		UI.graph.Data = new double[total][nDataNum];
		UI.graph.strScaleX = new String[nDataNum];
		UI.graph.ResetData();
		int sum = nDataNum;
		do
		{
			if (UI.bCancel) break;
			localFile = new DoesRateFile(MachineNum,dataStart);
			if (!localFile.exists())
			{
				setPrompt("Local data is not avilable,will read from remote...");
				int n=ReadRemoteData(MachineNum,dataStart,1,PT);
				sum-=n;
				dataStart.addMinute(n*PT);
			}
			else
			{
				DoesRateData data;
				try {
					localFile.load();
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
				if (UI.bCancel) break;
				
				if ((dataStart.hour*60 + dataStart.minute)/PT >= localFile.dataArray.size())
				{
					data = ReadRemoteData(MachineNum, dataStart);
					if (data!=null) 
					{
						setPrompt("Remote "+data.date.toStringDate()+" "
								+data.date.toStringTime()+":"+String.valueOf(data.getDoesRatenGy()));
						DrawUI(data);
					}
					else
					{
						setPrompt(dataStart.toStringDate()
								+" "+dataStart.toStringTime()+ " unavailable");
						UI.graph.setData(0, Index, dataStart.toStringShortDate()
								+"\r\n"+dataStart.toStringTime());
					}
					dataStart.addMinute(PT);
					sum--;
				}
				
				for (int i = (dataStart.hour*60 + dataStart.minute)/PT; 
					i < localFile.dataArray.size() ;i++)
				{
					if (UI.bCancel) break;
					data = localFile.dataArray.get(i);
					if (data==null)
					{
						data = ReadRemoteData(MachineNum, dataStart);
						if (data!=null) setPrompt("Remote "+data.date.toStringDate()+" "
								+data.date.toStringTime()+":"+String.valueOf(data.getDoesRatenGy()));
					}
					else
					{
						setPrompt("Local "+data.date.toStringDate()+" "
								+data.date.toStringTime()+":"+String.valueOf(data.getDoesRatenGy()));
					}
					
					if (data!=null)
						DrawUI(data);
					else
					{
						setPrompt(dataStart.toStringDate()
								+" "+dataStart.toStringTime()+ " unavailable");
						UI.graph.setData(0, Index, dataStart.toStringShortDate()
								+"\r\n"+dataStart.toStringTime());
					}
					dataStart.addMinute(PT);
					sum--;
					if (dataStart.getTime().after(end.getTime()))
					{
						sum=0;
						break;
					}
					if (UI.bCancel) break;
					try {
						sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
		}while(sum>0);
		
		if (!UI.isDisposed())
			UI.getDisplay().asyncExec(new Runnable(){

			public void run() {
				if (!UI.isDisposed())
					UI.setCursor(null);
				UI.butStart.setEnabled(true);
				UI.butPrint.setEnabled(true);
				UI.butLoad.setEnabled(true);
				UI.lblPrompt.setText("");
			}
		});
	}
	
	void setPrompt(String str)
	{
		if (UI.isDisposed()) return;
		final String strT = str;
		UI.getDisplay().asyncExec(new Runnable(){

			public void run() {
					UI.lblPrompt.setText(strT);
			}
		});
	}
	
	void DrawUI(DoesRateData data)
	{
		final double val ;
		final String strT ;
		if (UI.isDisposed()) return;
		if (UI.bCancel) return;
		if (data==null) return;
		val = data.getDoesRatenGy();
		//System.out.println(data.date.CSVString());
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

	DoesRateData ReadRemoteData(int nMachine,DateTime date)
	{
		setPrompt("Read from remote...");
		CommunicationHistoryData his;
		his = new CommunicationHistoryData(port,MachineNum,CommunicationHistoryData.DoseRate);
		his.startTime = date;
		his.endTime = date;
		boolean done = false;
		DoesRateData data=null;
		int nError=0;
		meter.Pause(true);
		waitMeterPause();
		do
		{
			try 
			{
				
				his.Confirm();
				his.ConfirmAnswer();
				if( his.Confirmed.nCount>0)
				{
					his.DataRequest();
					data = his.DataAnswerDoseRate();
					if (data==null) his.DataAnswerDoseRate();
					if (data==null) his.DataAnswerDoseRate();
					if (data==null) his.DataAnswerDoseRate();

					if (data!=null)
					{
						data.Save();
					}
				};
				
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
				nError++;
				if (nError<3) continue;
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
		} while (false);
		meter.Pause(false);
		return data;
	}
	
	DoesRateData[] ReadRemoteData(int nMachine,DateTime dateStart,DateTime dateEnd)
	{
		CommunicationHistoryData his;
		his = new CommunicationHistoryData(port,MachineNum,CommunicationHistoryData.DoseRate);
		his.startTime = dateStart;
		his.endTime = dateEnd;
		boolean done = false;
		DoesRateData data[]=null;
		int nError=0;
		int index=0;
		meter.Pause(true);
		waitMeterPause();
		do
		{
			try 
			{
				his.Confirm();
				his.ConfirmAnswer();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				nError++;
				if (nError<3) continue;
				else
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
					break;
				}
			}

			nError=0;

			if (his.Confirmed.nCount>0)
			{
				data = new DoesRateData[his.Confirmed.nCount];
			}
			else
				return null;

			while( his.Confirmed.nCount>0)
			{
				try
				{
					his.DataRequest();
					data[index] = his.DataAnswerDoseRate();
					if (data[index]==null) his.DataAnswerDoseRate();
					if (data[index]==null) his.DataAnswerDoseRate();
					if (data[index]==null) his.DataAnswerDoseRate();

					if (data[index]!=null)
					{
						data[index].Save();
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
					nError++;
					if (nError < 3) continue;
				}
				index++;
				his.Confirmed.startNo++;
				his.Confirmed.nCount--;
				nError=0;
			};

			do
			{
				try {
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
					nError++;
					e.printStackTrace();
					if (nError<3) continue;
				}
			}while(false);
		} while (false);
		meter.Pause(false);
		return data;
	}
	
	int ReadRemoteData(int nMachine,DateTime dateStart,int nday,int PT)
	{
		setPrompt("Read from remote...");
		CommunicationHistoryData his;
		his = new CommunicationHistoryData(port,MachineNum,CommunicationHistoryData.DoseRate);
		his.startTime = dateStart;
		his.endTime = new DateTime();
		DateTime date = new DateTime();
		Calendar cal = dateStart.getTime();
		if (cal.getTimeInMillis()%(60000*PT)>0)
		{
			cal.setTimeInMillis(((cal.getTimeInMillis()/(60000*PT)+1))*(60000*PT));
		}
		date.setTime(cal);
		cal.add(Calendar.DATE, nday);
		his.endTime.setTime(cal);
		boolean done = false;
		
		DoesRateData data;
		
		int nError=0;
		int index=0;
		int nSum=0;
		
		meter.Pause(true);
		waitMeterPause();
		do
		{
			if (UI.bCancel) break;
			
			try 
			{
				his.Confirm();
				his.ConfirmAnswer();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				nError++;
				if (nError<3) continue;
				else
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

					break;
				}
			}

			nError=0;

			if (his.Confirmed.nCount==0)
				return 0;
			nSum = his.Confirmed.nCount;
			while( his.Confirmed.nCount>0)
			{
				if (UI.bCancel) break;
				try
				{
					his.DataRequest();
					data = his.DataAnswerDoseRate();
					if (data==null) his.DataAnswerDoseRate();
					if (data==null) his.DataAnswerDoseRate();
					if (data==null) his.DataAnswerDoseRate();

					if (data!=null)
					{
						setPrompt("Remote "+data.date.toStringDate()+" "
								+data.date.toStringTime()+":"+String.valueOf(data.getDoesRatenGy()));
						data.Save();
						DrawUI(data);
						if (UI.bCancel) break;
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
					nError++;
					if (nError < 3) continue;
					data = null;
				}
				
				if (data==null)
				{
					String strXScale = date.toStringShortDate()+"\r\n"
					+ date.toStringTime();
					UI.graph.setData(0, index, strXScale);
				}
				date.addMinute(PT);

				his.Confirmed.startNo++;
				his.Confirmed.nCount--;
				nError=0;
				if (UI.bCancel) break;
			};

			do
			{
				try {
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
					nError++;
					e.printStackTrace();
					if (nError<3) continue;
				}
			}while(false);
		} while (false);
		meter.Pause(false);
		return nSum;
	}
	void waitMeterPause()
	{
		String str="Commmunication Port busy, waiting";
		int i = 0;
		while(!meter.isPaused())
		{
			switch(i++%3)
			{
			case 0:
				setPrompt(str+ ".");
				break;
			case 1:
				setPrompt(str+ "..");
				break;
			case 2:
				setPrompt(str+ "...");
				break;
			}
			
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		};
	}
}
