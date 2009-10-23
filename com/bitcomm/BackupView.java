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
import org.eclipse.swt.graphics.Image;
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
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.vafada.swtcalendar.SWTCalendarEvent;
import org.vafada.swtcalendar.SWTCalendarListener;

public class BackupView extends Composite implements Listener {
	final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	Composite optionBar;

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

	Text console;

	ProgressBar prograss;

	Button chkData;

	Button chkSpec;

	Button butStart;

	Label lblMsg;

	Button butCancel;
	boolean bCancel;
	public BackupView(Composite parent, int style, MeterView[] meter) {
		super(parent, style);
		initialize();
		this.meter = meter;
		bCancel = false;
	}

	private void initialize() {

		display = getDisplay();
		setLayout(new GridLayout());
		initOptionBar();
		prograss = new ProgressBar(this, SWT.NONE);
		prograss.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		prograss.setMinimum(0);
		prograss.setMaximum(100);
		console = new Text(this, SWT.READ_ONLY | SWT.BORDER | SWT.MULTI
				| SWT.WRAP | SWT.V_SCROLL);
		console.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		getShell().addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
				//System.out.println("close");
				if (prograss.getSelection()>0) 
				{
					MessageBox box = new MessageBox(getShell(), SWT.ICON_WARNING
							| SWT.YES |SWT.NO);
					box.setMessage("Do you really want to quit backup process?");
					box.setText("QUIT");

					if (box.open() == SWT.YES)
					{
						event.doit = true;
					}
					else
					{
						event.doit = false;
						return;
					}
				}
				SaveSetting();
				AlokaPanel.backup = null;

				return;
			}
		});

		GetSetting();
	}
	void SaveSetting()
	{
		AlokaPanel.SaveSetting("BackupSelect", list.getSelectionIndex());
		AlokaPanel.SaveSetting("BackupFrom", textFrom.getText());
		AlokaPanel.SaveSetting("BackupTo", textTo.getText());
		AlokaPanel.SaveSetting("BackupHour", hour.getSelection());
		AlokaPanel.SaveSetting("BackupMinute", minute.getSelection());
		AlokaPanel.SaveSetting("BackupPeriod", butPeriod.getSelection());
		AlokaPanel.SaveSetting("BackupType",chkData.getSelection());
	}
	
	void GetSetting()
	{
		list.setSelection(AlokaPanel.GetSettingInt("BackupSelect"));
		textFrom.setText(AlokaPanel.GetSettingString("BackupFrom"));
		textTo.setText(AlokaPanel.GetSettingString("BackupTo")); 
		hour.setSelection(AlokaPanel.GetSettingInt("BackupHour"));
		minute.setSelection(AlokaPanel.GetSettingInt("BackupMinute"));
		butPeriod.setSelection(AlokaPanel.GetSettingBool("BackupPeriod"));
		if (butPeriod.getSelection())
		{
			calTo.setEnabled(true);
			textTo.setEnabled(true);
			labelTo.setEnabled(true);
		}
		butDate.setSelection(!butPeriod.getSelection());
		chkData.setSelection(AlokaPanel.GetSettingBool("BackupType"));
		chkSpec.setSelection(!chkData.getSelection());
	}

	private void initOptionBar() {
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
		grpStation.setLayout(new RowLayout(SWT.VERTICAL));
		grpStation.setText(ConstData.strStation);
		list = new List(grpStation, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		list.setLayoutData(new RowData(220,90));
		


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
		grpTime.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		grpTime.setText(ConstData.strStartTime);
		grpTime.setLayout(new GridLayout(5, false));
		Label lblHour=new Label(grpTime, SWT.NONE);
		lblHour.setText("Hour");
		hour = new Spinner(grpTime, SWT.NONE);
		hour.setMaximum(23);
		Label comma = new Label(grpTime, SWT.NONE);
		comma.setText(":");
		Label lblmin=new Label(grpTime, SWT.NONE);
		lblmin.setText("Minute");
		minute = new Spinner(grpTime, SWT.NONE);
		minute.setMaximum(59);

		Group grpType = new Group(optionBar, SWT.NONE);
		grpType.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false,
				false, 1, 1));
		grpType.setText(ConstData.strChoseType);
		grpType.setLayout(new GridLayout(3, false));
		chkData = new Button(grpType, SWT.RADIO);
		chkSpec = new Button(grpType, SWT.RADIO);
		chkData.setSelection(true);
		chkData.setText(ConstData.strDoesRate);
		chkSpec.setText(ConstData.strSpetru);
		
		lblMsg = new Label(optionBar,SWT.NONE);
		lblMsg.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
		
		Composite cmd = new Composite(optionBar, SWT.BORDER);
		cmd.setLayout(new GridLayout(2,true));
		cmd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		butStart = new Button(cmd, SWT.PUSH);
		butStart.setText(ConstData.strStart + " " + ConstData.strBackup);
		butStart.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		butCancel = new Button(cmd, SWT.PUSH);
		butCancel.setText(ConstData.strCancel);
		butCancel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		butStart.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				processBackup();
			}

		});
		
		butCancel.addSelectionListener(new SelectionListener() {
			
			
			public void widgetSelected(SelectionEvent arg0) {
				bCancel = true;
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

	void processBackup() {
		DateTime start;
		DateTime end;
		Date startDate = null;
		Date endDate = null;
		bCancel = false;
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

		int[] station = list.getSelectionIndices();

		if (station.length == 0) {
			MessageBox box = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
			box.setText("No measure point selected");
			box.setMessage("Please select a measure point to backup.");
			box.open();
			return;
		}
		int DataType ;
		if (chkData.getSelection()) DataType = CommunicationHistoryData.DoseRate;
		else
			DataType = CommunicationHistoryData.Spectrum;
		
		for (int i = 0; i < station.length; i++) {
			if (list.getItem(station[i]).equals(ConstData.strUnknown))
				continue;
			int n = station[i];
			
			if (!meter[n].isConnected() )
			{
				AlokaPanel.MessageBox("Warning", "network not ready, please try later");
				continue;
			}

			meter[n].Pause(true);
			int ii=0;
			while (!meter[n].isPaused())
			{
				   String strMsg = "Communication port busy, waiting";
				   for(int m=0;m < ii%3+1;m++)
				   {
					   strMsg+=".";
				   }
				   lblMsg.setText(strMsg);
				    try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					ii++;
			};
			BackupDataTask bk = new BackupDataTask(DataType,this,
						meter[n].ComPort, (byte) meter[n].nMachineNum, start,
						end);
			bk.meter = meter[n];
			prograss.setSelection(1);
			bk.start();

		}

	}
	
}
