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


public class ChartGraph extends Canvas {

	double [][]Data;
	double nMaxData;
	double nMinData;
	double yRate;
	double xRate;
	double yLogRate;
	
	double YOffset;
	double YLogOffset;
	double Xmin;
	double Xmax;
	int Margin;
	int MaxCount;
	double nScaleMax;
	double nScaleMin;
	double xScaleRate;  //
	double nScaleLogMax;
	double nScaleLogMin;
	 

	int nXMarkNum;
	int nYMarkNum;
	String []strScaleX;
	int pos;
	boolean logScale;
	
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
		nScaleMax = 0;
		nScaleMin = nScaleMax;
		YOffset = 0;
		MaxCount = 0;
		xScaleRate=1;
		nXMarkNum = 0;
		nYMarkNum = 0;
		pos = 0;
		logScale = false;
	}
	
	void setData(double val,int col, String strXScale)
	{
		if (Data!=null && col < Data.length && pos < Data[col].length)
		{
			if (strScaleX!=null) strScaleX[pos]=strXScale;
			//if (pos==10) val=100000; 
			Data[col][pos++]=val;
			if (val > nMaxData) setAutoTransform(); 
			
		}
	}
	
	void ResetData()
	{
		if (Data!=null)
		{
			for (int i=0;i<Data.length;i++)
				{
					for (int j=0;j< Data[i].length;j++)
					{
						Data[i][j]=0;
					}
				}
		}
		MaxCount=0;
		nScaleMax=0;
		nScaleMin = nScaleMax;
		pos = 0;
	}
	
	void setAutoTransform()
	{
		nMaxData = 0;
		nMinData = nMaxData;
		nScaleMax = 0;
		nScaleMin = nScaleMax;
		MaxCount = 0;
	
	}
	
	
	void paintControl(PaintEvent e) {
		GC gc = e.gc;
		
		Point size = getSize();
		
		if (nMaxData==nMinData) AutoSetTransform(gc,0,0,size.x,size.y);
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

	public void AutoSetTransform(GC gc,int x, int y, int width, int height)
	{
		Point pt = gc.stringExtent("0000");
		Margin = Math.min((int)(width * 0.04),(int)(height * 0.04));
		Margin = Math.max(Margin, pt.y*2);
		
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
		nScaleLogMax = Math.log10(nScaleMax);
		nScaleLogMin = (nScaleMin==0)?0:Math.log10(nScaleMin);
		double nRange = nScaleMax - nScaleMin;
		double nLogRange = nScaleLogMax - nScaleLogMin;
		if (MaxCount==0)
		{
			for (int j=0;j < Data.length;j++)
				MaxCount = Math.max(MaxCount, Data[j].length);
		}

		yRate = nRange  / (height-2*Margin);
		yLogRate = nLogRange / (height-2*Margin);
		if (nScaleMin < 0)
			YOffset = -nScaleMin ;
		else 
			YOffset = 0;
		if (nScaleLogMin < 0)
			YLogOffset = -nScaleLogMin ;
		else 
			YLogOffset = 0;

		xRate = (double) (width-2*Margin)/MaxCount;
		
	}
	
	
	public void drawAxis(GC gc, int x, int y, int width, int height) {
		// TODO 自动生成方法存根
		Color gray= getDisplay().getSystemColor(SWT.COLOR_GRAY);		
		
		gc.setForeground(gray);
		gc.setLineStyle(SWT.LINE_DOT);
		gc.drawString("Does Rate[nGy/h]",x+10,y+10,true);
		if (logScale)
		{
			Point pt = gc.stringExtent("-LOG-");
			gc.drawString("-LOG-",x+width/2-pt.x/2,y+10,true);
			drawLogAxisY(gc, x, y, width, height);
		}
		else
		{
			Point pt = gc.stringExtent("-LIN-");
			gc.drawString("-LIN-",x+width/2-pt.x/2,y+10,true);
			drawAxisY(gc, x, y, width, height);
		}
			
		//X axis
		if (MaxCount< 12) nXMarkNum = MaxCount;
		else if (MaxCount<120) nXMarkNum = MaxCount/(MaxCount/12);
		double avg = MaxCount / nXMarkNum;
		int ys;
		for (int i=0;i<nXMarkNum+1;i++)
		{
			ys = (int)Math.round((i*avg)*xRate);
			gc.drawLine(x+Margin+ys,y+Margin,x+Margin+ys,y+height-Margin);
			if (strScaleX!=null)
					
			{
				if ( (int)(i* avg) < MaxCount && strScaleX[(int)(i* avg)]!=null)
					gc.drawText(strScaleX[(int)(i* avg)],x+Margin+ys-20 , y+height-Margin,true);
				else
					gc.drawText("",x+Margin+ys-20 , y+height-Margin,true);
			}
			else
			{
				gc.drawString(String.valueOf(i*avg*xScaleRate),x+Margin+ys-20 , y+height-Margin,true);
			}
		}
		Color black= getDisplay().getSystemColor(SWT.COLOR_BLACK);
		gc.setForeground(black);
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.drawRectangle(x+Margin, y+Margin, width-2*Margin, height-2*Margin);
		
	}
	
	public void drawAxisY(GC gc, int x, int y, int width, int height) 
	{
		double avg = (nScaleMax-nScaleMin)/10;
		int ys;
		//Y axis
		for (int i=0;i<11;i++)
		{
			ys = height - (int)Math.round((YOffset + nScaleMin + i*avg)/yRate) -Margin;
			gc.drawLine((int)Margin,y+ys,x+width-Margin,y+ys);
			
			DecimalFormat   df   =new   java.text.DecimalFormat("#.00");  
			String strValue  = df.format(nScaleMin+i*avg);
			if (i>0) gc.drawString(strValue, Margin, ys,true);
		}
	}
	
	public void drawLogAxisY(GC gc, int x, int y, int width, int height) 
	{
		double avg = (nScaleLogMax-nScaleLogMin)/10;
		int ys;
		//Y axis
		//System.out.println(String.valueOf(nScaleLogMax));
		//System.out.println(String.valueOf(nScaleLogMin));
		//System.out.println(String.valueOf(avg));
		for (int i=0;i<11;i++)
		{
			ys = height - (int)Math.round((YLogOffset + nScaleLogMin + i*avg)/yLogRate) -Margin;
			gc.drawLine((int)Margin,y+ys,x+width-Margin,y+ys);
			
			DecimalFormat   df   =new   java.text.DecimalFormat("#");  
			String strValue  = df.format(Math.pow(10, nScaleLogMin+i*avg));
			if (i>0) gc.drawString(strValue, Margin, ys,true);
		}
	}
	
	public void drawData(GC gc, int x, int y, int width, int height) {
		int []p = new int[Data[0].length];
		if (logScale)
		{
			for (int i = 0; i < Data[0].length;i++)
			{
				p[i] = height - (int)Math.round(( YLogOffset+Math.log10(Data[0][i]))/ yLogRate) -Margin;
			}
		}
		else
		{
			for (int i = 0; i < Data[0].length;i++)
			{
				p[i] = height - (int)Math.round(( YOffset+Data[0][i])/ yRate) -Margin;
			}
		}
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLUE));

		for (int i = 0; i < Data[0].length-1;i++)
		{
			if (p[i+1]== height - Margin) 
			{
				gc.drawLine((int)(Margin + x+ (int)(i*xRate)), p[i],Margin + x+(int)((i)*xRate),p[i+1]);
				continue;
			}
			gc.drawLine((int)(Margin + x+ (int)(i*xRate)), p[i],Margin + x+(int)((i+1)*xRate),p[i+1]);
		}
		
	}


}
