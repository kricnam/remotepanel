package com.bitcomm;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.vafada.swtcalendar.SWTCalendarEvent;
import org.vafada.swtcalendar.SWTCalendarListener;

public class TrendView extends Composite implements Listener {

	final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	Composite optionBar;
	
	ChartGraph graph;
	
	Display display;

	Label labelFrom;

	Text textFrom;

	Button calFrom;

	Label labelTo;

	Text textTo;

	Button calTo;

	Spinner hour;

	Spinner minute;

	Button list[];

	Button butDate;

	Button butPeriod;

	MeterView[] meter;
	Button butLog;
	Button butPrint;
	Button butStart;
	Spinner scaleMin;
	Spinner scaleMax;
	private Button butLoad;
	public TrendView(Composite parent, int style) {
		super(parent, style);
		display=parent.getDisplay();
		initialize();
	}

	private void initialize() {
		//setSize(new Point(300, 200));
		GridLayout layout = new GridLayout();
		
		setLayout(layout);
		//setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
		
		initOptionBar();
		initGraph();
		getShell().addListener(SWT.Close, new Listener() {
			
			
			public void handleEvent(Event arg0) {
				// TODO Auto-generated method stub
				SaveSetting();
				AlokaPanel.trend=null;
			}
		}) ;
		GetSetting();
	}
	
	void SaveSetting()
	{
		if (list!=null)
		{
			for(int i=0;i<list.length;i++)
			{
				if (list[i]!=null)
				{
					AlokaPanel.SaveSetting("TrendColor_R_"+String.valueOf(i), list[i].getBackground().getRGB().red);
					AlokaPanel.SaveSetting("TrendColor_G_"+String.valueOf(i), list[i].getBackground().getRGB().green);
					AlokaPanel.SaveSetting("TrendColor_B_"+String.valueOf(i), list[i].getBackground().getRGB().blue);
					AlokaPanel.SaveSetting("TrendSelect_"+String.valueOf(i), list[i].getSelection());
				}
					
			}
		}
		
		AlokaPanel.SaveSetting("TrendFrom", textFrom.getText());
		AlokaPanel.SaveSetting("TrendTo", textTo.getText());
		AlokaPanel.SaveSetting("TrendHour", hour.getSelection());
		AlokaPanel.SaveSetting("TrendMinute", minute.getSelection());
		AlokaPanel.SaveSetting("TrendPeriod", butPeriod.getSelection());
		AlokaPanel.SaveSetting("TrendLog", butLog.getSelection());
		AlokaPanel.SaveSetting("TrendScaleYMin", scaleMin.getSelection());
		AlokaPanel.SaveSetting("TrendScaleYMax", scaleMax.getSelection());
	}
	
	void GetSetting()
	{
		if (list!=null)
		{
			for(int i=0;i<list.length;i++)
			{
				if (list[i]!=null)
				{
					int r,g,b;
					r=AlokaPanel.GetSettingInt("TrendColor_R_"+String.valueOf(i));
					g=AlokaPanel.GetSettingInt("TrendColor_G_"+String.valueOf(i));
					b=AlokaPanel.GetSettingInt("TrendColor_B_"+String.valueOf(i));
					Color color = new Color(getDisplay(),r,g,b);
					if ((r+g+b) < 100) 
						list[i].setForeground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
					list[i].setBackground(color);
					list[i].setSelection(AlokaPanel.GetSettingBool("TrendSelect_"+String.valueOf(i)));
				}
					
			}
		}
		
		textFrom.setText(AlokaPanel.GetSettingString("TrendFrom"));
		textTo.setText(AlokaPanel.GetSettingString("TrendTo")); 
		hour.setSelection(AlokaPanel.GetSettingInt("TrendHour"));
		minute.setSelection(AlokaPanel.GetSettingInt("TrendMinute"));
		butPeriod.setSelection(AlokaPanel.GetSettingBool("TrendPeriod"));
		if (butPeriod.getSelection())
		{
			calTo.setEnabled(true);
			textTo.setEnabled(true);
			labelTo.setEnabled(true);
		}
		butDate.setSelection(!butPeriod.getSelection());
		butLog.setSelection(AlokaPanel.GetSettingBool("TrendLog"));
		if (butLog.getSelection()) graph.logScale = true;
		scaleMax.setSelection(AlokaPanel.GetSettingInt("TrendScaleYMax"));
		scaleMin.setSelection(AlokaPanel.GetSettingInt("TrendScaleYMin"));
	}
	
