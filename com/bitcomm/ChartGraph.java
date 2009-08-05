package com.bitcomm;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class ChartGraph extends Canvas {

	double []Data;
	double nMaxData;
	double nMinData;
	
	
	public ChartGraph(Composite parent, int style) {
		super(parent, style);
		// TODO 自动生成构造函数存根
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				ChartGraph.this.paintControl(e);
			}
		});
	}
	void paintControl(PaintEvent e) {
		GC gc = e.gc;
		Point size = getSize();
		Color white= getDisplay().getSystemColor(SWT.COLOR_WHITE);
		//setBackground(white);
		drawBackground(gc, 0, 0, size.x, size.y);
		
		drawAxis(gc, 0, 0,size.x, size.y);
	}
	/* （非 Javadoc）
	 * @see org.eclipse.swt.widgets.Canvas#drawBackground(org.eclipse.swt.graphics.GC, int, int, int, int)
	 */
	@Override
	public void drawBackground(GC gc, int x, int y, int width, int height) {
		// TODO 自动生成方法存根
		//super.drawBackground(gc, x, y, width, height);
		
		
	}
	public void drawAxis(GC gc, int x, int y, int width, int height) {
		// TODO 自动生成方法存根
		//super.drawBackground(gc, x, y, width, height);		
		gc.drawRectangle(x+10, y+10, width-20, height-20);
		gc.setLineStyle(SWT.LINE_DOT);
		gc.drawLine(x+20, y+20, width-30, y+20);
		
		Path path;
		gc.drawPath(path);
	}


}
