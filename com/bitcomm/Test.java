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
		//AnalogMeterControl c = new AnalogMeterControl(shell,SWT.PUSH);
		//Button a = new Button(shell,SWT.PUSH);
		Composite tool = new Composite(shell,SWT.NONE);
		tool.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,true,false));
		ToolBar toolbar = new ToolBar(tool,SWT.NONE);
		ToolItem item = new ToolItem(toolbar,SWT.PUSH);
		ToolItem item1 = new ToolItem(toolbar,SWT.PUSH);
		item.setText("test");
		item1.setText("www");
		toolbar.pack();
		
		AnalogMeter m = new AnalogMeter(shell,SWT.BORDER);
		AnalogMeter n = new AnalogMeter(shell,SWT.BORDER);
		//AnalogMeter mm = new AnalogMeter(shell,SWT.BORDER);
		//m.addPaintListener(listener);
		//a.setText("dddd");
		
		//shell.setSize(100, 100);
		GridLayout layout = new GridLayout();
		layout.numColumns =3;
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		m.setLayoutData(layoutData);
		n.setLayoutData(layoutData);
		shell.setLayout(layout);
		
		shell.open();
		shell.layout();
		// TODO 自动生成方法存根
		
		
		
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


