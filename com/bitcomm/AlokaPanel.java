/**
 * 
 */
package com.bitcomm;

import java.io.IOException;

import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
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
public class AlokaPanel {

	/**
	 * @param args
	 */
	static Display d;
	static Shell shell;
	
	public static void main(String[] args) {
		// TODO 自动生成方法存根
		d = new Display();
		shell =new Shell(d);

		//shell.setLocation(0, 0);
		shell.setText("控制台");
		//shell.setMaximized(true);
		shell.setSize(800, 600);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		
		shell.setLayout(layout);
		
		//layout.marginLeft = 10;
		
		Composite tool = new Composite(shell,SWT.BORDER);
		Composite Meters = new Composite(shell,SWT.BORDER);
		
		tool.setBounds(0, 0, 80, shell.getClientArea().height);

		ToolBar toolbar = new ToolBar(tool,SWT.NONE|SWT.VERTICAL|SWT.FLAT|SWT.BORDER);
		d.beep();
		//.setBackground(d.getSystemColor(SWT.COLOR_BLACK));
		tool.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,true));
		new ToolItem(toolbar,SWT.FLAT);
		ToolItem itemSetup = new ToolItem(toolbar,SWT.PUSH);
		ToolItem itemTrend = new ToolItem(toolbar,SWT.PUSH);
		ToolItem itemReport = new ToolItem(toolbar,SWT.PUSH);
		ToolItem itemSpectrum = new ToolItem(toolbar,SWT.PUSH);
		ToolItem itemClose = new ToolItem(toolbar,SWT.PUSH);
		
		Image imgSetup = new Image(d,"com/bitcomm/resource/setup.png");
		Image imgNum = new Image(d,"com/bitcomm/resource/numbers.png");
		Image imgReport = new Image(d,"com/bitcomm/resource/report.png");
		Image imgSpectrum = new Image(d,"com/bitcomm/resource/spectrum.png");
		Image imgClose = new Image(d,"com/bitcomm/resource/power_on_blue.png");
		
		itemSetup.setText("设置");
		itemSetup.setImage(imgSetup);
		imgSetup.dispose();
		
		itemSetup.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e){
				
				PreferenceManager manager= new PreferenceManager();
				PreferenceNode node1= new PreferenceNode("System","系统设置",null,SetupPage.class.getName());
				manager.addToRoot(node1);
				PreferenceDialog dlg = new PreferenceDialog(shell,manager);
				PreferenceStore store = new PreferenceStore("config.ini");
				try{
					store.load();
					dlg.setPreferenceStore(store);
					dlg.open();
					store.save();
				}
				catch(IOException ex){
					ex.printStackTrace();
				}
			}
			public void widgetDefaultSelected(SelectionEvent e){
			}
		});
		
		itemTrend.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e){
				Shell s = new Shell(shell);
				s.setLayout(new FillLayout());
				new TrendView(s,SWT.BORDER);
				s.open();
				s.layout();
				
			}
			public void widgetDefaultSelected(SelectionEvent e){
			}
		});
		
		itemTrend.setText("趋势图");
		itemTrend.setImage(imgNum);
		imgNum.dispose();

		itemReport.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e){
				Shell s = new Shell(shell);
				ReprotView report = new ReprotView(s,SWT.BORDER);
				s.setLayout(new FillLayout());
				s.open();
				
				s.layout();
			}
			public void widgetDefaultSelected(SelectionEvent e){
			}
		});
		
		itemReport.setText("报表");
		itemReport.setImage(imgReport);
		imgReport.dispose();
		
		itemSpectrum.setText("频谱");
		itemSpectrum.setImage(imgSpectrum);
		imgSpectrum.dispose();

		itemSpectrum.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e){
				Shell s = new Shell(shell);
				s.setLayout(new FillLayout());
				s.open();
				s.layout();
			}
			public void widgetDefaultSelected(SelectionEvent e){
			}
		});
		
		itemClose.setText("关闭");
		itemClose.setImage(imgClose);
		imgClose.dispose();
		itemClose.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e){
				d.dispose();
			}
			public void widgetDefaultSelected(SelectionEvent e){
			}
		});

		
		toolbar.pack();
		
		//Meters.setBounds(tool.getSize().x, 0, shell.getClientArea().width - 80, shell.getClientArea().height);
		
		GridLayout meterLayout= new GridLayout();
		//
		Meters.setLayout(meterLayout);
		Meters.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		//AnalogMeter m = new AnalogMeter(Meters,SWT.NONE);
		//AnalogMeter n = new AnalogMeter(Meters,SWT.NONE);
		//ChartGraph g= new ChartGraph(Meters,SWT.NONE);
		
		GridData layoutData = new GridData(SWT.FILL,SWT.FILL,true,true);
		MeterView meter ;
		for(int i =0 ;i < 4;i++)
		{
		  meter = new MeterView(Meters,SWT.NONE);
		  meter.setLayoutData(layoutData);
		  
		  meter.setTitle(new String("站点")+String.valueOf(i+1));
		}
		meterLayout.numColumns = (int)(Math.ceil(Math.sqrt(4)));
		//m.setLayoutData(layoutData);
		//n.setLayoutData(layoutData);
		//n.setLayoutData(layoutData);
		//m.setSize(400, 350);
		//m.setLocation(0, 0);
		Meters.pack();
		//n.setLocation(400, 0);
		//n.setSize(400, 350);
		//g.setLocation(0,0);
		//g.setSize(800,400);
		//g.Data = new double[1][360];
		//for (int i=0;i<360 ;i++)
		//{
			//g.Data[0][i]= Math.sin(i*Math.PI/180);
		//}
		//g.Margin =20;
		//g.nMaxData = 1;
		//g.nMinData = -1;
//		g.nXMarkNum = 10;
//		g.nYMarkNum = 10;

		
		shell.open();
		shell.layout();
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


