/**
 * 
 */
package com.bitcomm;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * @author mxx
 *
 */
public class AlokaPanel {

	static Display d;
	static Shell shell;
	static MeterView []meter;
	static BackupView backup;
	static TrendView trend;
	static SpectrumView spectrum;
	static ReprotView report;
	static LogoView logoView;
	static String homedir;
	static int nTimeOffset;
	static String strExtIp;
	static int nExtIPtryCounter;
	static void MessageBox(String strTitle,String strMsg)
	{
		MessageBox box = new MessageBox(shell);
		box.setText(strTitle);
		box.setMessage(strMsg);
		box.open();
	}
	
	static void SaveSetting(String key, String Value)
	{
		PreferenceStore store = new PreferenceStore("config.ini");
		
		try{
			store.load();
			store.setValue(key, Value);
			store.save();
		}
		catch(IOException eio)
		{
			MessageBox("Error",eio.getMessage());
			eio.printStackTrace();
		}
	}
	
	static void SaveSetting(String key, int Value)
	{
		PreferenceStore store = new PreferenceStore("config.ini");
		
		try{
			store.load();
			store.setValue(key, Value);
			store.save();
		}
		catch(IOException eio)
		{
			MessageBox("Error",eio.getMessage());
			eio.printStackTrace();
		}
	}
	
	static void SaveSetting(String key, long Value)
	{
		PreferenceStore store = new PreferenceStore("config.ini");
		
		try{
			store.load();
			store.setValue(key, Value);
			store.save();
		}
		catch(IOException eio)
		{
			MessageBox("Error",eio.getMessage());
			eio.printStackTrace();
		}
	}
	static void SaveSetting(String key, boolean Value)
	{
		PreferenceStore store = new PreferenceStore("config.ini");
		
		try{
			store.load();
			store.setValue(key, Value);
			store.save();
		}
		catch(IOException eio)
		{
			MessageBox("Error",eio.getMessage());
			eio.printStackTrace();
		}
	}
	static int GetSettingInt(String key)
	{
		PreferenceStore store = new PreferenceStore("config.ini");
		
		try{
			store.load();
			return store.getInt(key);
		}
		catch(IOException eio)
		{
			MessageBox("Error",eio.getMessage());
			eio.printStackTrace();
			return 0;
		}
	}
	
	static long GetSettingLong(String key)
	{
		PreferenceStore store = new PreferenceStore("config.ini");
		
		try{
			store.load();
			return store.getLong(key);
		}
		catch(IOException eio)
		{
			MessageBox("Error",eio.getMessage());
			eio.printStackTrace();
			return 0;
		}
	}
	
