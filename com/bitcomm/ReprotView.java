package com.bitcomm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.vafada.swtcalendar.SWTCalendarEvent;
import org.vafada.swtcalendar.SWTCalendarListener;

public class ReprotView extends Composite implements Listener {
	CTabFolder folder;

	Image img;

	Composite comDaily;

	Composite comMonth;

	final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	final SimpleDateFormat monformatter = new SimpleDateFormat("yyyy-MM");

	Text textDate;
	Text textMonth;

	Button butDate;
	Button butMonth;
	
	Combo cmbDailyList;
	Combo cmbMonthlyList;

	MeterView[] meter;

	Composite dailyTab;

	Composite monthTab;

	Label[][] dailyItem;

	Label[][] MonthlyItem;
	Label stationLabelD;
	Label stationLabelM;

	public ReprotView(MeterView[] meter,Composite parent, int style) {
		super(parent, style);
		this.meter=meter;
		initialize();
	}

	private void initialize() {
		setSize(this.getShell().getSize());

		//		System.out.println(this.getShell().getSize().toString());

		setLayout(new FillLayout());
		img = new Image(this.getDisplay(), "com/bitcomm/resource/tab.png");
		folder = new CTabFolder(this, SWT.BORDER);
		folder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		folder.setSimple(false);
		folder.setUnselectedImageVisible(true);
		folder.setUnselectedCloseVisible(true);
		folder.setSelectionForeground(this.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		folder.setSelectionBackground(this.getDisplay().getSystemColor(
				SWT.COLOR_BLUE));
		folder.setMaximized(true);

		CTabItem itemDaily = new CTabItem(folder, SWT.NONE);
		CTabItem itemMonthly = new CTabItem(folder, SWT.NONE);
		itemDaily.setText(ConstData.strDailyReprot);
		itemDaily.setImage(img);
		createDailyTable();
		itemDaily.setControl(comDaily);
		itemMonthly.setText(ConstData.strMonthReport);
		itemMonthly.setImage(img);
		createMonthlyTable();
		itemMonthly.setControl(comMonth);
		
		getShell().addListener(SWT.Close, new Listener() {
			
			public void handleEvent(Event arg0) {
				SaveSetting();
				AlokaPanel.report=null;
			}
		});
		GetSetting();
	}

	void SaveSetting()
	{
		AlokaPanel.SaveSetting("ReportDailySelect", cmbDailyList.getSelectionIndex());
		AlokaPanel.SaveSetting("ReportDailyDate", textDate.getText());
		AlokaPanel.SaveSetting("ReportMonthlySelect", cmbMonthlyList.getSelectionIndex());
		AlokaPanel.SaveSetting("ReportMonth", textMonth.getText());
	}
	
	void GetSetting()
	{
		cmbDailyList.select(AlokaPanel.GetSettingInt("ReportDailySelect"));
		textDate.setText(AlokaPanel.GetSettingString("ReportDailyDate"));
		cmbMonthlyList.select(AlokaPanel.GetSettingInt("ReportMonthlySelect"));
		textMonth.setText(AlokaPanel.GetSettingString("ReportMonth"));
	}

	private void createDailyTable() {
		comDaily = new Composite(folder, SWT.NONE);
		GridLayout comLayout = new GridLayout();
		comLayout.numColumns = 1;
		comDaily.setLayout(comLayout);

		Composite optionBar = new Composite(comDaily, SWT.NONE);
		RowLayout optionLayout = new RowLayout();
		optionLayout.marginLeft = 10;

		optionLayout.justify = true;
		optionLayout.fill = true;
		optionBar.setLayout(optionLayout);
		
		Label label = new Label(optionBar,SWT.NONE|SWT.CENTER);
		label.setText(ConstData.strStation);
		
		cmbDailyList = new Combo(optionBar,SWT.DROP_DOWN|SWT.READ_ONLY);
		String []items=new String[meter.length];
		for(int i=0;i<meter.length;i++)
		{
			items[i]=meter[i].label.getText();
		}
		cmbDailyList.setItems(items);

		butDate = new Button(optionBar, SWT.PUSH);
		textDate = new Text(optionBar, SWT.BORDER);
		Button butRun = new Button(optionBar, SWT.PUSH);
		butRun.setText(ConstData.strStart);
		butRun.addSelectionListener(new SelectionListener() {
			
			
			public void widgetSelected(SelectionEvent arg0) {
				fillDailyReport();
			}
			
			
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
			}
		});

		Button butPrint = new Button(optionBar, SWT.PUSH);
		butPrint.setText(ConstData.strPrint);

		butPrint.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				return;
			}

