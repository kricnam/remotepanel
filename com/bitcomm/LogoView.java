package com.bitcomm;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
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
		GC gc = e.gc;
		
		
		Image imgLogo = new Image(gc.getDevice(),"com/bitcomm/resource/logo.png");
		GC g = e.gc;
		
		g.drawImage(imgLogo,e.width-imgLogo.getBounds().width-100,0);
		g.drawText("ALOKA Console", 0, 0);
		imgLogo.dispose();

		

	}
	
}
