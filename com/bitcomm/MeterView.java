package com.bitcomm;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class MeterView extends Composite  {
	Group group;
	AnalogMeter meter;
	int nMachineNum;
	Label label;
	HiLowData data;
	CommunicationTask dataTask;
	CommunicationPort ComPort;
	final static RGB rgbGreen = new RGB(150,250,150);
	final static RGB rgbRed = new RGB(255,90,100);
	final static RGB rgbOff = new RGB(190,190,200);
	final static RGB rgbYellow = new RGB(250,255,150);
	public MeterView(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		// setSize(new Point(300, 200));
		setLayout(new FillLayout());
	
		group= new Group(this,SWT.NONE);
		group.setLayout(new GridLayout());
		
		GridData layoutData = new GridData(SWT.FILL,SWT.FILL,true,false);
		label = new Label(group,SWT.NONE);
		label.setAlignment(SWT.CENTER);
		label.setText(" ");
		label.setLayoutData(layoutData);
		meter = new AnalogMeter(group,SWT.BORDER);
		meter.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		ComPort = new CommunicationPort();
		dataTask = new CommunicationTask(this,ComPort);
	}
	
	public void setValue()
	{
		
		if ((data.nStatus & 0x10000001) > 0 )
		{
			
			if ((data.nStatus & 0x10000000) > 0 )
			{
				meter.rgbLED = rgbRed;
				meter.strLED = ConstData.strNoConnect;
			}
				
			if ((data.nStatus & 0x00000001) > 0 )
			{	
				meter.rgbLED = rgbYellow;
				meter.strLED = ConstData.strAbnoralData;
			}
		}
		else
		{
			meter.rgbLED = rgbGreen;
			meter.strLED = ConstData.strNormal;
		}
		
		if ((data.nStatus & 0x00080000) > 0 )
		{
			meter.rgbGPS = rgbRed;
			meter.strGPS = ConstData.strGPSFail;
		}
		else
		{
			meter.rgbGPS = rgbGreen;
			meter.strGPS = ConstData.strGPS;
		}
			
		if ((data.nStatus & 0x0000C000) > 0 )
		{
			meter.rgbBatty = rgbRed;
			meter.strBattry = ConstData.strBattryLow;
		}
		else
		{
			meter.rgbBatty = rgbGreen;
			meter.strBattry = ConstData.strBattry;
		}

		if ((data.nStatus & 0x00020000) > 0 )
		{
			meter.rgbComm = rgbRed;
			meter.strComm = ConstData.strCommErr;
		}
		else
		{
			meter.rgbComm = rgbGreen;
			meter.strComm = ConstData.strCommuni;
		}

		if ((data.nStatus & 0x00003800) > 0 )
		{
			meter.rgbDetector = rgbRed;
			meter.strDector = ConstData.strDetectorErr;
		}
		else
		{
			meter.rgbDetector = rgbGreen;
			meter.strDector = ConstData.strDetector;
		}

		if ((data.nStatus & 0x00000700) > 0 )
		{
			meter.rgbAlarm = rgbRed;
			if ((data.nStatus & 0x00000100) > 0)
				meter.strAlarm = ConstData.strHihiAlarm;
			if ((data.nStatus & 0x00000200) > 0)
				meter.strAlarm = ConstData.strHiAlarm;
			if ((data.nStatus & 0x00000300) > 0)
				meter.strAlarm = ConstData.strLoAlarm;
		}
		else
		{
			meter.rgbAlarm = rgbOff;
			meter.strAlarm = ConstData.strAlarm;
		}

		if ((data.nStatus & 0x00100000) > 0 )
		{
			meter.rgbMoni = rgbRed;
			meter.strMoni = ConstData.strMonitorErr;
		}
		else
		{
			meter.rgbMoni = rgbGreen;
			meter.strMoni = ConstData.strMonitor;
		}
		
		if (data.cValidType == HiLowData.NaI)
		{
				meter.nValue = data.nNaIValue/10.0;
				meter.dateData = data.date;
				switch(data.cNaIUnit)
				{
					case HiLowData.nGyh:
							meter.strUnit = "nGy/h";
							break;
					case HiLowData.uGyh:
							meter.strUnit = "uGy/h";
							break;
					case HiLowData.mGyh:
							meter.strUnit = "mGy/h";
							break;
				}
		}
		else
		{
			meter.nValue = data.nSSDrate / 10;
			meter.dateData = data.date;
			switch(data.cSSDUnit)
			{
				case HiLowData.nGyh:
						meter.strUnit = "nGy/h";
						break;
				case HiLowData.uGyh:
						meter.strUnit = "uGy/h";
						break;
				case HiLowData.mGyh:
						meter.strUnit = "mGy/h";
						break;
			}
		
		}
		
		meter.redraw();
	}
	public void setTitle(String str)
	{
		group.setText(str);
	}

	public void setStationName(String str)
	{
		label.setText(str);
	}

	public void Enable(boolean b)
	{
		meter.Enable = b;
	}

	public void Pause(boolean pause)
	{
		dataTask.Pause = pause;
	}
	public boolean isPaused()
	{
		return dataTask.Paused;
	}

}
