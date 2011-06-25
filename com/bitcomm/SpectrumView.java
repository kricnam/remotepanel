package com.bitcomm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
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

public class SpectrumView extends Composite implements Listener {
	final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	Composite optionBar;
	ChartGraph3D graph3d;
	Display display;
	Label labelFrom;

	Text textFrom;

	Button calFrom;

	Label labelTo;

	Text textTo;
	Label labelROI;

	Button calTo;

	Spinner hour;

	Spinner minute;

	Combo list;

	Button butDate;

	Button butPeriod;

	MeterView[] meter;
	Button butLog;
	Button butNext;
	Button butPrev;
	private Spinner scaleMin;
	private Spinner scaleMax;
	Button butLeft;
	Button butRight;
	Button butSwitch;

	public SpectrumView(Composite parent, int style) {
		super(parent, style);
		display=parent.getDisplay();
		initialize();
	}

	private void initialize() {
		setLayout(new GridLayout());
		
		initOptionBar();
		initGraph();
		graph3d.setData(this);
		getShell().addListener(SWT.Close, new Listener() {
			public void handleEvent(Event arg0) {
				SaveSetting();
				AlokaPanel.spectrum=null;
			}
		});
		
		GetSetting();
		graph3d.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent arg0) {

				
			}
			public void keyPressed(KeyEvent arg0) {
				if (arg0.keyCode == SWT.ARROW_LEFT)
				{
					graph3d.ChangeCur(-1);
				}
				if (arg0.keyCode == SWT.ARROW_RIGHT)
				{
					graph3d.ChangeCur(1);
				}
				if (arg0.keyCode == SWT.ARROW_UP)
				{
					ChangePos(1);
				}
				if (arg0.keyCode == SWT.ARROW_DOWN)
				{
					ChangePos(-1);
				}
				labelROI.setText(String.valueOf(graph3d.nROI));
				labelROI.getParent().layout();
			}
		});
		graph3d.lblROI = labelROI;
	}
	
	void ChangePos(int n)
	{
		if (graph3d.Data==null) return;
		graph3d.pos+=n;
		if (graph3d.pos < 0)
			graph3d.pos = graph3d.Data.length-1;
		if (graph3d.pos > (graph3d.Data.length-1))
			graph3d.pos = 0;
		labelROI.setText(String.valueOf(graph3d.nROI));
		labelROI.getParent().layout();
		graph3d.UpdateSelection();
		graph3d.setFocus();
	}
	
	void SaveSetting()
	{
		AlokaPanel.SaveSetting("SpectrumSelect", list.getSelectionIndex());
		AlokaPanel.SaveSetting("SpectrumFrom", textFrom.getText());
		AlokaPanel.SaveSetting("SpectrumTo", textTo.getText());
		AlokaPanel.SaveSetting("SpectrumHour", hour.getSelection());
		AlokaPanel.SaveSetting("SpectrumMinute", minute.getSelection());
		AlokaPanel.SaveSetting("SpectrumPeriod", butPeriod.getSelection());
		AlokaPanel.SaveSetting("SpectrumLog", butLog.getSelection());
		AlokaPanel.SaveSetting("SpectrumYScaleMin", scaleMin.getSelection());
		AlokaPanel.SaveSetting("SpectrumYScaleMax", scaleMax.getSelection());
		AlokaPanel.SaveSetting("SpectrumCurSelect", butLeft.getSelection());
	}
	
	void GetSetting()
	{
		list.select(AlokaPanel.GetSettingInt("SpectrumSelect"));
		textFrom.setText(AlokaPanel.GetSettingString("SpectrumFrom"));
		textTo.setText(AlokaPanel.GetSettingString("SpectrumTo")); 
		hour.setSelection(AlokaPanel.GetSettingInt("SpectrumHour"));
		minute.setSelection(AlokaPanel.GetSettingInt("SpectrumMinute"));
		butPeriod.setSelection(AlokaPanel.GetSettingBool("SpectrumPeriod"));
		if (butPeriod.getSelection())
		{
			calTo.setEnabled(true);
			textTo.setEnabled(true);
			labelTo.setEnabled(true);
		}
		butDate.setSelection(!butPeriod.getSelection());
		butLog.setSelection(AlokaPanel.GetSettingBool("SpectrumLog"));
		graph3d.logScale=butLog.getSelection();
		if(AlokaPanel.GetSettingInt("SpectrumYScaleMin")==0)
			scaleMin.setSelection(100);
		else
			scaleMin.setSelection(AlokaPanel.GetSettingInt("SpectrumYScaleMin"));
		scaleMax.setSelection(AlokaPanel.GetSettingInt("SpectrumYScaleMax"));
		butLeft.setSelection(AlokaPanel.GetSettingBool("SpectrumCurSelect"));
		butRight.setSelection(!AlokaPanel.GetSettingBool("SpectrumCurSelect"));
		graph3d.HCursor1Active = butLeft.getSelection();
	}

	private void initOptionBar(){
		optionBar = new Composite(this, SWT.BORDER);
		GridLayout optionLayout = new GridLayout(3, true);
		optionBar.setLayout(optionLayout);
		optionBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Composite set = new Composite(optionBar, SWT.BORDER);
		set.setLayout(new GridLayout(4,true));
		set.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,2,2));
		butDate = new Button(set, SWT.RADIO);
		butDate.setText(ConstData.strStartDate);
		butDate
				.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, true,
						false));
		butDate.setSelection(true);

		
		Group grpTime = new Group(set, SWT.NONE);
		grpTime.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false,1,2));
		grpTime.setText(ConstData.strStartTime);
		grpTime.setLayout(new GridLayout(5, false));
		
		Label lblHour = new Label(grpTime,SWT.NONE);
		lblHour.setText("Hour");
		hour = new Spinner(grpTime, SWT.NONE);
		hour.setMaximum(23);
		hour.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		Label comma = new Label(grpTime, SWT.NONE);
		comma.setText(":");
		Label lblMinute = new Label(grpTime,SWT.NONE);
		lblMinute.setText("Minute");
		minute = new Spinner(grpTime, SWT.NONE);
		minute.setMaximum(59);
		minute.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		butPeriod = new Button(set, SWT.RADIO);
		butPeriod.setText(ConstData.strEndDate);
		butPeriod.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, true,
				false));

		Button butStart = new Button(set, SWT.PUSH);
		butStart.setText("Draw");
		butStart.setLayoutData(new GridData(SWT.FILL,SWT.FILL,  false, false,1,2));

		Group grpStation = new Group(optionBar, SWT.NONE);
		grpStation.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 2));
		grpStation.setLayout(new GridLayout(1,true));
		grpStation.setText(ConstData.strStation);
		list = new Combo(grpStation, SWT.SINGLE | SWT.DROP_DOWN | SWT.READ_ONLY);
		list.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		Composite inputDate = new Composite(set, SWT.NONE);
		inputDate.setLayout(new GridLayout(3, false));
		inputDate.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false,
				false));

		labelFrom = new Label(inputDate, SWT.NONE);
		labelFrom.setText("From");
		labelFrom.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		textFrom = new Text(inputDate, SWT.BORDER);
		GridData dateGrid = new GridData(SWT.END, SWT.CENTER, false, false);
		dateGrid.widthHint = 100;
		textFrom.setLayoutData(dateGrid);
		calFrom = new Button(inputDate, SWT.DOWN | SWT.ARROW);
		GridData dateGrid1 = new GridData(SWT.END, SWT.CENTER, false, false);
		calFrom.setLayoutData(dateGrid1);
		calFrom.setData(textFrom);

		Composite inputEnd = new Composite(set, SWT.NONE);
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


		Group scale = new Group(optionBar,SWT.NONE);
		scale.setLayout(new GridLayout(6,false));
		scale.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		scale.setText("Y scale");
		butLog = new Button(scale, SWT.CHECK);
		butLog.setText("LOG SCALE");
		butLog.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				1, 1));
		Label sMin = new Label(scale, SWT.NONE);
		sMin.setText("Min");
		scaleMin = new Spinner(scale, SWT.NONE);
		scaleMin.setMaximum(999999999);
		scaleMin.setPageIncrement(1000);
		scaleMin.setDigits(2);
		Label sMax = new Label(scale, SWT.NONE);
		sMax.setText("Max");
		scaleMax = new Spinner(scale, SWT.NONE);
		scaleMax.setMaximum(999999999);
		scaleMax.setPageIncrement(1000);
		Button butApply = new Button(scale, SWT.PUSH);
		butApply.setText("Apply");
		butApply.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent arg0) {
				SetGraphScale();
			}
			
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		
				
		//command buttons
		Composite com = new Composite(optionBar, SWT.NONE);
		com.setLayout(new GridLayout(7,false));
		com.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true,2,1));

		Button butLoad = new Button(com, SWT.PUSH);
		butLoad.setText("View File");
		butLoad.setToolTipText("Load multi arbitrary data file");
		butLoad.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		//
		butSwitch = new Button(com, SWT.PUSH);
		butSwitch.setText("Switch");
		butSwitch.setToolTipText("2D-3D graph switch");
		butSwitch.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		butSwitch.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				graph3d.bSwitch=!graph3d.bSwitch;
				graph3d.setAutoTransform(false);
				graph3d.redraw();
			}
			public void widgetDefaultSelected(SelectionEvent arg0) {
				
			}
		});
		Button butZoom = new Button(com, SWT.PUSH);
		butZoom.setText("Zoom");
		butZoom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		butZoom.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				Point size = graph3d.getSize();
				if (size.x < 2000) size.x*=2;
				else size.x = getSize().x;
				graph3d.setSize(size);
				//graph3d.setAutoTransform();
				graph3d.redraw();
			}
			
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		
		Button butPrint = new Button(com, SWT.PUSH);
		butPrint.setText(ConstData.strPrint);
		butPrint.setToolTipText("print current graph");
		butPrint.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		butPrint.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				PrintGraph();
			}
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		
		
		Composite grpNav = new Composite(com, SWT.BORDER);
		Label lblNav = new Label(grpNav,SWT.NONE);
		butNext =new Button(grpNav, SWT.ARROW|SWT.UP);
		butPrev =new Button(grpNav, SWT.ARROW|SWT.DOWN);
	
		lblNav.setText("Roll:");
		grpNav.setLayout(new GridLayout(3, false));
		grpNav.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,1,1));
		butNext.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		butPrev.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

		Composite cursor = new Composite(com, SWT.BORDER);
		cursor.setLayout(new GridLayout(3,false));
		cursor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,1,1));
		Label cur = new Label(cursor, SWT.NONE);
		cur.setText("Cursor Bar Select:");
		butLeft = new Button(cursor,SWT.RADIO);
		butRight = new Button(cursor,SWT.RADIO);
		butLeft.setText("Left");
		butRight.setText("Right");
		
		Composite roi = new Composite(com, SWT.BORDER);
		roi.setLayout(new GridLayout(2,false));
		roi.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,1,1));
		Label lblROIText = new Label(roi, SWT.NONE);
		lblROIText.setText("ROI:");
		labelROI = new Label(roi,SWT.NONE);
		labelROI.setText("0");
		
		
		
		butLeft.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent arg0) {
				graph3d.HCursor1Active = true;
				labelROI.setText(String.valueOf(graph3d.nROI));
				labelROI.getParent().layout();
				graph3d.setFocus();
			}
			
			
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		butRight.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				graph3d.HCursor1Active = false;
				labelROI.setText(String.valueOf(graph3d.nROI));
				labelROI.getParent().layout();
				graph3d.setFocus();
			}
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		
		
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
		list.setItems(str);
		
		butDate.addListener(SWT.Selection, this);
		butPeriod.addListener(SWT.Selection, this);

		calFrom.addListener(SWT.Selection, this);
		calTo.addListener(SWT.Selection, this);
		
		butStart.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				graph3d.bSwitch=false;
				graph3d.bMutilty=false;
				process();
			}
		});

		butLoad.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				OnLoad();	
			}
			public void widgetDefaultSelected(SelectionEvent arg0) {
				
			}
		});

		butLog.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				graph3d.logScale = butLog.getSelection();
				graph3d.redraw();
			}
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		
		butNext.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				ChangePos(1);
			}

		});
		butPrev.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				ChangePos(-1);
			}

		});
		
	}
	@Override
	public void update() {
		labelROI.setText(String.valueOf(graph3d.nROI));
		System.out.println("update");
		super.update();
	}

	protected void OnLoad() {
		FileDialog dlg = new FileDialog(getShell(),SWT.MULTI);
		dlg.setFilterExtensions(new String[]{"*.spc"});
		dlg.setFilterNames(new String[]{"spectrum data"});
		dlg.setFilterPath("./spectrum");
		String files = dlg.open();
		if (files!=null)
		{
			String file[]=dlg.getFileNames();
			String path = dlg.getFilterPath();
			if (file.length>6)
			{
				AlokaPanel.MessageBox("Warning", "More than 6 files selected, but only the first 6 file data will be displayed.");
			}
			int num = Math.min(6, file.length);
			if (num<1) return;
			butSwitch.setEnabled((num > 1));
			
			graph3d.Data = new double[num][1000];
			graph3d.Index = new String[num];
			graph3d.pos=0;
			for(int i=0;i< num;i++)
			{
				try {
					SpectrumFile sf = new SpectrumFile(path+File.separator+file[i]);
					graph3d.Index[i]= file[i].substring(0,file[i].length()-4)+ " " + sf.data.dateEnd.toStringDate() + " " 
						+ sf.data.dateEnd.toStringTime();
					for (int n=0;n<1000;n++)
						graph3d.Data[i][n]=sf.data.Channel[n];
					
				} catch (IOException e) {
					AlokaPanel.MessageBox("Error", e.getMessage());
					e.printStackTrace();
					break;
				} catch (ParseException e) {
					AlokaPanel.MessageBox("Error", e.getMessage());
					e.printStackTrace();
					break;
				}
			}
			graph3d.bMutilty = true;
			graph3d.bSwitch = true;
			graph3d.setSize(graph3d.getParent().getParent().getClientArea().width,
					graph3d.getParent().getParent().getClientArea().height);
			graph3d.SetROI();
			
			graph3d.redraw();
			
			labelROI.setText(String.valueOf(graph3d.nROI));
			labelROI.getParent().layout();
			graph3d.setFocus();
		}
	}

	protected void SetGraphScale() {
		if (graph3d==null) return;
		if (scaleMin.getSelection()/100 >= scaleMax.getSelection())
		{
			AlokaPanel.MessageBox("Error", "Min value shall not greater or equal to Max value!");
			return;
		}
		graph3d.nScaleMax = scaleMax.getSelection();
		graph3d.nScaleMin = scaleMin.getSelection()/100.0;
		graph3d.nMaxData = graph3d.nScaleMax; 
		graph3d.redraw(); 
	}

	void PrintGraph()
	{
		PrintDialog dlg = new PrintDialog(getShell(),SWT.NONE);
		PrinterData printData = dlg.open();
		if (printData==null) return;
		//System.out.println("printing");
		Printer printer= new Printer(printData);
		if (printer.startJob("SpetrumGraph"))
		{
			GC gc = new GC(printer);
			int nOldMargin = graph3d.Margin;
			Rectangle trim = printer.getBounds();
			Point dpi = printer.getDPI();
			//System.out.println(trim.toString());
			if (printer.startPage()) {
				graph3d.AutoSetTransform(gc,dpi.x, dpi.y, trim.width-2*dpi.x, trim.height-2*dpi.y);
				graph3d.drawBackground(gc, dpi.x, dpi.y, trim.width-2*dpi.x, trim.height-2*dpi.y);
				graph3d.drawData(gc, dpi.x, dpi.y, trim.width-2*dpi.x, trim.height-2*dpi.y);
				printer.endPage();
				graph3d.setAutoTransform(false);
			}
			gc.dispose();
			printer.endJob();
			graph3d.Margin = nOldMargin;
		}
		graph3d.redraw();
	}

	private void initGraph(){
		Composite com = new Composite(this,SWT.BORDER);
		com.setLayout(new FillLayout());
		com.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		ScrolledComposite sc=new ScrolledComposite(com, SWT.H_SCROLL);
		graph3d = new ChartGraph3D(sc,SWT.NONE);
		graph3d.setSize(getShell().getClientArea().width,
				getShell().getClientArea().height);
		sc.setContent(graph3d);
	
		graph3d.nXMarkNum = 10;
		graph3d.nYMarkNum = 10;
		graph3d.setAutoTransform(true);
		graph3d.logScale = true;
		graph3d.bMutilty=false;
		graph3d.bSwitch = false;
	

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

	void process()  {
		DateTime start;
		DateTime end;
		Date startDate = null;
		Date endDate = null;
		try {
			startDate = formatter.parse(textFrom.getText());
		} catch (ParseException e) {
			MessageBox box = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
			box.setMessage(e.getMessage()
					+ "\r\nPlease select a valid date for start date.");
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
			if (!endDate.after(startDate))
			{
				AlokaPanel.MessageBox("Error", 
				"End date shall not before or the same as the start date.\nPlease chose a right end date.");
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
			//cal.set(Calendar.HOUR_OF_DAY, 23);
			//cal.set(Calendar.MINUTE, 59);
		}
		
		end.setTime(cal);
		cal.setTime(startDate);
		cal.set(Calendar.HOUR_OF_DAY, hour.getSelection());
		cal.set(Calendar.MINUTE, minute.getSelection());
		
		start.setTime(cal);
		
		int station = list.getSelectionIndex();

		if (station <  0) {
			MessageBox box = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
			box.setText("No measure point selected");
			box.setMessage("Please select a measure point to draw.");
			box.open();
			return;
		}
		
		if (list.getItem(station).equals(ConstData.strUnknown))
				return;
		int n = meter[station].nMachineNum;
	
		String strFileName;
		int PT=0;
		try
		{
			PT = SpectrumFile.getPT(n,start);
			strFileName = SpectrumFile.getFileName(n, start,PT);
		} 
		catch (IOException e) 
		{
				MessageBox box = new MessageBox(getShell(), 
						SWT.ICON_ERROR | SWT.OK);
				box.setText("Read File Fail");
				box.setMessage("Please goto backup to download data first.");
				box.open();
				return;
		}
			
		if (butDate.getSelection())
		{
			SpectrumFile file;
			try {
				file = new SpectrumFile(strFileName);
				graph3d.Data = null;
				graph3d.Data = new double[1][1000];
				graph3d.Index = new String[1];
				graph3d.pos =0;
				for(int i=0;i<1000;i++)
					graph3d.Data[0][i]=file.data.Channel[i];
				graph3d.Index[0]=file.getName().substring(0, file.getName().length()-4)
				+" "+file.data.dateEnd.toStringDate()						
			+ " "+file.data.dateEnd.toStringTime();
			}
			catch (FileNotFoundException ef)
			{
				AlokaPanel.MessageBox("Error", ef.getMessage()+ "\n"
						+"Please download data first.");
				
			}
			catch (IOException e) 
			{
				AlokaPanel.MessageBox("Error", e.getMessage());
				//e.printStackTrace();
			} 
			catch (ParseException e) 
			{
				e.printStackTrace();
			}
		}
		else
		{
			cal = start.getTime();
			DateTime date = new DateTime();
			date.setTime(cal);
			//System.out.println(cal.toString());
			//System.out.println(end.getTime().toString());
			long nfile = end.getTime().getTimeInMillis()-cal.getTimeInMillis();
			
			nfile = nfile/60000 /PT;
			int nlim=graph3d.getParent().getParent().getClientArea().height/4 - 2;
			if (nlim < nfile)
			{
				AlokaPanel.MessageBox("Info", "Selected data is more than expected to display, only the first "+
						String.valueOf(nlim)+" will be displayed.");
				nfile = nlim;
			}
				
			graph3d.Data = null;
			graph3d.Data = new double[(int)nfile][1000];
			graph3d.Index = new String[(int)nfile];
			System.out.println("Total files:"+String.valueOf(nfile));
			boolean bIgnor = false;
			for (int t=0;t<nfile;t++)
			{
				try {
					strFileName = SpectrumFile.getFileName(n, date,PT);
					//System.out.println(strFileName);
					SpectrumFile file = null;
					try {
						
						file = new SpectrumFile(strFileName);
						for(int i=0;i<1000;i++)
							graph3d.Data[t][i]=file.data.Channel[i];
						graph3d.Index[t]=file.getName().substring(0, file.getName().length()-4)
						+" "+file.data.dateEnd.toStringDate()
						+" "+file.data.dateEnd.toStringTime();
						//System.out.println(graph3d.Index[t]);
					} 
					catch (ParseException e) 
					{
						e.printStackTrace();
					}
					catch (FileNotFoundException ee)
					{
						if  (!bIgnor)
						{
							MessageBox box = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.YES|SWT.NO);
							box.setText("Read File Fail");
							box.setMessage(ee.getMessage()+"\nPlease goto backup to download data first.\nContinue to read next?");
							int choice =box.open();
							if (choice == SWT.YES) 
							{
								bIgnor = true;
							}
							if (choice != SWT.YES) break;
						}
					}
					
					date.addMinute(PT);
					if (end.getTime().before(date.getTime()))
						break;
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					MessageBox box = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
					box.setText("Read File Fail");
					box.setMessage(e.getMessage());
					box.open();
					e.printStackTrace();
					return;
				}
			};
		}
		graph3d.setSize(graph3d.getParent().getParent().getClientArea().width,
				graph3d.getParent().getParent().getClientArea().height);
		graph3d.setAutoTransform(true);
		graph3d.bMutilty=false;
		graph3d.bSwitch = false;

		getShell().setCursor(getDisplay().getSystemCursor(SWT.CURSOR_WAIT));
		graph3d.redraw();
		graph3d.setFocus();
		getShell().setCursor(null);

	}

}
