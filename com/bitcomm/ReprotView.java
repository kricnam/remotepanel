package com.bitcomm;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import org.vafada.swtcalendar.SWTCalendarEvent;
import org.vafada.swtcalendar.SWTCalendarListener;
import java.text.ParseException;

import java.text.SimpleDateFormat;

import java.util.Date;

public class ReprotView extends Composite {
	CTabFolder folder;
	Image img;
	Composite comDaily;
    final SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
	Table dailyTab;
	Table monthTab;
	public ReprotView(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		setSize(this.getShell().getSize());
		
//		System.out.println(this.getShell().getSize().toString());
		setLayout(new FillLayout());
		img = new Image(this.getDisplay(),"com/bitcomm/resource/tab.png");
		folder = new CTabFolder(this,SWT.BORDER);
		folder.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
		folder.setSimple(false);
		folder.setUnselectedImageVisible(true);
		folder.setUnselectedCloseVisible(true);
		folder.setSelectionForeground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		folder.setSelectionBackground(this.getDisplay().getSystemColor(SWT.COLOR_BLUE));
		folder.setMaximized(true);
		
		CTabItem itemDaily = new CTabItem(folder,SWT.NONE);
		CTabItem itemMonthly = new CTabItem(folder,SWT.NONE);
		itemDaily.setText("日报表");
		itemDaily.setImage(img);
		createDailyTable();
		itemDaily.setControl(comDaily);
		itemMonthly.setText("月表报");
		itemMonthly.setImage(img);
		
	}
	
	private void createDailyTable()
	{
		comDaily = new Composite(folder,SWT.NONE);
		GridLayout comLayout = new GridLayout();
		comLayout.numColumns =1;
		comDaily.setLayout(comLayout);
		
		Composite optionBar = new Composite(comDaily,SWT.NONE);
		RowLayout optionLayout = new RowLayout();
		optionLayout.marginLeft = 10;
		
		optionLayout.justify = true		;
		optionLayout.fill = true;
		
		
		optionBar.setLayout(optionLayout);
		Button butDate = new Button(optionBar,SWT.NONE);
		final Text textDate = new Text(optionBar,SWT.SINGLE);
		Button butPrint = new Button(optionBar,SWT.NONE);
		
		butDate.setText("日期选择");
		
        butDate.addListener(SWT.Selection, new Listener() {

            public void handleEvent(Event event) {

                final SWTCalendarDialog cal = new SWTCalendarDialog(folder.getShell().getDisplay());

                cal.addDateChangedListener(new SWTCalendarListener() {

                    public void dateChanged(SWTCalendarEvent calendarEvent) {

                        textDate.setText(formatter.format(calendarEvent.getCalendar().getTime()));

                    }

                });
                if (textDate.getText() != null && textDate.getText().length() > 0) {

                    try {

                        Date d = formatter.parse(textDate.getText());

                        cal.setDate(d);

                    } catch (ParseException pe) {



                    }

                }

                cal.open();



            }

        });

		
		butPrint.setText("打印");
		textDate.setTextLimit(20);
		textDate.setLayoutData(new RowData(200,16));
		
		
		GridData gridData= new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace  = true;
		gridData.grabExcessVerticalSpace =  true;
		gridData.verticalAlignment = SWT.FILL;
		
		dailyTab = new Table(comDaily,SWT.FULL_SELECTION);
		dailyTab.setHeaderVisible(true);
		dailyTab.setLayoutData(gridData);
		dailyTab.setLinesVisible(true);
		String []tabHead = {"1","2","3","4"};
		for (int i=0;i<tabHead.length;i++)
		{
			TableColumn tabCol = new TableColumn(dailyTab,SWT.NONE);
			tabCol.setText(tabHead[i]);
		}
		
		TableItem item = new TableItem(dailyTab,SWT.NONE);
		item.setText(new String[]{"a","b","c","d"});
		
		for (int i=0;i<tabHead.length;i++)
		{
			dailyTab.getColumn(i).pack();
		}
	}
	
}
