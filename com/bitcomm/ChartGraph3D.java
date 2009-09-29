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
	int pos;
	int nDepthStep;
	double degree;
	boolean UpDateSelect;
	public ChartGraph3D(Composite parent, int style) {
		// TODO 自动生成构造函数存根
		super(parent, style|SWT.DOUBLE_BUFFERED|SWT.NO_BACKGROUND);
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
		pos = 0;
		UpDateSelect =false;
	}
	void paintControl(PaintEvent e) {
		GC gc = e.gc;
		
		Point size = getSize();
		
		if (Data!=null)
		{
			if (nMaxData==nMinData) AutoSetTransform(gc,0,0,size.x,size.y);
			else SetTransform(0,0,size.x,size.y);
		}
		
		gc.setAntialias(SWT.ON);
		gc.setAdvanced(true);
		if (UpDateSelect)
		{
			UpDateSelect = false;
			
			//DrawSelection(gc,0, 0, size.x, size.y);
			//return;
		}
		setBackground(gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
		drawBackground(gc, 0, 0, size.x, size.y);
		
		if (Data!=null)	drawData(gc, 0, 0,size.x, size.y);
	}
	
	void UpdateSelection()
	{
		UpDateSelect = true;
		redraw();
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
	}
	
	void setAutoTransform()
	{
		nMaxData = 0;
		nMinData = nMaxData;
		nScaleMax = 0;
		nScaleMin = nScaleMax;
		MaxCount = 0;
	
	}

	/* （非 Javadoc）
	 * @see org.eclipse.swt.widgets.Canvas#drawBackground(org.eclipse.swt.graphics.GC, int, int, int, int)
	 */
	@Override
	public void drawBackground(GC gc, int x, int y, int width, int height) {
		// TODO 自动生成方法存根
		//super.drawBackground(gc, x, y, width, height);
		
		if (Data!=null && Data.length>1) drawAxis3D(gc, x, y, width, height);
		else drawAxis(gc, x, y, width, height);
	}

	public void AutoSetTransform(GC gc,int x, int y, int width, int height)
	{
		Point pt = gc.stringExtent("00000");
		System.out.println("MArgin:"+String.valueOf(Margin));
		Margin = Math.max(Margin,Math.max((int)(width * 0.04),(int)(height * 0.04)));
		System.out.println("MArgin:"+String.valueOf(Margin));
		Margin = Math.max(Margin, pt.y*2);
		System.out.println("MArgin:"+String.valueOf(Margin));
		
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
		//nDepthStep = height / 3 /(Data.length+2);
		//if (nDepthStep==0) nDepthStep=1;
		SetTransform(x,y,width,height);

	}

	public void SetTransform(int x, int y, int width, int height)
	{
		if (nScaleMax == nScaleMin)
		{
			nScaleMax = ((int)Math.ceil(nMaxData)/1000 + 1)*1000;
			nScaleMin = Math.floor(nMinData);
		}

		double nRange = nScaleMax - nScaleMin;
		if (MaxCount==0)
		{
			for (int j=0;j < Data.length;j++)
				MaxCount = Math.max(MaxCount, Data[j].length);
		}

		yRate = nRange  / (height-2*Margin -Math.abs((Data.length-1)*nDepthStep*Math.sin(degree)));
		
		if (nScaleMin < 0)
			YOffset =  -nScaleMin ;
		else 
			YOffset = 0;
		
		xRate = (double) (width-2*Margin - Math.abs((Data.length+2) * nDepthStep*Math.cos(degree)))/MaxCount;
	}
	
	
	public void drawAxis(GC gc, int x, int y, int width, int height) {
		 
		Color gray= getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);		
		Color black= getDisplay().getSystemColor(SWT.COLOR_BLACK);
		
		gc.setForeground(black);
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.drawRectangle(x+Margin, y+Margin, width-2*Margin, height-2*Margin);
		
		gc.setForeground(gray);
		gc.setLineStyle(SWT.LINE_DOT);
		
		double avg = (nScaleMax-nScaleMin)/10;
		int ys;
		
		for (int i=1;i<11;i++)
		{
			ys = height -(int)Math.round((YOffset + nScaleMin+i*avg)/yRate)- Margin;
			gc.drawLine((int)Margin,y+ys,x+width-Margin,y+ys);
			
			DecimalFormat   df   =new   java.text.DecimalFormat("#.00");  
			String strValue  = df.format(nScaleMin+i*avg);
			gc.drawString(strValue, Margin, ys,true);
		}
		avg = MaxCount / nXMarkNum;
		for (int i=0;i<nXMarkNum+1;i++)
		{
			ys = (int)Math.round((i*avg)*xRate);
			gc.drawLine(x+Margin+ys,y+Margin,x+Margin+ys,y+height-Margin);
			gc.drawString(String.valueOf((int)(i*avg*xScaleRate)),x+Margin+ys-20 , y+height-Margin,true);
		}
		
		
	}
	public void drawAxis3D(GC gc, int x, int y, int width, int height) {
		 		
		double avg = (nScaleMax-nScaleMin)/10;
		int ys;
		int deltX = (int)Math.abs(Math.round((Data.length+2)*nDepthStep*Math.cos(degree)));
		int deltY = (int)Math.abs(Math.round((Data.length+2)*nDepthStep*Math.sin(degree)));

		Color black= getDisplay().getSystemColor(SWT.COLOR_BLACK);
		Color gray= getDisplay().getSystemColor(SWT.COLOR_GRAY);
		int []p=new int[12];
		p[0]=Margin;p[1]=Margin+deltY;
		p[2]=Margin;p[3]=height-Margin;
		p[4]=width-Margin-deltX;p[5]=p[3];
		p[6]=width-Margin;p[7]=p[5]-deltY;
		p[8]=p[6];p[9]=Margin;
		p[10]=Margin+deltX;p[11]=p[9];
		gc.setForeground(black);
		gc.setBackground(gray);
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.drawPolygon(p);
		gc.fillPolygon(p);
		
		gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		gc.setLineStyle(SWT.LINE_DOT);
		// draw Vertical / scale
		
		for (int i=1;i<11;i++)
		{
			ys = height - (int)Math.round((YOffset+ nScaleMin+i*avg/yRate))-Margin;
			if (i<10) gc.drawLine((int)Margin,y+ys,Margin+deltX,y+ys-deltY);
			
			DecimalFormat   df   =new   java.text.DecimalFormat("##");  
			String strValue  = df.format(nScaleMin+i*avg);
			gc.drawString(strValue, 3, ys,true);
		}
		
		
		//draw Herizon / scale
		avg = MaxCount / nXMarkNum;
		for (int i=0;i<=nXMarkNum;i++)
		{
			ys = (int)Math.round((i*avg)*xRate);
			gc.drawLine(x+Margin+ys,y+height-Margin,
					x+Margin+ys+deltX,y+height-Margin-deltY);
			gc.drawString(String.valueOf((int)(i*avg*xScaleRate)),x+Margin+ys-20 ,
					y+height-Margin,true);
		}
		// draw back herizon scale
		avg = (nScaleMax-nScaleMin)/10;
		for (int i=1;i<10;i++)
		{
			ys = height - (int)Math.round((YOffset+ nScaleMin+i*avg)/yRate)-Margin;
			gc.drawLine((int)Margin+deltX,y+ys-deltY,x+width-Margin,y+ys-deltY);
		}
		
		// draw back vertical scale
		avg = MaxCount / nXMarkNum;
		for (int i=0;i<= nXMarkNum;i++)
		{
			ys = (int)Math.round((i*avg)*xRate);
			if (i==0)
			{
				gc.setLineStyle(SWT.LINE_SOLID);
				gc.setForeground(black);
				gc.drawLine(x+Margin+ys+deltX,y+Margin,x+Margin+ys+deltX,y+height-Margin-deltY);
				gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
				gc.setLineStyle(SWT.LINE_DOT);
			}
			gc.drawLine(x+Margin+ys+deltX,y+Margin,x+Margin+ys+deltX,y+height-Margin-deltY);
		}
		gc.setLineStyle(SWT.LINE_SOLID);

		
	}

	public void drawData(GC gc, int x, int y, int width, int height) {
		gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_BLUE));
		gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_WHITE));

		for (int j=Data.length-1;j>-1;j--)
		{
			int deltX = (int)Math.round((j+2)*nDepthStep*Math.cos(degree));
			int deltY = (int)Math.round((j+2)*nDepthStep*Math.sin(degree));
			//int DY = (int)Math.round(Data.length*nDepthStep*Math.sin(degree));
			
			
		int []p ;
		if (Data.length==1)
		{
			p = new int[Data[0].length];
			for (int i = 0; i < Data[0].length;i++)
			{
				p[i] = height - (int)Math.round((YOffset+Data[0][i])/ yRate) - Margin;
			}
			gc.setLineStyle(SWT.LINE_SOLID);

			for (int i = 0; i < Data[0].length-1;i++)
				gc.drawLine((int)(Margin + x+ (int)(i*xRate))+deltX, p[i]+deltY,
						Margin + x+(int)((i+1)*xRate)+deltX,p[i+1]+deltY);

		}
		else
		{
			p = new int[Data[j].length*2+4];
			p[0]=Margin + x + deltX;
			p[1]=height - Margin + deltY;
			for (int i = 0; i < Data[j].length;i++)
			{
				p[2*(i+1)]= (int)(Margin + x+ (int)Math.round(i*xRate))+deltX;
				p[2*(i+1)+1] = height - (int)Math.round((YOffset+Data[j][i])/ yRate) - Margin + deltY;
			}
			p[(Data[j].length+1)*2]= (int)(Margin + x+ (int)Math.round(Data[j].length*xRate))+deltX;;
			p[(Data[j].length+1)*2+1]=p[1];

			if (j==pos) 
			{
				gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_RED));
				gc.drawPolygon(p);
				gc.fillPolygon(p);
				gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_BLUE));

			}
			else
			{
				gc.drawPolygon(p);
				gc.fillPolygon(p);
			}
		}
		
		}
		
	}
	
	void DrawSelection(GC gc, int x, int y, int width, int height)
	{
		//gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_BLUE));
		//gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_WHITE));

		
		
		int deltX = (int)Math.round((pos+2)*nDepthStep*Math.cos(degree));
		int deltY = (int)Math.round((pos+2)*nDepthStep*Math.sin(degree));
			//int DY = (int)Math.round(Data.length*nDepthStep*Math.sin(degree));
			
		int []p = new int[Data[pos].length];
		for (int i = 0; i < Data[pos].length;i++)
		{
			p[i] = height - (int)Math.round((YOffset+Data[0][i])/ yRate) - Margin;
		}	

		gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_RED));
		for (int i = 0; i < Data[pos].length-1;i++)
			gc.drawLine((int)(Margin + x+ (int)(i*xRate))+deltX, p[i]+deltY,
					Margin + x+(int)((i+1)*xRate)+deltX,p[i+1]+deltY);
				//gc.fillPolygon(p);
		gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_BLUE));
		
	}

}
