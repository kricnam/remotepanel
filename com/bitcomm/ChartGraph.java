package com.bitcomm;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class ChartGraph extends Canvas {

	double [][]Data;
	double nMaxData;
	double nMinData;
	double yRate;
	double xRate;
	double YOffset;
	double Xmin;
	double Xmax;
	int Margin;
	
	
	
	
	public ChartGraph(Composite parent, int style) {
		super(parent, style);
		// TODO 自动生成构造函数存根
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				ChartGraph.this.paintControl(e);
			}
		});
		nMaxData = 0;
		nMinData = nMaxData;
	}
	void paintControl(PaintEvent e) {
		GC gc = e.gc;
		
		Point size = getSize();
		
		if (nMaxData==nMinData) AutoSetTransform(0,0,size.x,size.y);
		else SetTransform(0,0,size.x,size.y);
		
		
		Color white= getDisplay().getSystemColor(SWT.COLOR_WHITE);
		
		setBackground(white);
		drawBackground(gc, 0, 0, size.x, size.y);
		
		
		drawData(gc, 0, 0,size.x, size.y);

	}
	/* （非 Javadoc）
	 * @see org.eclipse.swt.widgets.Canvas#drawBackground(org.eclipse.swt.graphics.GC, int, int, int, int)
	 */
	@Override
	public void drawBackground(GC gc, int x, int y, int width, int height) {
		// TODO 自动生成方法存根
		super.drawBackground(gc, x, y, width, height);
		drawAxis(gc, x, y, width, height);
		
	}

	public void AutoSetTransform(int x, int y, int width, int height)
	{
		Margin = Math.min((int)(width * 0.04),(int)(height * 0.04));
		int MaxCount=0;
		nMaxData = 0;
			for (int j=0;j < Data.length;j++)
			{
				MaxCount = Math.max(MaxCount, Data[j].length);
				for(int i=0 ; i< Data[j].length;i++)
				{
					nMaxData = Math.max(nMaxData , Data[j][i]);
				}
			}
		nMinData = 0;
			for (int j=0;j < Data.length;j++)
				for(int i=0 ; i< Data[j].length;i++)
					nMinData = Math.min(nMinData , Data[j][i]);
		
		
		double nRange = nMaxData - nMinData;
		yRate = (nRange / 0.8) / (height-2*Margin);
		if (nMinData < 0)
			YOffset = (0-nMinData)/0.8;
		else 
			YOffset = 0;
		
		xRate = (double) (width-2*Margin)/MaxCount;

	}

	public void SetTransform(int x, int y, int width, int height)
	{
		double nRange = nMaxData - nMinData;
		int MaxCount=0;
		for (int j=0;j < Data.length;j++)
			MaxCount = Math.max(MaxCount, Data[j].length);

		yRate = (nRange / 0.8) / (height-2*Margin);
		if (nMinData < 0)
			YOffset = (0-nMinData)/0.8;
		else 
			YOffset = 0;
		
		xRate = (double) (width-2*Margin)/MaxCount;
	}
	
	
	public void drawAxis(GC gc, int x, int y, int width, int height) {
		// TODO 自动生成方法存根
		Color gray= getDisplay().getSystemColor(SWT.COLOR_GRAY);		
		
		gc.drawRectangle(x+Margin, y+Margin, width-2*Margin, height-2*Margin);
		gc.setForeground(gray);
		gc.setLineStyle(SWT.LINE_DOT);
		//gc.drawLine(x1, y1, x2, y2);
		
	}
	
	public void drawData(GC gc, int x, int y, int width, int height) {
		int []p = new int[Data[0].length];
		for (int i = 0; i < Data[0].length;i++)
		{
			p[i] = (int)Math.round((Data[0][i]+ YOffset)/ yRate)+Margin;
		}
		gc.setLineStyle(SWT.LINE_SOLID);
		for (int i = 0; i < Data[0].length-1;i++)
			gc.drawLine((int)(Margin + x+ (int)(i*xRate)), p[i],Margin + x+(int)((i+1)*xRate),p[i+1]);
	}


}