	static boolean GetSettingBool(String key)
	{
		PreferenceStore store = new PreferenceStore("config.ini");
		
		try{
			store.load();
			return store.getBoolean(key);
		}
		catch(IOException eio)
		{
			MessageBox("Error",eio.getMessage());
			eio.printStackTrace();
			return false;
		}
	}
	static String GetSettingString(String key)
	{
		PreferenceStore store = new PreferenceStore("config.ini");
		
		try{
			store.load();
			return store.getString(key);
		}
		catch(IOException eio)
		{
			MessageBox("Error",eio.getMessage());
			eio.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		
	
		File root = new File("root");
		if (!root.exists())
					root.mkdir();
		homedir = System.getProperty("user.dir");
		nExtIPtryCounter = 0;
		PreferenceStore store = new PreferenceStore("./config.ini");
		try{
			store.load();
		}
		catch(IOException eio)
		{
			MessageBox("Error",eio.getMessage());
			eio.printStackTrace();
			return;
		}
		nTimeOffset = store.getInt("TIMEOFFSET");
		if (nTimeOffset > 24) nTimeOffset = 24;
		if (nTimeOffset < -24) nTimeOffset = -24;
//		System.out.println("timeoffset="+nTimeOffset);
		
		d = new Display();
		
		shell =new Shell(d);//,SWT.MIN|SWT.MAX);
		shell.setText(ConstData.strName);
		Image imgShell = new Image(d,"com/bitcomm/resource/burn.png");
		shell.setImage(imgShell);
		shell.setMaximized(true);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;

		shell.setLayout(layout);
		
		Composite tool = new Composite(shell,SWT.NONE);
		Composite Logo = new Composite(shell,SWT.NONE);

		tool.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent arg0) {
			
			}
			
			public void keyPressed(KeyEvent arg0) {
				//System.out.println(arg0.toString());
				int bit = SWT.CTRL|SWT.ALT;
				if ((arg0.keyCode=='c') && ((arg0.stateMask & bit) > 0))
				{
					Setup();
				}
				
			}
		});
		Composite Meters = new Composite(shell,SWT.BORDER);
		GridData gridData = new GridData(SWT.FILL,SWT.FILL,true,false);
		gridData.heightHint = 36;
		Logo.setLayoutData(gridData);
		tool.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false,1,2));

		logoView = new LogoView(Logo,SWT.NONE);
		logoView.setLayout(new FillLayout());
		Logo.setLayout(new FillLayout());

		ToolBar toolbar = new ToolBar(tool,SWT.NONE|SWT.VERTICAL|SWT.PUSH|SWT.NONE);
		d.beep();

		ToolItem itemSetup = new ToolItem(toolbar,SWT.PUSH);
		ToolItem itemTrend = new ToolItem(toolbar,SWT.PUSH);
		ToolItem itemReport = new ToolItem(toolbar,SWT.PUSH);
		ToolItem itemSpectrum = new ToolItem(toolbar,SWT.PUSH);
		ToolItem itemBackup = new ToolItem(toolbar,SWT.BORDER);
		ToolItem itemMap = new ToolItem(toolbar,SWT.PUSH);
		ToolItem itemClose = new ToolItem(toolbar,SWT.PUSH);
		

		Image imgSetup = new Image(d,"com/bitcomm/resource/setup.png");
		Image imgNum = new Image(d,"com/bitcomm/resource/numbers.png");
		Image imgReport = new Image(d,"com/bitcomm/resource/report.png");
		Image imgSpectrum = new Image(d,"com/bitcomm/resource/spectrum.png");
		Image imgClose = new Image(d,"com/bitcomm/resource/power_off.png");
		Image imgBackup = new Image(d,"com/bitcomm/resource/backup.png");
		Image imgSetupDis = new Image(d,"com/bitcomm/resource/setup.png");
		Image imgMap = new Image(d,"com/bitcomm/resource/map.png");

		
		itemSetup.setImage(imgSetup);
		itemSetup.setDisabledImage(imgSetupDis);
		itemSetup.setEnabled(false);
		itemSetup.setText("Getting ip...");
		
		itemSetup.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e){
				Setup();
			}
			public void widgetDefaultSelected(SelectionEvent e){
			}
		});

		itemTrend.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e){
				
				if (trend!=null)
				{
					trend.getShell().setActive();
					return;
				}
				if (backup!=null)
					return;
				Shell s = new Shell(shell,SWT.RESIZE|SWT.DIALOG_TRIM);
				s.setLayout(new FillLayout());
				trend=new TrendView(s,SWT.BORDER);
				trend.meter = meter;
				s.setText(ConstData.strTrend);
				s.open();
				s.layout();

			}
			public void widgetDefaultSelected(SelectionEvent e){
			}
		});

		itemTrend.setText(ConstData.strTrend);
		itemTrend.setImage(imgNum);
		

		itemReport.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e){
				if (report!=null)
				{
					report.getShell().setActive();
					return;
				}
				Shell s = new Shell(shell,SWT.RESIZE|SWT.DIALOG_TRIM);
				GC gc = new GC(s);
				Point pt=gc.stringExtent("888");
				s.setSize(600,pt.y*54);
				shell.setCursor(d.getSystemCursor(SWT.CURSOR_WAIT));
				report=new ReprotView(meter,s,SWT.BORDER);
				s.setLayout(new FillLayout());
				s.setText("Report");
				s.open();
				shell.setCursor(null);
				s.layout();
			}
			public void widgetDefaultSelected(SelectionEvent e){
			}
		});

		itemReport.setText(ConstData.strReport);
		itemReport.setImage(imgReport);
		

		itemSpectrum.setText(ConstData.strSpetru);
		itemSpectrum.setImage(imgSpectrum);
		

		itemSpectrum.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e){
				if (spectrum!=null)
				{
					spectrum.getShell().setActive();
					return;
				}
				Shell s = new Shell(shell);
				s.setText(ConstData.strSpetru);
				SpectrumView spec = new SpectrumView(s,SWT.BORDER);
				//s.setLocation(0, 16);
				s.setMaximized(true);
				//s.setSize(d.getClientArea().width, d.getClientArea().height - 20);
				s.setLayout(new FillLayout());
				spec.meter = meter;
				s.open();
				s.layout();
			}
			public void widgetDefaultSelected(SelectionEvent e){
			}
		});

		itemBackup.setText(ConstData.strBackup);
		itemBackup.setImage(imgBackup);
		itemBackup.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e){
				if (backup!=null) 
				{
						backup.getShell().setActive();
						return;
				}
				if (trend!=null)
				{
					return;
				}
				Shell diag = new Shell(shell);
				diag.setLayout(new FillLayout());
				diag.setText(ConstData.strBackup);
				backup=new BackupView(diag,SWT.BORDER,meter);
				diag.open();
				diag.layout();
					
			}
			public void widgetDefaultSelected(SelectionEvent e){
			}
		});
		
		itemMap.setText("Map");
		itemMap.setImage(imgMap);
		itemMap.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e){
				LoadMap();
			}
			public void widgetDefaultSelected(SelectionEvent e){
			}
		});
		
		itemClose.setText(ConstData.strClose);
		itemClose.setImage(imgClose);
		
		itemClose.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e){
				shell.close();
			}
			public void widgetDefaultSelected(SelectionEvent e){
			}
		});
		toolbar.pack();

		GridLayout meterLayout= new GridLayout();
		Meters.setLayout(meterLayout);
		Meters.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));

		GridData layoutData = new GridData(SWT.FILL,SWT.FILL,true,true);
		
		int Num = store.getInt("StationNum");
		meter = new MeterView[Num];
		String strIP = store.getString(ConstData.strKeyServerURL);
		for (int i=0 ; i < Num ;i++)
		{
			meter[i] = new MeterView(Meters,SWT.NONE);
			meter[i].setLayoutData(layoutData);
			meter[i].Enable(false);
			meter[i].setTitle(ConstData.strDataTitel+" "+String.valueOf(i+1));
			String strName = store.getString(ConstData.strStation.replace(" ", "_")
					+"_"+String.valueOf(i+1)+"_Name");
			if (strName == null || strName.length() == 0) strName = "Unknow"; 
			meter[i].setStationName(strName);
			if (i==0)
			{
				
				meter[i].Enable(true);
				meter[i].nMachineNum = store.getInt(
						ConstData.strStation.replace(" ", "_")
						+"_"+String.valueOf(i+1)+"_MNUM");
				meter[i].ComPort.strServer=strIP;
				meter[i].ComPort.nPort = store.getInt(
						ConstData.strStation.replace(" ", "_")
						+"_"+String.valueOf(i+1)+"_Port");
				meter[i].dataTask.nInterval = store.getInt(
						ConstData.strStation.replace(" ", "_")
						+"_"+String.valueOf(i+1)+"_INTERVAL");
				if (meter[i].dataTask.nInterval==0) meter[i].dataTask.nInterval=600;
				
				Inet4Address ip;
				try {
					ip = (Inet4Address) InetAddress.getByName(strIP);
				
					if (ip.isLinkLocalAddress() || ip.isLoopbackAddress())
					{
						System.out.println("is local");
						meter[i].server=new TCPServer(meter[i].ComPort.nPort);
						new Thread(meter[i].server).start();
					}
					
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				}
				
				meter[i].dataTask.Pause = true;
				meter[i].dataTask.start();
				meter[i].showOffLine();
			}
		}


		meterLayout.numColumns = (int)(Math.ceil(Math.sqrt(Num)));
		if (Num==8) meterLayout.numColumns = 4;
		Meters.pack();
		
		shell.addListener(SWT.Close, new Listener() {
		      public void handleEvent(Event event) {
		    	    
					MessageBox box = new MessageBox(shell, SWT.ICON_WARNING
							| SWT.YES |SWT.NO);
					box.setMessage("Do you really want to quit?");
					box.setText("QUIT");
					
					if (box.open() == SWT.YES)
					{
				          event.doit = true;
				          shell.dispose();
				          d.dispose();
					}
				    else
				          event.doit = false;
					return;
		      }
		    });

		shell.open();
		shell.layout();
		meter[0].dataTask.Pause = false;
		

		
		while (!shell.isDisposed()){
			if (!d.readAndDispatch()) 
			{
				if ((nExtIPtryCounter< 3) && (strExtIp == null)) {
					Cursor cursor = d.getSystemCursor(SWT.CURSOR_WAIT);
					shell.setCursor(cursor);
					GetExtIp();
					if (strExtIp != null)
						itemSetup.setText(strExtIp);
					shell.setCursor(null);
				}
				d.sleep();
			}
		}

		if (meter[0].server!=null) meter[0].server.stop();
		meter[0].dataTask.Stop = true;
		meter[0].dataTask.interrupt();

		imgSetup.dispose();
		imgNum.dispose();
		imgReport.dispose();
		imgSpectrum.dispose();
		imgClose.dispose();
		imgSetupDis.dispose();
		imgMap.dispose();
		
	}

	static void GetExtIp()
	{
		if (nExtIPtryCounter>3) return;
		if (strExtIp == null || strExtIp.isEmpty()) {
			nExtIPtryCounter++;
			ExternalIPFetcher fetcher = new ExternalIPFetcher(
					"http://checkip.dyndns.org/");
			strExtIp = fetcher.getMyExternalIpAddress();
			fetcher = null;
			System.out.println("Externale IP:" + strExtIp);
		}
	}
	
	static void LoadMap()
	{
		Shell ie = new Shell(shell);
		Cursor cursor = d.getSystemCursor(SWT.CURSOR_WAIT);
		shell.setCursor(cursor);

		Browser browser = new Browser(ie, SWT.NONE);
		System.out.println(browser.getBrowserType());
		
		Rectangle rect = d.getBounds();
		
		ie.setBounds(rect.x+rect.width/8,rect.y+rect.height/8,rect.width/8*6,Math.max(600,rect.height/8*6));
		browser.setBounds(ie.getClientArea());
		writeHtml(browser.getBrowserType());
		
		String ipAddress = "maps.google.com.hk";
				
		Socket   clientSocket;

		try {
			clientSocket = new Socket();
			SocketAddress remoteAddr=new InetSocketAddress(ipAddress,80);
			clientSocket.connect(remoteAddr, 2000);
			System.out.println("port opend");
			clientSocket.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			MessageBox("Warning" , "You network seems slow or blocked on connecting to google map server.\nthe map may not be displayed quickly or correctly.");
		}
		clientSocket = null;
		shell.setCursor(null);
		
		ie.setText("Google Map");
		ie.open();
		browser.setUrl("file://" + homedir + "/map/GoogleMaps.html");
		ie.layout();
		ie.setCursor(d.getSystemCursor(SWT.CURSOR_WAIT));
	}
	
	static void writeHtml(String strType) {
		String s ;
		String strTmplt;
		StringBuffer sb = new StringBuffer();
		if (strType.matches("ie"))
			strTmplt = homedir + "/map/ieGoogleMaps.html.tmplt";
		else
			strTmplt = homedir + "/map/GoogleMaps.html.tmplt";
		File f = new File(strTmplt);
		
		if (f.exists()) {
			try {
				FileInputStream st = new FileInputStream(f);
				InputStreamReader sr = new InputStreamReader(st);
				BufferedReader br = new BufferedReader(sr);
				while ((s = br.readLine()) != null) {
					sb.append(s+"\n");
					
				}
				st.close();
				sr.close();
				s = sb.toString();
				
				Float lang  = (float)meter[0].data.gps.lgDegree ;
				lang = lang + meter[0].data.gps.lgMinute / 60 + (meter[0].data.gps.lgSecond10 / 10) / 3600;
				Float lati;
				lati = (float)meter[0].data.gps.laDegree + meter[0].data.gps.laMinute / 60 +
				                   (meter[0].data.gps.laSecond10 / 10) / 3600;;
				if (lang<1) lang = (float)104.0466;
				if (lati<1) lati = (float)30.648;
 				s = s.replaceAll("LANGI", lang.toString());
				s = s.replaceAll("LATI", lati.toString());
				String strText;
				strText = "Station:" + meter[0].label.getText();
				strText += "<br/>Dose Rate:" ;
				
				if (meter[0].meter.strUnit!=null)
					strText +=  meter[0].meter.nValue + meter[0].meter.strUnit;
				else
					strText += "--";
				
				s = s.replaceAll("TEXT_LEBAL", strText);
				
				FileWriter fw = new FileWriter(homedir + "/map/GoogleMaps.html");
				
				fw.write(s);
				fw.flush();
				fw.close();
				

			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		} else {
			return;
		}
	}

	static void Setup()
	{
		PreferenceManager manager= new PreferenceManager();
		PreferenceStore store = new PreferenceStore("config.ini");
		
		try{
			store.load();
		}
		catch(IOException eio)
		{
			MessageBox("Error",eio.getMessage());
			eio.printStackTrace();
		}
		
		PreferenceNode node1= new PreferenceNode("System",ConstData.strSysSetup,null,SetupPage.class.getName());
		manager.addToRoot(node1);
		int nNum = store.getInt("StationNum");
		
		if (nNum==0) nNum = 4;
		PreferenceNode[] node = new PreferenceNode[nNum];
		for (int i =0;i< nNum;i++)
		{
			node[i] = new PreferenceNode("System.Station"+String.valueOf(i+1),
					ConstData.strStation+" "+String.valueOf(i+1),
					null,
					SetupStationPage.class.getName());
			manager.addToRoot(node[i]);
		}
		
		PreferenceDialog dlg = new PreferenceDialog(shell,manager);
		
		try
		{
			store.load();
			dlg.setPreferenceStore(store);
			dlg.open();
			store.save();
		}
		catch(IOException ex){
			MessageBox("Warning",ex.getMessage());
			ex.printStackTrace();
		}
		
	}
}


