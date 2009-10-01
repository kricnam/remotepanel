package com.bitcomm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
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

	Button calTo;

	Spinner hour;

	Spinner minute;

	List list;

	Button butDate;

	Button butPeriod;

	MeterView[] meter;
	Button butLog;
	Button butNext;
	Button butPrev;

	public SpectrumView(Composite parent, int style) {
		super(parent, style);
		display=parent.getDisplay();
		initialize();
	}

	private void initialize() {
		//setSize(new Point(300, 200));
		setLayout(new GridLayout());
		//setLayout(layout);
		//setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
		
		initOptionBar();
		initGraph();

	}
	private void initOptionBar(){
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

		Group grpStation = new Group(optionBar, SWT.BORDER);
		grpStation.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 4));
		grpStation.setLayout(new RowLayout(SWT.VERTICAL));
		grpStation.setText(ConstData.strStation);
		list = new List(grpStation, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
		list.setLayoutData(new RowData(220,120));
		


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
		grpTime.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		grpTime.setText(ConstData.strStartTime);
		grpTime.setLayout(new GridLayout(3, false));
		hour = new Spinner(grpTime, SWT.NONE);
		hour.setMaximum(23);
		Label comma = new Label(grpTime, SWT.NONE);
		comma.setText(":");
		minute = new Spinner(grpTime, SWT.NONE);
		minute.setMaximum(59);
		

		Button butStart = new Button(optionBar, SWT.PUSH);
		butStart.setText(ConstData.strStart);
		butStart.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));

		butStart.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				process();
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
		
		butLog = new Button(optionBar, SWT.CHECK);
		butLog.setText("LOG");
		butLog.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		
		Group grpNav = new Group(optionBar, SWT.NONE);
		butNext =new Button(grpNav, SWT.ARROW|SWT.UP);
		butPrev =new Button(grpNav, SWT.ARROW|SWT.DOWN);
		grpNav.setLayout(new GridLayout(2, true));
		grpNav.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		butNext.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				graph3d.pos+=1;
				if (graph3d.pos > (graph3d.Data.length-1))
					graph3d.pos = 0;
				graph3d.UpdateSelection();
			}

		});
		butPrev.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				graph3d.pos-=1;
				if (graph3d.pos < 0)
					graph3d.pos = graph3d.Data.length-1;
				graph3d.UpdateSelection();
			}

		});



		butDate.addListener(SWT.Selection, this);
		butPeriod.addListener(SWT.Selection, this);

		calFrom.addListener(SWT.Selection, this);
		calTo.addListener(SWT.Selection, this);

	}
	private void initGraph(){
		Composite com = new Composite(this,SWT.BORDER);
		com.setLayout(new FillLayout());
		com.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		graph3d = new ChartGraph3D(com,SWT.NONE);
		graph3d.Data = new double[10][11];
		
		graph3d.Margin = 63;
		graph3d.nMaxData = 2;
		graph3d.nMinData = -2;
		graph3d.nXMarkNum = 10;
		graph3d.nYMarkNum = 10;
		graph3d.nDepthStep = 2;
		//graph3d.setBackground(graph3d.getDisplay().getSystemColor(SWT.COLOR_WHITE));
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
				strFileName = SpectrumFile.getFileName(n, start);
				
				PT = SpectrumFile.getPT(strFileName);
			} catch (IOException e) {
				MessageBox box = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
				box.setText("Read File Fail");
				box.setMessage("Please goto backup to download date first.");
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
				for(int i=0;i<1000;i++)
					graph3d.Data[0][i]=file.data.Channel[i];
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			graph3d.setAutoTransform();
			graph3d.redraw();
			
		}
		else
		{
			cal = start.getTime();
			DateTime date = new DateTime();
			date.setTime(cal);
			//System.out.println(cal.toString());
			//System.out.println(end.getTime().toString());
			long nfile = end.getTime().getTimeInMillis()-cal.getTimeInMillis();
			
			nfile = nfile/60000 /PT ;
			graph3d.Data = null;
			graph3d.Data = new double[(int)nfile][1000];
			//System.out.println("Total files:"+String.valueOf(nfile));
			for (int t=0;t<nfile;t++)
			{
				try {
					strFileName = SpectrumFile.getFileName(n, date);
					//System.out.println(strFileName);
					SpectrumFile file;
					try {
						
						file = new SpectrumFile(strFileName);
						for(int i=0;i<1000;i++)
							graph3d.Data[t][i]=file.data.Channel[i];
						date.addMinute(PT);
						if (end.getTime().before(date.getTime()))
							break;
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
				catch (FileNotFoundException ee)
				{
					MessageBox box = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
					box.setText("Read File Fail");
					box.setMessage(strFileName +" not found.Please goto backup to download date first."+ee.getMessage());
					box.open();
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
			
			graph3d.setAutoTransform();
			graph3d.redraw();
			
		}
	}

}
