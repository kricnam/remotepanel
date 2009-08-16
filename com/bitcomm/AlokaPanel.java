/**
 * 
 */
package com.bitcomm;

import org.eclipse.swt.SWT;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * @author mxx
 *
 */
public class AlokaPanel {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO 自动生成方法存根
		
		Display d = new Display();
		Shell shell=new Shell(d);

		//shell.setLocation(0, 0);
		shell.setText("控制台");
		//shell.setMaximized(true);
		//shell.setSize(1000, 600);
		//RowLayout layout = new RowLayout();
		//layout.numColumns = 2;
		//shell.setLayout(layout);
		
		//layout.marginLeft = 10;
		
		Composite tool = new Composite(shell,SWT.NONE);
		Composite Meters = new Composite(shell,SWT.BORDER);
		
		tool.setBounds(0, 0, 80, shell.getClientArea().height);
	
		
		ToolBar toolbar = new ToolBar(tool,SWT.NONE|SWT.VERTICAL|SWT.FLAT);
		//toolbar.setBackground(d.getSystemColor(SWT.COLOR_BLUE));
		ToolItem itemSetup = new ToolItem(toolbar,SWT.PUSH);
		ToolItem itemTrend = new ToolItem(toolbar,SWT.PUSH);
		Image imgSetup = new Image(d,"com/bitcomm/resource/setup.png");
		Image imgNum = new Image(d,"com/bitcomm/resource/numbers.png");
		itemSetup.setText("设置");
		itemSetup.setImage(imgSetup);
		imgSetup.dispose();
		itemTrend.setText("趋势图");
		itemTrend.setImage(imgNum);
		imgNum.dispose();
		toolbar.pack();
				
		Meters.setBounds(tool.getSize().x, 0, shell.getClientArea().width - 80, shell.getClientArea().height);
		
		GridLayout meterLayout= new GridLayout();
		meterLayout.numColumns = 1;
		Meters.setLayout(meterLayout);
		
		AnalogMeter m = new AnalogMeter(Meters,SWT.NONE);
		//AnalogMeter n = new AnalogMeter(Meters,SWT.NONE);
		//ChartGraph g= new ChartGraph(Meters,SWT.NONE);
		
		GridData layoutData = new GridData(SWT.FILL,SWT.FILL,true,true);

		m.setLayoutData(layoutData);
		//n.setLayoutData(layoutData);
		//n.setLayoutData(layoutData);
		m.setSize(400, 350);
		m.setLocation(0, 0);
		//Meters.pack();
		//n.setLocation(400, 0);
		//n.setSize(400, 350);
		//g.setLocation(0,0);
		//g.setSize(800,400);
		//g.Data = new double[1][360];
		for (int i=0;i<360 ;i++)
		{
			//g.Data[0][i]= Math.sin(i*Math.PI/180);
		}
		//g.Margin =20;
		//g.nMaxData = 1;
		//g.nMinData = -1;
//		g.nXMarkNum = 10;
//		g.nYMarkNum = 10;

		
		shell.open();
		shell.layout();
		itemSetup.setSelection(false);
        while (!shell.isDisposed()){
        	 if (!d.readAndDispatch()) 
        	 {
        		/* m.nValue = m.nValue-1;
        		 n.nValue -= 1;
        		 n.redraw();
        		 m.redraw();
        		 */
        		 d.sleep();
        	 }
         }
	}
	}


