package com.bitcomm;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class MeterView extends Composite {
	Group group;
	AnalogMeter meter;

	public MeterView(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		//setSize(new Point(300, 200));
		setLayout(new FillLayout());
	
		group= new Group(this,SWT.NONE);
		group.setLayout(new GridLayout());
		
		GridData layoutData = new GridData(SWT.FILL,SWT.FILL,true,true);
		meter = new AnalogMeter(group,SWT.BORDER);
		meter.setLayoutData(layoutData);
		
		
	}
	public void setTitle(String str)
	{
		group.setText(str);
	}

}