			public void widgetSelected(SelectionEvent e) {
				System.out.println("print");
				PrintDailyReport();
			}
			
		});
		Button butSave = new Button(optionBar, SWT.PUSH);
		butSave.setText(ConstData.strSave);
		butSave.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				SaveDailyTable();
			}
			
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		
		butDate.setText(ConstData.strDate);
		butDate.addListener(SWT.Selection, this);

		textDate.setTextLimit(20);
		textDate.setLayoutData(new RowData(100, 16));
		textDate.setData("format", "format");
		butDate.setData(textDate);

		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = SWT.FILL;

		//Make scroll
		Composite sb = new Composite(comDaily, SWT.NONE);
		sb.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		sb.setLayout(new FillLayout());
		ScrolledComposite sc = new ScrolledComposite(sb, SWT.BORDER
				| SWT.V_SCROLL | SWT.H_SCROLL);
		sc.setLayout(new FillLayout());

		dailyTab = new Composite(sc, SWT.NONE);
		sc.setContent(dailyTab);
		GridLayout gridTab = new GridLayout(5, false);
		gridTab.horizontalSpacing = -1;
		gridTab.marginBottom = 0;
		gridTab.verticalSpacing = 0;

		dailyTab.setLayout(gridTab);
		
		dailyTab.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		dailyItem = new Label[25][4];
		String[] tabHead = { ConstData.strMax,
				ConstData.strMin, ConstData.strAvg, ConstData.str3Sigma };
		Label item = new Label(dailyTab, SWT.BORDER | SWT.CENTER);
		item.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		item.setText("Data Type");
		
		{
			stationLabelD = new Label(dailyTab, SWT.BORDER | SWT.CENTER);
			stationLabelD.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
			stationLabelD.setText(" Does Rate(nGy/h)");
		}
		
		item = new Label(dailyTab, SWT.BORDER | SWT.CENTER);
		item.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		item.setText(ConstData.strTime);
		
		for (int n=0;n<4;n++)
		{
			item = new Label(dailyTab, SWT.BORDER | SWT.CENTER);
			item.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			item.setText(tabHead[n]);
		}
		
		for (int i = 0; i < 25; i++)
		{
			item = new Label(dailyTab, SWT.BORDER | SWT.CENTER);
			item.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			if (i<24) item.setText(String.valueOf(i)+":00~"+String.valueOf(i+1)+":00");
			else item.setText("Summary");
			
				for (int m=0;m<4;m++)
				{
					dailyItem[i][m] = new Label(dailyTab, SWT.BORDER | SWT.CENTER);
					dailyItem[i][m].setLayoutData(new GridData(SWT.FILL, SWT.FILL,
						true, true));
				
					dailyItem[i][m].setText("  ----  ");

					dailyItem[i][m].setBackground(getDisplay().getSystemColor(
						SWT.COLOR_WHITE));
				}
			
		}
		dailyTab.pack();
	}
	
	void SaveDailyTable()
	{
		FileDialog dlg = new FileDialog(getShell(),SWT.SAVE);
		dlg.setFilterExtensions(new String[]{"*.csv","*.txt","*.*"});
		dlg.setFilterNames(new String[] {"CSV File(*.csv)","Text File(*.txt","All Files(*.*)"});
		String fileName=dlg.open();
		if (fileName!=null)
		{
			File file = new File(fileName);
			StringBuilder sb = new StringBuilder();
			
			if (file.exists())
			{
				MessageBox box=new MessageBox(getShell(),SWT.YES|SWT.NO|SWT.ICON_WARNING);
				box.setText("File already exist");
				box.setMessage("Overwrite existing file "+ fileName + " ?");
				int choice=box.open();
				if (choice==SWT.NO)
					return;
			}

			try {

				FileWriter fout = new FileWriter(file);

				sb.append(ConstData.strTime+",");

				sb.append(ConstData.strMax+",");

				sb.append(ConstData.strMin+",");

				sb.append(ConstData.strAvg+",");

				sb.append(ConstData.str3Sigma+"\r\n");

				for(int n=0;n<24;n++)
				{
					sb.append(String.format("\"%02d",n)+":00-"+String.format("%02d",n+1)+":00\""+",");

					for (int m=0;m<4;m++)
					{
						sb.append(dailyItem[n][m].getText());
						if (m<3) sb.append(",");
						else sb.append("\r\n");
					}
				}




				sb.append("Summary,");
				for (int m=0;m<4;m++)
				{
					sb.append(dailyItem[24][m].getText());
					if (m<3) sb.append(",");
					else sb.append("\r\n");
				}
				fout.write(sb.toString());
				fout.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
	private void createMonthlyTable() {
		comMonth = new Composite(folder, SWT.NONE);
		GridLayout comLayout = new GridLayout();
		comLayout.numColumns = 1;
		comMonth.setLayout(comLayout);

		Composite optionBar = new Composite(comMonth, SWT.NONE);
		RowLayout optionLayout = new RowLayout();
		optionLayout.marginLeft = 10;

		optionLayout.justify = true;
		optionLayout.fill = true;

		optionBar.setLayout(optionLayout);
		
		Label label = new Label(optionBar,SWT.NONE|SWT.CENTER);
		label.setText(ConstData.strStation);
		cmbMonthlyList = new Combo(optionBar,SWT.DROP_DOWN|SWT.READ_ONLY);
		String []items=new String[meter.length];
		for(int i=0;i<meter.length;i++)
		{
			items[i]=meter[i].label.getText();
		}
		cmbMonthlyList.setItems(items);
		
		butMonth = new Button(optionBar, SWT.NONE);
		textMonth = new Text(optionBar, SWT.BORDER);
		
		
		butMonth.setData(textMonth);
		butMonth.setText(ConstData.strMonth);
		butMonth.addListener(SWT.Selection, this);
		textMonth.setTextLimit(20);
		textMonth.setLayoutData(new RowData(100, 16));

		Button butRun = new Button(optionBar, SWT.NONE);
		butRun.setText(ConstData.strStart);
		butRun.addSelectionListener(new SelectionListener() {
			
			
			public void widgetSelected(SelectionEvent arg0) {
				fillMonthlyReport();
			}
			
			
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		Button butPrint = new Button(optionBar, SWT.NONE);
		butPrint.setText(ConstData.strPrint);
		butPrint.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e) {
				return;
			}

			public void widgetSelected(SelectionEvent e) {
				PrintMonthlyReport();
			}
			
		});
		
		Button butSave = new Button(optionBar, SWT.PUSH);
		butSave.setText(ConstData.strSave);
		butSave.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				SaveMonthlyTable();
			}
			
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		//Make scroll
		Composite sb = new Composite(comMonth, SWT.NONE);
		sb.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		sb.setLayout(new FillLayout());
		ScrolledComposite sc = new ScrolledComposite(sb, SWT.BORDER
				| SWT.V_SCROLL | SWT.H_SCROLL);
		FillLayout fill = new FillLayout();
		fill.type=SWT.VERTICAL;
		sc.setLayout(fill);

		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = SWT.FILL;

		monthTab = new Composite(sc, SWT.BORDER);
		monthTab.setLayoutData(gridData);
		sc.setContent(monthTab);
		
		GridLayout gridTab = new GridLayout(5, false);
		gridTab.horizontalSpacing = 1;
		gridTab.verticalSpacing = 0;

		monthTab.setLayout(gridTab);
		monthTab.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		MonthlyItem = new Label[32][4];
		String[] tabHead = { ConstData.strMax,ConstData.strMin, ConstData.strAvg, ConstData.str3Sigma };
		
		Label item = new Label(monthTab, SWT.BORDER | SWT.CENTER);
		item.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		item.setText("Data Type");
		{
			stationLabelM = new Label(monthTab, SWT.BORDER | SWT.CENTER);
			stationLabelM.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
			stationLabelM.setText(" Does Rate(nGy/h)");
		}
		
		item = new Label(monthTab, SWT.BORDER | SWT.CENTER);
		item.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		item.setText(ConstData.strDate);
		
		for (int n=0;n<4;n++)
		{
			item = new Label(monthTab, SWT.BORDER | SWT.CENTER);
			item.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			item.setText(tabHead[n]);
		}
		
		for (int i = 0; i < 32; i++)
		{
			item = new Label(monthTab, SWT.BORDER | SWT.CENTER);
			item.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			if (i< 31) item.setText(String.valueOf(i+1));
			else item.setText("Summary");
			
			
				for (int m=0;m<4;m++)
				{
					MonthlyItem[i][m] = new Label(monthTab, SWT.BORDER | SWT.CENTER);
					MonthlyItem[i][m].setLayoutData(new GridData(SWT.FILL, SWT.FILL,
						true, true));
				
					MonthlyItem[i][m].setText("  ----  ");

					MonthlyItem[i][m].setBackground(getDisplay().getSystemColor(
						SWT.COLOR_WHITE));
				}
			
		}
		monthTab.pack();

	}

	void SaveMonthlyTable()
	{
		FileDialog dlg = new FileDialog(getShell(),SWT.SAVE);
		dlg.setFilterExtensions(new String[]{"*.csv","*.txt","*.*"});
		dlg.setFilterNames(new String[] {"CSV File(*.csv)","Text File(*.txt","All Files(*.*)"});
		String fileName=dlg.open();
		if (fileName!=null)
		{
			File file = new File(fileName);
			StringBuilder sb = new StringBuilder();
			
			if (file.exists())
			{
				MessageBox box=new MessageBox(getShell(),SWT.YES|SWT.NO|SWT.ICON_WARNING);
				box.setText("File already exist");
				box.setMessage("Overwrite existing file "+ fileName + " ?");
				int choice=box.open();
				if (choice==SWT.NO)
					return;
			}

			try {

				FileWriter fout = new FileWriter(file);

				sb.append(ConstData.strTime+",");

				sb.append(ConstData.strMax+",");

				sb.append(ConstData.strMin+",");

				sb.append(ConstData.strAvg+",");

				sb.append(ConstData.str3Sigma+"\r\n");

				Date date=null;
				Calendar cal=null;
				try {
					date = monformatter.parse(textMonth.getText());
					cal = Calendar.getInstance();
					cal.setTime(date);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int MonthMax;
				if (cal!=null)
					MonthMax = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				else
					MonthMax = 31;
				for(int n=0;n<MonthMax;n++)
				{
					sb.append(String.format("%d",n+1)+",");

					for (int m=0;m<4;m++)
					{
						sb.append(MonthlyItem[n][m].getText());
						if (m<3) sb.append(",");
						else sb.append("\r\n");
					}
				}

				sb.append("Summary,");
				for (int m=0;m<4;m++)
				{
					sb.append(MonthlyItem[31][m].getText());
					if (m<3) sb.append(",");
					else sb.append("\r\n");
				}
				fout.write(sb.toString());
				fout.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
	void resetDailyTab()
	{
		for (int i = 0; i < 25; i++)
		{	
				for (int m=0;m<4;m++)
				{
					dailyItem[i][m].setText("  ----  ");
				}
			
		}

	}
	
	void resetMonthlyTab()
	{
		for (int i = 0; i < 32; i++)
		{
				for (int m=0;m<4;m++)
				{
					MonthlyItem[i][m].setText("  ----  ");
				}
			
		}
	}
	public void handleEvent(Event event) {
		final Text text = (Text)event.widget.getData();
		
		if (text == null)
			return;

		Image img = new Image(getDisplay(), "com/bitcomm/resource/calendar.png");
		SWTCalendarDialog cal = new SWTCalendarDialog(getShell(), img);
		cal.addDateChangedListener(new SWTCalendarListener() {
			public void dateChanged(SWTCalendarEvent calendarEvent) {
				if (text.getData("format")!=null)
				{
					text.setText(formatter.format(calendarEvent.getCalendar()
						.getTime()));
				}
				else
					text.setText(monformatter.format(calendarEvent.getCalendar()
							.getTime()));
					
			}
		});
		if (text.getText() != null && text.getText().length() > 0) {
			try {
				Date d;
				if (text.getData("format")!=null)
					d = formatter.parse(text.getText());
				else
					d =monformatter.parse(text.getText());
				cal.setDate(d);
			} catch (ParseException pe) {
				pe.printStackTrace();
			}
		}
		cal.open();
		img.dispose();
		
			
	}

	void fillDailyReport() {
		//System.out.println("Daily Process");
		Date date;
		resetDailyTab();
		try {
			date = formatter.parse(textDate.getText());
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			DateTime d = new DateTime();
			d.setTime(cal);
			int i = cmbDailyList.getSelectionIndex();
			{
				if (i==-1)
				{
					MessageBox box =  new MessageBox(getShell());
					box.setText("Message");
					box.setMessage("Please select a measurement point to display.");
					box.open();
					return;
				}
				if (meter[i].nMachineNum==0) return;
				stationLabelD.setText(cmbDailyList.getText()+ " Dose Rate nGy/h");
				DoesRateFile file = new DoesRateFile(
						DoesRateFile.getFileName(meter[i].nMachineNum, d));
				file.load();
				calculateData(i,file);
				
			}
			dailyTab.pack();
		} catch (ParseException e) {
			MessageBox box = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);

			box.setMessage(e.getMessage()
					+ "\r\nPlease use backup button to download first.");
			box.setText("Read Data Error");
			box.open();
			e.printStackTrace();
		} catch (IOException e) {
			MessageBox box = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
			box.setMessage(e.getMessage()
					+ "\r\nPlease use backup button to download first.");
			box.setText("Read Data Error");
			box.open();
			e.printStackTrace();
		}

	}

	void fillMonthlyReport()
	{
		Date date;
		resetMonthlyTab();
		try {
			date = monformatter.parse(textMonth.getText());
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			DateTime d = new DateTime();
			d.setTime(cal);
			int i = cmbMonthlyList.getSelectionIndex();
			if (i==-1)
			{
				MessageBox box =  new MessageBox(getShell());
				box.setText("Message");
				box.setMessage("Please select a measurement point to display.");
				box.open();
				return;
			}
			stationLabelM.setText(cmbDailyList.getText()+ " Dose Rate nGy/h");
			{
				if (meter[i].nMachineNum==0) return;
				calculateMonthData(i,cal);
			}
			dailyTab.pack();
		} catch (ParseException e) {
			MessageBox box = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);

			box.setMessage(e.getMessage()
					+ "\r\nPlease use backup button to download first.");
			box.setText("Read Data Error");
			box.open();
			e.printStackTrace();
		} 
	}
	
	void calculateMonthData(int index ,Calendar cal)
	{
		DecimalFormat fVal = new DecimalFormat("0.00");
		int   count,  mcount;
		double maxV = 0;
		double minV = Double.MAX_VALUE;
		double sum = 0;
		double dmax = 0;
		double dmin = Double.MAX_VALUE;
		double dsum = 0;
		
		double[] values = null;
		double[] mvalues = null;

		int i = 0;
		count = 0;
		mcount = 0;
		

		DateTime date = new DateTime();
		date.setTime(cal);
		for(date.day=1;date.day <= cal.getMaximum(Calendar.DAY_OF_MONTH);date.day+=1)
		{
			//System.out.println(date.toStringDate());
			count = 0;
			sum=0;
			maxV = 0;
			minV = Double.MAX_VALUE;
			
			DoesRateFile file = new DoesRateFile(
				DoesRateFile.getFileName(meter[index].nMachineNum, date));
			try {
				file.load();
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}

			DoesRateData data;
			for (i=0;i < file.dataArray.size();i++) 
			{
				data = file.dataArray.get(i);
				if (data != null && data.isValideData()) {
					//System.out.println(String.valueOf(j) + ":" + data.CSVString());
					if (values == null)
						values = new double[24* 60 / data.cPT];
					if (mvalues==null)
						mvalues = new double[31*24* 60 / data.cPT];
					double v=data.getDoesRatenGy();
					maxV = Math.max(maxV, v);
					dmax = Math.max(dmax, v);
					minV = Math.min(minV, v);
					dmin = Math.min(dmin, v);
					sum += v;
					dsum += v;
					values[count++] = (double) v;
					mvalues[mcount++] = (double) v;
				}
				
			}
			
			MonthlyItem[date.day-1][0].setText(String.valueOf((double) maxV));
			if (count > 0)
				MonthlyItem[date.day-1][1].setText(String.valueOf((double) minV));
			else
				MonthlyItem[date.day-1][1].setText(String.valueOf(0));

			if (count > 0)
				MonthlyItem[date.day-1][2].setText(fVal.format((float) sum 
						/ (float) count));
			else
				MonthlyItem[date.day-1][2].setText(String.valueOf(0));

			if (count > 0)
				MonthlyItem[date.day-1][3].setText(fVal
						.format(get3sigma(values, count)));
			else
				MonthlyItem[date.day-1][3].setText(String.valueOf(0));
			file = null;
			//values=null;
		}
		i=31;
		MonthlyItem[i][0].setText(String.valueOf((double) dmax));
		if (mcount > 0)
			MonthlyItem[i][1].setText(String.valueOf((double) dmin));
		else
			MonthlyItem[i][1].setText(String.valueOf(0));

		if (mcount > 0)
			MonthlyItem[i][2].setText(fVal.format((float) dsum 
					/ (float) mcount));
		else
			MonthlyItem[i][2].setText(String.valueOf(0));

		if (mcount > 0)
			MonthlyItem[i][3].setText(fVal
					.format(get3sigma(mvalues, mcount)));
		else
			MonthlyItem[i][3].setText(String.valueOf(0));
	
	}
	
	void calculateData(int index,DoesRateFile file) {
		//System.out.println(file.toString());
		DecimalFormat fVal = new DecimalFormat("0.00");
		int   count,  dcount;
		double maxV = 0;
		double minV = Double.MAX_VALUE;
		double sum = 0;
		count = 0;
		int nowhour = 0;
		double[] values = null;
		double[] dvalues = new double[file.dataArray.size()];
		int j = 0;
		int i = -1;

		count = 0;
		dcount = 0;
		maxV = 0;
		double dmax = 0;
		minV = Double.MAX_VALUE;
		double dmin = Double.MAX_VALUE;
		sum = 0;
		double dsum = 0;
		nowhour = 0;
		while (j < file.dataArray.size()) {
			DoesRateData data = file.dataArray.get(j);
			if (data != null && data.isValideData()) {
				//System.out.println(String.valueOf(j) + ":" + data.CSVString());
				if (values == null)
					values = new double[60 / data.cPT];
				
				if (data.date.hour != nowhour && count==0)
					nowhour = data.date.hour;
				
				if (data.date.hour != nowhour && count>0) {
					i = nowhour;
					dailyItem[i][0].setText(String
							.valueOf((double) maxV ));
					if (count > 0)
						dailyItem[i][1].setText(String
								.valueOf((double) minV ));
					else
						dailyItem[i][1].setText(String.valueOf(0));
					
					if (count > 0)
						dailyItem[i][2].setText(fVal.format((float) sum
								 / (float) count));
					else
						dailyItem[i][2].setText(String.valueOf(0));
					
					if (count > 0)
						dailyItem[i][3].setText(fVal.format(get3sigma(
								values, count)));
					else
						dailyItem[i][3].setText(String.valueOf(0));

					count = 0;
					maxV = 0;
					minV = Integer.MAX_VALUE;
					sum = 0;
					nowhour = data.date.hour;
				}

				maxV = Math.max(maxV, data.getDoesRatenGy());
				dmax = Math.max(dmax, data.getDoesRatenGy());
				minV = Math.min(minV, data.getDoesRatenGy());
				dmin = Math.min(dmin, data.getDoesRatenGy());
				sum += data.getDoesRatenGy();
				dsum += data.getDoesRatenGy();
				values[count++] = (double) data.getDoesRatenGy();
				dvalues[dcount++] = (double) data.getDoesRatenGy();
				if (j + 1 == file.dataArray.size()&& i!=nowhour)
				{
					i=nowhour;
					dailyItem[i][0].setText(String
							.valueOf((double) maxV ));
					if (count > 0)
						dailyItem[i][1].setText(String
								.valueOf((double) minV ));
					else
						dailyItem[i][1].setText(String.valueOf(0));
					
					if (count > 0)
						dailyItem[i][2].setText(fVal.format((float) sum
								 / (float) count));
					else
						dailyItem[i][2].setText(String.valueOf(0));
					
					if (count > 0)
						dailyItem[i][3].setText(fVal.format(get3sigma(
								values, count)));
					else
						dailyItem[i][3].setText(String.valueOf(0));

				}
			}
			j++;
		}
		;
		i = 24;
		
		dailyItem[i][0].setText(String.valueOf((double) dmax));
		if (dcount > 0)
			dailyItem[i][1].setText(String.valueOf((double) dmin));
		else
			dailyItem[i][1].setText(String.valueOf(0));

		if (dcount > 0)
			dailyItem[i][2].setText(fVal.format((float) dsum 
					/ (float) dcount));
		else
			dailyItem[i][2].setText(String.valueOf(0));

		if (dcount > 0)
			dailyItem[i][3].setText(fVal
					.format(get3sigma(dvalues, dcount)));
		else
			dailyItem[i][3].setText(String.valueOf(0));
	}

	double get3sigma(double[] data, int n) {
		double xbar = 0;
		double sigma;
		int i = 0;
		while (i < n && i < data.length) {
			xbar += data[i++];
		}
		xbar = xbar / n;
		//System.out.print("XBAR="+String.valueOf(xbar));
		i = 0;
		sigma = 0;
		double x = 0;
		while (i < n && i < data.length) {
			x = data[i++] - xbar;
			sigma += x * x;
		}
		//System.out.println(" sigma^2="+String.valueOf(sigma));
		sigma = 3 * Math.sqrt(sigma / (n - 1));
		//System.out.println(" sigma^0.5*3="+String.valueOf(sigma));
		return sigma;
	}
	
	void PrintDailyReport()
	{
		
		PrintDialog dlg = new PrintDialog(getShell(),SWT.NONE);
		PrinterData printData = dlg.open();
		if (printData==null) return;
		
		//System.out.println("printing");
		Printer printer= new Printer(printData);
		if (printer.startJob("DailyReport"))
		{
			GC gc = new GC(printer);
			Rectangle trim = printer.computeTrim(0, 0, 0, 0);
			Point dpi = printer.getDPI();
			System.out.println(trim.toString());
			int leftMargin = dpi.x+trim.x;
			int topMargin = dpi.x/2 + trim.y;
			
			int mmWidth = 120;
			int mmLineHeight = 5;
			int []center = new int[5];
			for (int i=0;i<5;i++)
			{
				center[i]=leftMargin + (mmWidth*dpi.x/25)/10 * (2*i+1);
				//System.out.println("CenterX="+String.valueOf(center[i]));
			}
			
				if (printer.startPage()) {
					Point pt;
					gc.drawString(textDate.getText(), leftMargin, topMargin);
					int Y = topMargin + 2*mmLineHeight*dpi.y/25;
					gc.drawString(ConstData.strDataType, leftMargin, Y);
					pt=gc.stringExtent(stationLabelD.getText());
					gc.drawLine(leftMargin, topMargin+pt.y, leftMargin+mmWidth*dpi.x/25, topMargin+pt.y);
					gc.drawString(stationLabelD.getText(), 
							leftMargin + (mmWidth * dpi.x/25* 3)/5 - (pt.x)/2,Y);
					gc.drawLine(leftMargin, Y+pt.y, leftMargin+mmWidth*dpi.x/25, Y+pt.y);
					Y+=mmLineHeight*dpi.y/25;
					pt=gc.stringExtent(ConstData.strTime);
					gc.drawString(ConstData.strTime, center[0]-pt.x/2,Y );
					pt=gc.stringExtent(ConstData.strMax);
					gc.drawString(ConstData.strMax, center[1]-pt.x/2,Y);
					pt=gc.stringExtent(ConstData.strMin);
					gc.drawString(ConstData.strMin, center[2]-pt.x/2, Y);
					pt=gc.stringExtent(ConstData.strAvg);
					gc.drawString(ConstData.strAvg, center[3]-pt.x/2, Y);
					pt=gc.stringExtent(ConstData.str3Sigma);
					gc.drawString(ConstData.str3Sigma, center[4]-pt.x/2,Y);
					
					Y+=mmLineHeight*dpi.y/25;
					for(int n=0;n<24;n++)
					{
						pt=gc.stringExtent(String.format("%02d",n)+":00-"+String.format("%02d",n+1)+":00");
						gc.drawString(String.format("%02d",n)+":00-"+String.format("%02d",n+1)+":00", center[0]-pt.x/2,Y);
						for (int m=0;m<4;m++)
						{
//							System.out.println(String.valueOf(i)+" ,"+
//									String.valueOf(n)+" ,"+
//									String.valueOf(m));
							pt=gc.stringExtent(dailyItem[n][m].getText());
							gc.drawString(dailyItem[n][m].getText(), center[m+1]-pt.x/2,Y);
						}
						Y+=mmLineHeight*dpi.y/25;
					}
					
					gc.drawLine(leftMargin, Y, leftMargin+mmWidth*dpi.x/25, Y);
					
					Y+=mmLineHeight*dpi.y/25/4;
					pt=gc.stringExtent("Summary");
					gc.drawString("Summary", center[0]-pt.x/2,Y);
					for (int m=0;m<4;m++)
					{
						pt=gc.stringExtent(dailyItem[24][m].getText());
						gc.drawString(dailyItem[24][m].getText(), center[m+1]-pt.x/2,Y);
					}
					printer.endPage();
				
				}
			gc.dispose();
			printer.endJob();
		}
		printer.dispose();
	}

	void PrintMonthlyReport()
	{
		
		PrintDialog dlg = new PrintDialog(getShell(),SWT.NONE);
		PrinterData printData = dlg.open();
		
		if (printData==null) return;
		
		Printer printer= new Printer(printData);
		if (printer.startJob("MonthlyReport"))
		{
			GC gc = new GC(printer);
			Rectangle trim = printer.computeTrim(0, 0, 0, 0);
			Point dpi = printer.getDPI();
			System.out.println(trim.toString());
			int leftMargin = dpi.x+trim.x;
			int topMargin = dpi.x/2 + trim.y;
			
			int mmWidth = 120;
			int mmLineHeight = 5;
			int []center = new int[5];
			for (int i=0;i<5;i++)
			{
				center[i]=leftMargin + (mmWidth*dpi.x/25)/10 * (2*i+1);
				//System.out.println("CenterX="+String.valueOf(center[i]));
			}
			
			{
				
				if (printer.startPage()) {
					Point pt;
					gc.drawString(textMonth.getText(), leftMargin, topMargin);
					int Y = topMargin + 2*mmLineHeight*dpi.y/25;
					gc.drawString(ConstData.strDataType, leftMargin, Y);
					pt=gc.stringExtent(stationLabelM.getText());
					gc.drawLine(leftMargin, topMargin+pt.y, leftMargin+mmWidth*dpi.x/25, topMargin+pt.y);
					gc.drawString(stationLabelM.getText(), 
							leftMargin + (mmWidth * dpi.x/25* 3)/5 - (pt.x)/2,Y);
					gc.drawLine(leftMargin, Y+pt.y, leftMargin+mmWidth*dpi.x/25, Y+pt.y);
					Y+=mmLineHeight*dpi.y/25;
					pt=gc.stringExtent(ConstData.strDate);
					gc.drawString(ConstData.strDate, center[0]-pt.x/2,Y );
					pt=gc.stringExtent(ConstData.strMax);
					gc.drawString(ConstData.strMax, center[1]-pt.x/2,Y);
					pt=gc.stringExtent(ConstData.strMin);
					gc.drawString(ConstData.strMin, center[2]-pt.x/2, Y);
					pt=gc.stringExtent(ConstData.strAvg);
					gc.drawString(ConstData.strAvg, center[3]-pt.x/2, Y);
					pt=gc.stringExtent(ConstData.str3Sigma);
					gc.drawString(ConstData.str3Sigma, center[4]-pt.x/2,Y);
					
					Y+=mmLineHeight*dpi.y/25;
					for(int n=0;n<31;n++)
					{
						pt=gc.stringExtent(String.format("%02d",n+1));
						gc.drawString(String.format("%02d",n+1), center[0]-pt.x/2,Y);
						for (int m=0;m<4;m++)
						{
							pt=gc.stringExtent(MonthlyItem[n][m].getText());
							gc.drawString(MonthlyItem[n][m].getText(), center[m+1]-pt.x/2,Y);
						}
						Y+=mmLineHeight*dpi.y/25;
					}
					
					gc.drawLine(leftMargin, Y, leftMargin+mmWidth*dpi.x/25, Y);
					
					Y+=mmLineHeight*dpi.y/25/4;
					pt=gc.stringExtent("Summary");
					gc.drawString("Summary", center[0]-pt.x/2,Y);
					for (int m=0;m<4;m++)
					{
						pt=gc.stringExtent(MonthlyItem[31][m].getText());
						gc.drawString(MonthlyItem[31][m].getText(), center[m+1]-pt.x/2,Y);
					}
					printer.endPage();
				}
			}
			gc.dispose();
			printer.endJob();
		}
		printer.dispose();
	}
}
