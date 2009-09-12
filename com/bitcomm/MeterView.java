package com.bitcomm;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class MeterView extends Composite  {
	Group group;
	AnalogMeter meter;
	HiLowData data;
	CommunicationTask dataSource;

	public MeterView(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		// setSize(new Point(300, 200));
		setLayout(new FillLayout());
	
		group= new Group(this,SWT.NONE);
		group.setLayout(new GridLayout());
		
		GridData layoutData = new GridData(SWT.FILL,SWT.FILL,true,true);
		meter = new AnalogMeter(group,SWT.BORDER);
		meter.setLayoutData(layoutData);
		dataSource = new CommunicationTask(this);
		dataSource.strServer = "61.135.144.51";
		dataSource.nPort = 9998;
		dataSource.start();
	}
	public void setValue()
	{
		System.out.print("L");
		System.out.println((int)(data.DataNum&0x0FFFF));
		System.out.println(data.gps.ToString());
		System.out.println(data.nThermoral/10.0);
		System.out.println(data.nHVVolt/10.0);
		System.out.println(data.nBattVoltage/10.0);
		
		System.out.println(data.nSSD_dr_count);
		System.out.println(data.nNaI_cr_count);
		System.out.println(data.nNaI_dr_count);
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

	public void Enable(boolean b)
	{
		meter.Enable = b;
	}


}
