package com.bitcomm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.vafada.swtcalendar.SWTCalendarEvent;
import org.vafada.swtcalendar.SWTCalendarListener;

public class SpectrumView extends Composite {
	final SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
	Composite optionBar;
	ChartGraph graph;
	Display display;

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
		optionBar = new Composite(this,SWT.BORDER);
		RowLayout optionLayout = new RowLayout();
		optionLayout.marginLeft = 10;
		
		//optionLayout.justify = true;
		optionLayout.fill = true;
		optionBar.setLayout(optionLayout);
		optionBar.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
		Button butSetup = new Button(optionBar,SWT.PUSH);
		Button butDate = new Button(optionBar,SWT.PUSH);
		final Text textDate = new Text(optionBar,SWT.BORDER);
		Button butTime = new Button(optionBar,SWT.PUSH);
		final Text textTime = new Text(optionBar,SWT.BORDER);

		Button butDate1 = new Button(optionBar,SWT.PUSH);
		final Text textDate1 = new Text(optionBar,SWT.BORDER);
		Button butTime1 = new Button(optionBar,SWT.PUSH);
		final Text textTime1 = new Text(optionBar,SWT.BORDER);

		Button butPrint = new Button(optionBar,SWT.PUSH);
		
		butDate.setText("起始日期");
		butTime.setText("起始时间");
		butDate1.setText("结束日期");
		butTime1.setText("结束时间");
		butPrint.setText("打印");
		butSetup.setText("设置");
		textDate.setLayoutData(new RowData(100,16));
		textDate1.setLayoutData(new RowData(100,16));
		textTime.setLayoutData(new RowData(50,16));
		textTime1.setLayoutData(new RowData(50,16));
		textDate.setEditable(false);
		

		
		butDate.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                final SWTCalendarDialog cal = new SWTCalendarDialog(display);
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
                    	pe.printStackTrace();
                    }
                }
                cal.open();
            }
        });
		butDate1.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                final SWTCalendarDialog cal = new SWTCalendarDialog(display);
                cal.addDateChangedListener(new SWTCalendarListener() {
                    public void dateChanged(SWTCalendarEvent calendarEvent) {
                        textDate1.setText(formatter.format(calendarEvent.getCalendar().getTime()));
                    }
                });
                if (textDate.getText() != null && textDate.getText().length() > 0) {
                    try {
                        Date d = formatter.parse(textDate.getText());
                        cal.setDate(d);
                    } catch (ParseException pe) {
                    	pe.printStackTrace();
                    }
                }
                cal.open();
            }
        });

	}
	private void initGraph(){
		Composite com = new Composite(this,SWT.BORDER);
		com.setLayout(new FillLayout());
		com.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		graph = new ChartGraph(com,SWT.NONE);
		graph.Data = new double[1][360];
		for (int i=0;i<360 ;i++)
		{
			graph.Data[0][i]= Math.sin(i*Math.PI/180);
		}
		graph.Margin =20;
		graph.nMaxData = 2;
		graph.nMinData = -2;
		graph.nXMarkNum = 10;
		graph.nYMarkNum = 10;
	}

}