	private void initOptionBar(){
		PreferenceStore store = new PreferenceStore("./config.ini");
		try {
			store.load();
		} catch (IOException eio) {
			MessageBox box = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
			box.setMessage(eio.getMessage());
			box.setText("open config fail");
			box.open();
			return;
		}
		int num = store.getInt("StationNum");
		if (num == 0)
			num = 1;
		
		String[] str = new String[num];
		for (int i = 0; i < num; i++) {
			String key = ConstData.strStation.replace(" ", "_") + "_"
					+ String.valueOf(i + 1) + "_Name";

			String strName = store.getString(key);

			if (strName == null || strName.length() == 0)
				str[i] = ConstData.strUnknown;
			else
				str[i] = strName;
		}

		optionBar = new Composite(this, SWT.BORDER);
		GridLayout optionLayout = new GridLayout(3, true);
		optionBar.setLayout(optionLayout);
		optionBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		butDate = new Button(optionBar, SWT.RADIO);
		butDate.setText(ConstData.strStartDate);
		butDate
				.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, true,
						false));
		butDate.setSelection(true);

		GridData dateGrid = new GridData(SWT.END, SWT.CENTER, false, false);

		butPeriod = new Button(optionBar, SWT.RADIO);
		butPeriod.setText(ConstData.strEndDate);
		butPeriod.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, true,
				false));

		Group grpStation = new Group(optionBar, SWT.NONE);
		grpStation.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 4));
		
		int nCol = CalGroupCol(num);
		grpStation.setLayout(new GridLayout(nCol,true));
		grpStation.setText(ConstData.strStation);
		
		Composite inputDate = new Composite(optionBar, SWT.NONE);
		inputDate.setLayout(new GridLayout(3, false));
		inputDate.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false,
				false));

		labelFrom = new Label(inputDate, SWT.NONE);
		labelFrom.setText("From");
		labelFrom.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		textFrom = new Text(inputDate, SWT.BORDER);
		dateGrid.widthHint = 100;
		textFrom.setLayoutData(dateGrid);
		calFrom = new Button(inputDate, SWT.DOWN | SWT.ARROW);
		GridData dateGrid1 = new GridData(SWT.END, SWT.CENTER, false, false);
		calFrom.setLayoutData(dateGrid1);
		calFrom.setData(textFrom);

		Composite inputEnd = new Composite(optionBar, SWT.NONE);
		inputEnd.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		inputEnd.setLayout(new GridLayout(3, false));

		labelTo = new Label(inputEnd, SWT.NONE);
		labelTo.setText("To");
		labelTo.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		textTo = new Text(inputEnd, SWT.BORDER);
		textTo.setLayoutData(dateGrid);
		calTo = new Button(inputEnd, SWT.DOWN | SWT.ARROW);
		calTo.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		calTo.setData(textTo);
		calTo.setEnabled(false);
		textTo.setEnabled(false);
		labelTo.setEnabled(false);

		Group grpTime = new Group(optionBar, SWT.NONE);
		grpTime.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		grpTime.setText(ConstData.strStartTime);
		grpTime.setLayout(new GridLayout(3, false));
		hour = new Spinner(grpTime, SWT.NONE);
		hour.setMaximum(23);
		hour.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		Label comma = new Label(grpTime, SWT.NONE);
		comma.setText(":");
		minute = new Spinner(grpTime, SWT.NONE);
		minute.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		minute.setMaximum(59);

		Group scale = new Group(optionBar,SWT.NONE);
		scale.setLayout(new GridLayout(5,false));
		scale.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		scale.setText("Y scale");
		Label sMin = new Label(scale, SWT.NONE);
		sMin.setText("Min");
		scaleMin = new Spinner(scale, SWT.NONE);
		scaleMin.setMaximum(1000000000);
		scaleMin.setPageIncrement(1000);
		Label sMax = new Label(scale, SWT.NONE);
		sMax.setText("Max");
		scaleMax = new Spinner(scale, SWT.NONE);
		scaleMax.setMaximum(1000000000);
		scaleMin.setPageIncrement(1000);
		Button butApply = new Button(scale, SWT.PUSH);
		butApply.setText("Apply");
		butApply.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent arg0) {
				SetGraphScale();
			}
			
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		
		Composite cmd = new Composite(optionBar,SWT.BORDER);
		cmd.setLayout(new GridLayout(3,true));
		cmd.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,1,1));
		butLoad = new Button(cmd, SWT.PUSH);
		butLoad.setText("Load");
		butLoad.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		butStart = new Button(cmd, SWT.PUSH);
		butStart.setText(ConstData.strStart);
		butStart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		butPrint = new Button(cmd, SWT.PUSH);
		butPrint.setText(ConstData.strPrint);
		butPrint.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		butLoad.addSelectionListener(new SelectionListener() {
			
			
			public void widgetSelected(SelectionEvent arg0) {
				OnLoad();
			}
			
			
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		
		butStart.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				process();
			}
		});
		
		list = new Button[num];
		for (int i=0;i< num;i++)
		{
			list[i] = new Button(grpStation, SWT.CHECK);
			list[i].setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
			list[i].setText(str[i]);
			list[i].addSelectionListener(new SelectionListener() {
				
				
				public void widgetSelected(SelectionEvent arg0) {
					// TODO Auto-generated method stub
					if (!((Button)arg0.widget).getSelection()) return;
					ColorDialog dlg=new ColorDialog(getShell());
					
					RGB rgb = dlg.open();
					if (rgb!=null)
					((Button)arg0.widget).setBackground(new Color(getDisplay(),rgb));
				}
				
				
				public void widgetDefaultSelected(SelectionEvent arg0) {
					// TODO Auto-generated method stub
					
				}
			});
		}
		
		butLog = new Button(optionBar, SWT.CHECK);
		butLog.setText("LOG");
		butLog.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		
		
		butLog.addSelectionListener(new SelectionListener() {
			
			
			public void widgetSelected(SelectionEvent arg0) {
				graph.logScale = butLog.getSelection();
				graph.redraw();
			}
			
			
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});

		butPrint.addSelectionListener(new SelectionListener() {
			
			
			public void widgetSelected(SelectionEvent arg0) {
				PrintGraph();
			}
			
			
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});

		butDate.addListener(SWT.Selection, this);
		butPeriod.addListener(SWT.Selection, this);

		calFrom.addListener(SWT.Selection, this);
		calTo.addListener(SWT.Selection, this);

	}
	
	protected void OnLoad() {
		FileDialog dlg = new FileDialog(getShell(),SWT.SINGLE);
		dlg.setFilterExtensions(new String[]{"*.dat"});
		dlg.setFilterNames(new String[]{"dose rate data"});
		dlg.setFilterPath("./root");
		String file = dlg.open();
		if (file!=null)
		{
			try {
					DoesRateFile df = new DoesRateFile(file);
					df.load();
					graph.Data = new double[1][df.dataArray.size()];
					graph.strScaleX= new String[df.dataArray.size()];
					//System.out.println("data length="+String.valueOf(df.dataArray.size()));
					for(int i=0;i<df.dataArray.size();i++)
					{
						DoesRateData d= df.dataArray.get(i);
						if (d!=null)
						{
							graph.Data[0][i]= d.getDoesRatenGy();
							graph.strScaleX[i]= d.date.toStringShortDate()+"\r\n"+
							d.date.toStringTime();
						}
						else
						{
							graph.Data[0][i]= 0;
						}
					}
					graph.setAutoTransform();
					graph.redraw();
				} catch (IOException e) {
					AlokaPanel.MessageBox("Error", e.getMessage());
					e.printStackTrace();
				}
			}
		}



	protected void SetGraphScale() {
		if (graph==null) return;
		if (scaleMin.getSelection() >= scaleMax.getSelection())
		{
			AlokaPanel.MessageBox("Error", "Min value shall not greater or equal to Max value!");
			return;
		}
		graph.nScaleMax = scaleMax.getSelection();
		graph.nScaleMin = scaleMin.getSelection();
		graph.nMaxData = graph.nScaleMax; 
		graph.redraw();
	}

	int CalGroupCol(int num)
	{
		if (num < 5) return 1;
		if (num > 4 && num < 11) return 2;
		if (num > 10 && num < 16) return 3;
		return (int)Math.sqrt(num+1);
	}
	
	void PrintGraph()
	{
		PrintDialog dlg = new PrintDialog(getShell(),SWT.NONE);
		PrinterData printData = dlg.open();
		if (printData==null) return;
		//System.out.println("printing");
		Printer printer= new Printer(printData);
		if (printer.startJob("TrendGraph"))
		{
			GC gc = new GC(printer);
			int Margin = graph.Margin;
			Rectangle trim = printer.getBounds();
			Point dpi = printer.getDPI();
			System.out.println(trim.toString());
			if (printer.startPage()) {
				graph.AutoSetTransform(gc,dpi.x, dpi.y, trim.width-2*dpi.x, trim.height-2*dpi.y);
				graph.drawBackground(gc, dpi.x, dpi.y, trim.width-2*dpi.x, trim.height-2*dpi.y);
				graph.drawData(gc, dpi.x, dpi.y, trim.width-2*dpi.x, trim.height-2*dpi.y);
				printer.endPage();
			}
			gc.dispose();
			printer.endJob();
			graph.Margin = Margin; 
		}
		
		graph.redraw();
	}
	
	public void handleEvent(Event event) {

		if (event.widget.toString().indexOf(ConstData.strStartDate) >= 0) {
			calTo.setEnabled(false);
			textTo.setEnabled(false);
			labelTo.setEnabled(false);
			return;
		}
		if (event.widget.toString().indexOf(ConstData.strEndDate) >= 0) {
			calTo.setEnabled(true);
			textTo.setEnabled(true);
			labelTo.setEnabled(true);
			return;
		}
		final Text text = (Text) event.widget.getData();

		if (text == null)
			return;

		Image img = new Image(display, "com/bitcomm/resource/calendar.png");
		SWTCalendarDialog cal = new SWTCalendarDialog(getShell(), img);
		cal.addDateChangedListener(new SWTCalendarListener() {
			public void dateChanged(SWTCalendarEvent calendarEvent) {
				text.setText(formatter.format(calendarEvent.getCalendar()
						.getTime()));
			}
		});
		if (text.getText() != null && text.getText().length() > 0) {
			try {
				Date d = formatter.parse(text.getText());
				cal.setDate(d);
			} catch (ParseException pe) {
				pe.printStackTrace();
			}
		}
		cal.open();
		img.dispose();
	}
	

	private void initGraph(){
		
		Composite com = new Composite(this,SWT.NO_BACKGROUND);
		com.setLayout(new FillLayout());
		com.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		

		graph = new ChartGraph(com,SWT.NO_BACKGROUND);
	}
	
	void process() {
		DateTime start;
		DateTime end;
		Date startDate = null;
		Date endDate = null;
		try {
			startDate = formatter.parse(textFrom.getText());
		} catch (ParseException e) {
			MessageBox box = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
			box.setMessage(e.getMessage()
					+ "\r\nPlease select a valid date.");
			box.setText("Date Setting Error");
			box.open();
			return;
		}
		if (butPeriod.getSelection()) {
			try {
				endDate = formatter.parse(textTo.getText());
			} catch (ParseException e) {
				MessageBox box = new MessageBox(getShell(), SWT.ICON_ERROR
						| SWT.OK);
				box.setMessage(e.getMessage()
						+ "\r\nPlease select a valid date for end date.");
				box.setText("Date Setting Error");
				box.open();
				return;
			}
		}

		start = new DateTime();
		end = new DateTime();
		Calendar cal = Calendar.getInstance();

		if (endDate != null) {
			cal.setTime(endDate);
		} else {
			cal.setTime(startDate);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
		}

		end.year = (short) cal.get(Calendar.YEAR);
		end.month = (byte) (cal.get(Calendar.MONTH) + 1);
		end.day = (byte) cal.get(Calendar.DAY_OF_MONTH);
		end.hour = (byte) cal.get(Calendar.HOUR_OF_DAY);
		end.minute = (byte) cal.get(Calendar.MINUTE);
		end.bValid = true;

		cal.setTime(startDate);
		start.year = (short) cal.get(Calendar.YEAR);
		start.month = (byte) (cal.get(Calendar.MONTH) + 1);
		start.day = (byte) cal.get(Calendar.DAY_OF_MONTH);
		start.hour = (byte) (hour.getSelection() & 0x00ff);
		start.minute = (byte) minute.getSelection();
		start.bValid = true;

		int station=0;
		for (int i=0;i<list.length;i++)
			if (list[i].getSelection()) station++;

		if (station == 0) {
			MessageBox box = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
			box.setText("No measure point selected");
			box.setMessage("Please select a measure point to draw.");
			box.open();
			return;
		}
		graph.color = new Color[station];
		int n=0;
		butStart.setEnabled(false);
		butPrint.setEnabled(false);
		for (int i = 0; i < list.length; i++) {
			if (list[i].getText().equals(ConstData.strUnknown))
				continue;
			
			//System.out.println(n);
			while (!meter[i].isPaused()) {
				System.out.println("waiting");
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// 
					//e.printStackTrace();
				}
			};
			TrendDrawTask task = new TrendDrawTask(n,station,this,
						meter[i].ComPort, (byte) meter[i].nMachineNum, start,
						end);
			task.meter = meter[i];
			graph.color[n] = list[i].getBackground();
			graph.logScale = butLog.getSelection();
			//SetGraphScale();
			//graph.setAutoTransform();
			task.start();
			n++;
		}
	}
}
