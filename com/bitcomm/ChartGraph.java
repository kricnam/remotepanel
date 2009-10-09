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
	 
	Color []color;
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
		//System.out.println(size.toString());
		Color white= getDisplay().getSystemColor(SWT.COLOR_WHITE);
		
		setBackground(white);
		if (Data==null) 
		{
			drawBackground(gc, 0,0,size.x,size.y);
			return;
		}
		
		if (nMaxData==nMinData) AutoSetTransform(gc,0,0,size.x,size.y);
		else SetTransform(0,0,size.x,size.y);
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
		if (Data!=null)
			drawAxis(gc, x, y, width, height);
	}

	public void AutoSetTransform(GC gc,int x, int y, int width, int height)
	{
		Point pt = gc.stringExtent("00000000000");
		Margin = Math.min((int)(width * 0.04),(int)(height * 0.04));
		Margin = Math.max(Margin, pt.x);
		
		if (nMaxData == 0)
		{
			for (int j=0;j < Data.length;j++)
			{
				MaxCount = Math.max(MaxCount, Data[j].length);
				for(int i=0 ; i< Data[j].length;i++)
				{
					nMaxData = Math.max(nMaxData , Data[j][i]);
				}
			}
		}
		if (nMinData == 0)
		{
			for (int j=0;j < Data.length;j++)
				for(int i=0 ; i< Data[j].length;i++)
					nMinData = Math.min(nMinData , Data[j][i]);
		}
		
		SetTransform(x,y,width,height);

	}

	public void SetTransform(int x, int y, int width, int height)
	{
		if (nScaleMax == nScaleMin)
		{
			nScaleMax = Math.ceil(nMaxData/100 )*100;
			nScaleMin = Math.floor(nMinData);
		}
		nScaleLogMax = Math.log10(nScaleMax);
		nScaleLogMin = (nScaleMin==0)?-2:Math.log10(nScaleMin);
		double nRange = nScaleMax - nScaleMin;
		double nLogRange = nScaleLogMax - nScaleLogMin;
		if (MaxCount==0)
		{
			for (int j=0;j < Data.length;j++)
				MaxCount = Math.max(MaxCount, Data[j].length);
		}

		yRate = nRange  / (height-2*Margin);
		yLogRate = nLogRange / (height-2*Margin);
		//System.out.println("yRate="+String.valueOf(yRate));
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
		Color gray= getDisplay().getSystemColor(SWT.COLOR_GRAY);		
		
		gc.setForeground(gray);
		gc.setLineStyle(SWT.LINE_DOT);
		Point pt= gc.stringExtent("Does Rate[nGy/h]");
		gc.drawString("Does Rate[nGy/h]",x+pt.y,y+pt.y,true);
		if (logScale)
		{
			pt = gc.stringExtent("-LOG-");
			gc.drawString("-LOG-",x+width/2-pt.x/2,y+10,true);
			drawLogAxisY(gc, x, y, width, height);
		}
		else
		{
			pt = gc.stringExtent("-LIN-");
			gc.drawString("-LIN-",x+width/2-pt.x/2,y+10,true);
			drawAxisY(gc, x, y, width, height);
		}
			
		//X axis
		if (MaxCount< 12) nXMarkNum = MaxCount;
		else if (MaxCount<120) nXMarkNum = MaxCount/(MaxCount/12);
		double avg = (double)MaxCount / nXMarkNum;
		int ys;
		
		int intv = (width-2*Margin)/nXMarkNum;
		for (int i=0;i<nXMarkNum+1;i++)
		{
			ys = (int)Math.round((i*avg)*xRate);
			gc.drawLine(x+Margin+ys,y+Margin,x+Margin+ys,y+height-Margin);
			pt = gc.stringExtent("00");
			if (strScaleX!=null)
					
			{
				if ( (int)(i* avg) < MaxCount && strScaleX[(int)(i* avg)]!=null)
				{
					pt = gc.stringExtent(strScaleX[(int)Math.round(i* avg)]);
					if ((pt.x/2) > intv && i%2==1) continue;
					gc.drawText(strScaleX[(int)Math.round(i* avg)],x+Margin+ys-pt.x/4 , y+height-Margin+pt.y,true);
				}
				else
					gc.drawText("",x+Margin+ys-pt.x , y+height-Margin+pt.y,true);
			}
			else
			{
				gc.drawString(String.valueOf((int)i*avg*xScaleRate),x+Margin+ys-pt.x , y+height-Margin+pt.y,true);
			}
		}
		gc.drawString("Time",x+width-Margin/2 , y+height-Margin,true);
		Color black= getDisplay().getSystemColor(SWT.COLOR_BLACK);
		gc.setForeground(black);
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.drawRectangle(x+Margin, y+Margin, width-2*Margin, height-2*Margin);
		
	}
	
	public void drawAxisY(GC gc, int x, int y, int width, int height) 
	{
		int count =(int)( Math.floor(nScaleMax)-Math.ceil(nScaleMin));
		
		double div = Math.pow(10, Math.floor(Math.log10(count)-1));
		while (count>10)
		{
			count =(int)( (Math.floor(nScaleMax)-Math.ceil(nScaleMin))/div);
			if (count > 10)	div+=div;
		}
		if (count * div < 100)
		{
			div =10;
			count = (int)( (Math.floor(nScaleMax)-Math.ceil(nScaleMin))/div);
		}
		if (count==0)
		{
			count=1;
			div = 1;
		}
			
		int ys;
		//Y axis
		for (int i=0;i<count+1;i++)
		{
			double ceil = (((int)nScaleMin/(int)div)+i)*(int)div;
			
			if (nScaleMin%div > 0) ceil+=div;
			ys = height - (int)Math.round((YOffset + ceil - nScaleMin)/yRate) -Margin;
			if (y+ys < Margin) continue;
			gc.drawLine((int)x+Margin,y+ys,x+width-Margin,y+ys);
			
			DecimalFormat   df   =new   java.text.DecimalFormat("#");  
			String strValue  = df.format(ceil);
			Point pt=gc.stringExtent(strValue);
			gc.drawString(strValue, x+Margin-pt.x, y+ys-pt.y/2,true);
		}
	}
	
	public void drawLogAxisY(GC gc, int x, int y, int width, int height) 
	{
		int nLogScaleCount = (int)Math.floor(nScaleLogMax) - (int)Math.ceil(nScaleLogMin)+1; 

		int ys;
		//Y axis
		Point pt;
		for (int i=0;i<nLogScaleCount;i++)
		{
			double ceil = Math.ceil(nScaleLogMin+i);
			ys = height - (int)Math.round(( ceil-nScaleLogMin)/yLogRate) -Margin;
			if (y+ys < Margin) continue;
			gc.drawLine((int)Margin+x,y+ys,x+width-Margin,y+ys);
			
			DecimalFormat   df;
			if (ceil >= 1) df= new   java.text.DecimalFormat("#");
			else df = new java.text.DecimalFormat("#0.0#");
			String strValue  = df.format(Math.pow(10, ceil));
			pt=gc.stringExtent(strValue);
			gc.drawString(strValue, Margin+x-pt.x, y+ys-pt.y/2,true);
			df = null;
		}
		if (nLogScaleCount==0)
		{
			DecimalFormat   df;
			df= new   java.text.DecimalFormat("#");
			String strValue  = df.format(nScaleMax);
			pt=gc.stringExtent(strValue);
			gc.drawString(strValue, Margin+x-pt.x, y+Margin-pt.y/2,true);
			strValue  = df.format(nScaleMin);
			pt=gc.stringExtent(strValue);
			gc.drawString(strValue, Margin+x-pt.x, y+height-Margin-pt.y/2,true);
		}
	}
	
	public void drawData(GC gc, int x, int y, int width, int height) {
		
		gc.setLineStyle(SWT.LINE_SOLID);

		for (int n=0;n < Data.length; n++)
		{
			int []p = new int[Data[n].length];
			
			
				
			if (logScale)
			{
				for (int i = 0; i < Data[0].length;i++)
				{
					//p[i] = height - (int)Math.round(( YLogOffset+Math.log10(Data[n][i])-nScaleLogMin)/ yLogRate) -Margin;
					if (Data[n][i]>nScaleMax)
					{
						p[i]= Margin;
						continue;
					}
					if (Data[n][i]<nScaleMin)
					{
						p[i]= height - Margin;
						continue;
					}
					p[i] = height - (int)Math.round(( Math.log10(Data[n][i])-nScaleLogMin)/ yLogRate) -Margin;
				}
			}
			else
			{
				for (int i = 0; i < Data[0].length;i++)
				{
					if (Data[n][i]>nScaleMax)
					{
						p[i]= Margin;
						continue;
					}
					if (Data[n][i]<nScaleMin)
					{
						p[i]= height - Margin;
						continue;
					}
					//p[i] = height - (int)Math.round(( YOffset+Data[n][i]-nScaleMin)/ yRate) -Margin;
					p[i] = height - (int)Math.round(( Data[n][i]-nScaleMin)/ yRate) -Margin;
				}
			}
			
			if (color!=null && n<color.length && color[n]!=null)
			{
				gc.setForeground(color[n]);
			}
			else
				gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLUE));

			for (int i = 0; i < Data[n].length-1;i++)
			{
				if (p[i+1]== height - Margin) 
				{
					gc.drawLine((int)(Margin + x+ (int)(i*xRate)),y+ p[i],Margin + x+(int)((i)*xRate),y+p[i+1]);
					continue;
				}
				gc.drawLine((int)(Margin + x+ (int)(i*xRate)), y+p[i],Margin + x+(int)((i+1)*xRate),y+p[i+1]);
			}
		}
	}
}
