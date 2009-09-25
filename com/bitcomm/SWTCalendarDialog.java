package com.bitcomm;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.vafada.swtcalendar.SWTCalendar;
import org.vafada.swtcalendar.SWTCalendarListener;

public class SWTCalendarDialog {
    private Shell shell;

    private SWTCalendar swtcal;

    private Display display;



    public SWTCalendarDialog(Display display) {

        this.display = display;

        shell = new Shell(display, SWT.APPLICATION_MODAL | SWT.CLOSE);

        shell.setLayout(new RowLayout());

        swtcal = new SWTCalendar(shell);

    }

    public SWTCalendarDialog(Shell parent, Image img,int x,int y) {

        this.display = parent.getDisplay();

        shell = new Shell(parent,  SWT.APPLICATION_MODAL | SWT.CLOSE);
        shell.setLocation(x, y);
        shell.setLayout(new RowLayout());

        swtcal = new SWTCalendar(shell);
        shell.setText("Calendar");
        if (img!=null) shell.setImage(img);
        

    }

    public SWTCalendarDialog(Shell parent, Image img) {

        this.display = parent.getDisplay();

        shell = new Shell(parent,  SWT.APPLICATION_MODAL | SWT.CLOSE);
        shell.setLayout(new GridLayout());

        swtcal = new SWTCalendar(shell);
        Button but = new Button(shell,SWT.PUSH);
        but.setText("Close");
        but.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
        but.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				shell.dispose();
				
			}
        	
        });
        shell.setText("Calendar");
        if (img!=null) shell.setImage(img);
        

    }
  
    public void open() {

        shell.pack();

        shell.open();

        while (!shell.isDisposed()) {

            if (!display.readAndDispatch()) display.sleep();

        }

    }



    public Calendar getCalendar() {

        return swtcal.getCalendar();

    }



    public void setDate(Date date) {

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);

        swtcal.setCalendar(calendar);

    }



    public void addDateChangedListener(SWTCalendarListener listener) {

        swtcal.addSWTCalendarListener(listener);

    }


}
