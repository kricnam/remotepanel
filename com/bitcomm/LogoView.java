package com.bitcomm;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class LogoView extends Canvas {
	public LogoView(Composite parent, int style)
	{
		super(parent, style);
		// TODO 自动生成构造函数存根
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				LogoView.this.paintControl(e);
			}
		});
	}
	void paintControl(PaintEvent e) {
		GC g = e.gc;
		Point p=getSize();
		FontData fdata = new FontData("Times", 20, SWT.NORMAL);
		Image imgLogo = new Image(g.getDevice(),"com/bitcomm/resource/logo.png");
		
		g.drawImage(imgLogo,p.x-imgLogo.getBounds().width-10,0);
		Font font = new Font(g.getDevice(),fdata);
		g.setFont(font);
		
		Point pt=g.stringExtent(ConstData.strTitle);
		g.drawText(ConstData.strTitle,p.x/2 - pt.x/2 , 0);
		imgLogo.dispose();
		font.dispose();
	}
	
}
