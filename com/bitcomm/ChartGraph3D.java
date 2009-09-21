package com.bitcomm;


import java.text.DecimalFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class ChartGraph3D extends Canvas {
	double [][]Data;
	double nMaxData;
	double nMinData;
	double yRate;
	double xRate;
	double YOffset;
	double Xmin;
	double Xmax;
	int Margin;
	int MaxCount;
	double nScaleMax;
	double nScaleMin;
	double xScaleRate;  // 
	int nXMarkNum;
	int nYMarkNum;
	
	int nDepthStep;
	double degree;
	public ChartGraph3D(Composite parent, int style) {
		// TODO 自动生成构造函数存根
		super(parent, style);
		// TODO 自动生成构造函数存根
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				ChartGraph3D.this.paintControl(e);
			}
		});
		nMaxData = 0;
		nMinData = nMaxData;
		nScaleMax = 0;
		nScaleMin = nScaleMax;
		YOffset = 0;
		MaxCount = 0;
		xScaleRate=1;
		nXMarkNum = 0;
		nYMarkNum = 0;
		nDepthStep = 1;
		degree = -Math.PI / 4;
	}
	void paintControl(PaintEvent e) {
		GC gc = e.gc;
		
		Point size = getSize();
		
		if (nMaxData==nMinData) AutoSetTransform(0,0,size.x,size.y);
		else SetTransform(0,0,size.x,size.y);
		
		
		//Color white= getDisplay().getSystemColor(SWT.COLOR_WHITE);
		//setBackground(white);
		
		drawBackground(gc, 0, 0, size.x, size.y);
		
		
		drawData(gc, 0, 0,size.x, size.y);

	}
	/* （非 Javadoc）
	 * @see org.eclipse.swt.widgets.Canvas#drawBackground(org.eclipse.swt.graphics.GC, int, int, int, int)
	 */
	@Override
	public void drawBackground(GC gc, int x, int y, int width, int height) {
		// TODO 自动生成方法存根
		//super.drawBackground(gc, x, y, width, height);
		
		if (Data.length>1) drawAxis3D(gc, x, y, width, height);
		else drawAxis(gc, x, y, width, height);
	}

	public void AutoSetTransform(int x, int y, int width, int height)
	{
		Margin = Math.min((int)(width * 0.04),(int)(height * 0.04));
		
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
		
		SetTransform(x,y,width,height);

	}

	public void SetTransform(int x, int y, int width, int height)
	{
		if (nScaleMax == nScaleMin)
		{
			nScaleMax = Math.ceil(nMaxData );
			nScaleMin = Math.floor(nMinData);
		}

		double nRange = nScaleMax - nScaleMin;
		if (MaxCount==0)
		{
			for (int j=0;j < Data.length;j++)
				MaxCount = Math.max(MaxCount, Data[j].length);
		}

		yRate = nRange  / (height-2*Margin -Math.abs(Data.length*nDepthStep*Math.sin(degree)));
		
		if (nScaleMin < 0)
			YOffset =  -nScaleMin ;
		else 
			YOffset = 0;
		
		//YOffset += Math.round(Math.abs(Data.length * nDepthStep*Math.cos(degree)));
		
		xRate = (double) (width-2*Margin - Math.abs(Data.length * nDepthStep*Math.cos(degree)))/MaxCount;
	}
	
	
	public void drawAxis(GC gc, int x, int y, int width, int height) {
		// TODO 自动生成方法存根
		Color gray= getDisplay().getSystemColor(SWT.COLOR_GRAY);		
		
		gc.setForeground(gray);
		gc.setLineStyle(SWT.LINE_DOT);
		
		double avg = (nScaleMax-nScaleMin)/10;
		int ys;
		
		for (int i=0;i<11;i++)
		{
			ys = (int)Math.round((YOffset- nScaleMin-i*avg)/yRate)+Margin;
			gc.drawLine((int)Margin,y+ys,x+width-Margin,y+ys);
			
			DecimalFormat   df   =new   java.text.DecimalFormat("#.00");  
			String strValue  = df.format(nScaleMin+i*avg);
			gc.drawString(strValue, Margin, ys,true);
		}
		avg = MaxCount / nXMarkNum;
		for (int i=0;i<nXMarkNum;i++)
		{
			ys = (int)Math.round((i*avg)*xRate);
			gc.drawLine(x+Margin+ys,y+Margin,x+Margin+ys,y+height-Margin);
			gc.drawString(String.valueOf(i*avg*xScaleRate),x+Margin+ys-20 , y+height-Margin,true);
		}
		Color black= getDisplay().getSystemColor(SWT.COLOR_BLACK);
		gc.setForeground(black);
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.drawRectangle(x+Margin, y+Margin, width-2*Margin, height-2*Margin);
		
	}
	public void drawAxis3D(GC gc, int x, int y, int width, int height) {
		// TODO 自动生成方法存根
		
		double avg = (nScaleMax-nScaleMin)/10;
		int ys;
		int deltX = (int)Math.round(Data.length*nDepthStep*Math.cos(degree));
		int deltY = (int)Math.round(Data.length*nDepthStep*Math.sin(degree));

		Color black= getDisplay().getSystemColor(SWT.COLOR_BLACK);
		Color gray= getDisplay().getSystemColor(SWT.COLOR_WHITE);
		Color oldbk = gc.getBackground();
		gc.setBackground(gray);
		gc.setForeground(gray);
		//gc.fillRectangle(x,y,width,height);
		gc.fillRectangle(x+Margin+deltX, y+Margin, width-2*Margin-deltX, height-2*Margin-Math.abs(deltY));
		//);
		gc.setBackground(oldbk);

				
		//gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));	
		
		gc.setForeground(black);
		gc.setLineStyle(SWT.LINE_DOT);

		for (int i=0;i<11;i++)
		{
			ys = (int)Math.round((YOffset- nScaleMin-i*avg)/yRate)+Margin+Math.abs(deltY);
			gc.drawLine((int)Margin,y+ys,Margin+deltX,y+ys+deltY);
			DecimalFormat   df   =new   java.text.DecimalFormat("#.00");  
			String strValue  = df.format(nScaleMin+i*avg);
			gc.drawString(strValue, Margin, ys,true);
		}
		avg = MaxCount / nXMarkNum;
		for (int i=0;i<=nXMarkNum;i++)
		{
			ys = (int)Math.round((i*avg)*xRate);
			gc.drawLine(x+Margin+ys,y+height-Margin,
					x+Margin+ys+deltX,y+height-Margin+deltY);
			gc.drawString(String.valueOf(i*avg*xScaleRate),x+Margin+ys-20 , y+height-Margin,true);
		}
		
		avg = (nScaleMax-nScaleMin)/10;
		for (int i=0;i<11;i++)
		{
			ys = (int)Math.round((YOffset- nScaleMin-i*avg)/yRate)+Margin+Math.abs(deltY);
			gc.drawLine((int)Margin+Math.abs(deltX),y+ys+deltY,x+width-Margin,y+ys+deltY);
		}
		avg = MaxCount / nXMarkNum;
		for (int i=0;i<=nXMarkNum;i++)
		{
			ys = (int)Math.round((i*avg)*xRate);
			gc.drawLine(x+Margin+ys+deltX,y+Margin,x+Margin+ys+deltX,y+height-Margin+deltY);
		}
		gc.setLineStyle(SWT.LINE_SOLID);

		
	}
	public void drawData(GC gc, int x, int y, int width, int height) {
		for (int j=0;j<Data.length;j++)
		{
			int deltX = (int)Math.round(j*nDepthStep*Math.cos(degree));
			int deltY = (int)Math.round(j*nDepthStep*Math.sin(degree));
			int DY = (int)Math.round(Data.length*nDepthStep*Math.sin(degree));
			
			
		int []p = new int[Data[j].length];
		for (int i = 0; i < Data[j].length;i++)
		{
			p[i] = (int)Math.round((YOffset-Data[j][i])/ yRate)+Margin;
		}
		gc.setLineStyle(SWT.LINE_SOLID);

		for (int i = 0; i < Data[j].length-1;i++)
			gc.drawLine((int)(Margin + x+ (int)(i*xRate))+deltX, p[i]+deltY-DY,
					Margin + x+(int)((i+1)*xRate)+deltX,p[i+1]+deltY-DY);
		}
	}


}
