/**
 * 
 */
package com.bitcomm;

import org.eclipse.swt.SWT;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * @author mxx
 *
 */
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO 自动生成方法存根
		
		Display d = new Display();
		Shell shell=new Shell(d);

		shell.setLocation(0, 0);
		shell.setText("控制台");
		//shell.setMaximized(true);
		//shell.setSize(1800, 600);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		shell.setLayout(layout);
		
		Composite tool = new Composite(shell,SWT.NONE);
		ToolBar toolbar = new ToolBar(tool,SWT.NONE);
		ToolItem item = new ToolItem(toolbar,SWT.PUSH);
		ToolItem item1 = new ToolItem(toolbar,SWT.PUSH);
		item.setText("设置");
		item1.setText("www");
		//item1.setImage(ImageFactory.loadImage(toolbar.getDisplay(),imageFactory.ADD_OBJ));
		tool.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,true,false));
		toolbar.pack();
		
		Composite Meters = new Composite(shell,SWT.BORDER);
		GridLayout meterLayout= new GridLayout();

		Meters.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));

		AnalogMeter m = new AnalogMeter(Meters,SWT.NONE);
		AnalogMeter n = new AnalogMeter(Meters,SWT.NONE);
		
		GridData layoutData = new GridData(SWT.FILL,SWT.FILL,true,true);
		meterLayout.numColumns = 2;
		//Meters.setLayout(meterLayout);

		m.setLayoutData(layoutData);
		n.setLayoutData(layoutData);
		m.setSize(400, 350);
		m.setLocation(0, 0);
		n.setLocation(400, 0);
		n.setSize(400, 350);

		shell.open();
		shell.layout();
        while (!shell.isDisposed()){
        	 if (!d.readAndDispatch()) 
        	 {
        		 m.nValue = m.nValue-1;
        		 n.nValue -= 1;
        		 n.redraw();
        		 m.redraw();
        		 d.sleep();
        	 }
         }
	}
	}


