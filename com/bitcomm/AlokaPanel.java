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
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
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
		shell =new Shell(d,SWT.MIN);
		shell.setText(ConstData.strName);
		Image imgShell = new Image(d,"com/bitcomm/resource/burn.png");
		shell.setImage(imgShell);
		shell.setMaximized(true);
				
		//shell.setSize(d.getClientArea().width, d.getClientArea().height);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		
		shell.setLayout(layout);
		
		Composite tool = new Composite(shell,SWT.NONE);
		Composite Logo = new Composite(shell,SWT.NONE);
		
		Composite Meters = new Composite(shell,SWT.BORDER);
		GridData gridData = new GridData(SWT.FILL,SWT.FILL,true,false);
		gridData.heightHint = 36;
		Logo.setLayoutData(gridData);
		tool.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false,1,2));
		
		LogoView logoView = new LogoView(Logo,SWT.NONE);
		logoView.setLayout(new FillLayout());
		Logo.setLayout(new FillLayout());
		
		
		//tool.setBounds(0, 0, 80, shell.getClientArea().height);

		ToolBar toolbar = new ToolBar(tool,SWT.NONE|SWT.VERTICAL|SWT.PUSH|SWT.BORDER);
		d.beep();
		//.setBackground(d.getSystemColor(SWT.COLOR_BLACK));
		//tool.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,true));
		//new ToolItem(toolbar,SWT.FLAT);
		
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
		
		itemSetup.setText(ConstData.strConfig);
		itemSetup.setImage(imgSetup);
		imgSetup.dispose();
		
		itemSetup.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e){
				
				PreferenceManager manager= new PreferenceManager();
				PreferenceNode node1= new PreferenceNode("System",ConstData.strSysSetup,null,SetupPage.class.getName());
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
		
		itemTrend.setText(ConstData.strTrend);
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
		
		itemReport.setText(ConstData.strReport);
		itemReport.setImage(imgReport);
		imgReport.dispose();
		
		itemSpectrum.setText(ConstData.strSpetru);
		itemSpectrum.setImage(imgSpectrum);
		imgSpectrum.dispose();

		itemSpectrum.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e){
				Shell s = new Shell(shell);
				s.setLayout(new FillLayout());
				SpectrumView spec = new SpectrumView(s,SWT.BORDER);
				s.open();
				s.layout();
			}
			public void widgetDefaultSelected(SelectionEvent e){
			}
		});
		
		itemClose.setText(ConstData.strClose);
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
		
		GridData layoutData = new GridData(SWT.FILL,SWT.FILL,true,true);
		PreferenceStore store = new PreferenceStore("./config.ini");
		try{
			store.load();
		}
		catch(IOException eio)
		{
			eio.printStackTrace();
			return;
		}
		int Num = store.getInt("StationNum");
		MeterView meter ;
		for(int i =0 ;i < Num;i++)
		{
		  meter = new MeterView(Meters,SWT.NONE);
		  meter.setLayoutData(layoutData);
		  if (i==0) meter.Enable(true);
		  meter.setTitle(ConstData.strStation+String.valueOf(i+1));
		  meter.meter.nValue+=5*i;
		}
		meterLayout.numColumns = (int)(Math.ceil(Math.sqrt(Num)));
		Meters.pack();
		shell.open();
		shell.layout();
		
        while (!shell.isDisposed()){
        	 if (!d.readAndDispatch()) 
        	 {
        		 d.sleep();
        		 
        	 }
         }
	}
}


