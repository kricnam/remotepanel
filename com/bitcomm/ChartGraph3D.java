package com.bitcomm;


import java.text.DecimalFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;



public class ChartGraph3D extends Canvas {
	class scale {
		public scale(int count2, double div2) {
			count=count2;
			div = div2;
		}
		int  count;
		double div;
	};
	double [][]Data;
	String []Index;
	double nMaxData;
	double nMinData;
	double yRate;
	double xRate;
	double yLogRate;
	//double YLogOffset;
	double nScaleLogMax;
	double nScaleLogMin;
	
	//double YOffset;
	double Xmin;
	double Xmax;
	int Margin;
	int MaxCount;
	double nScaleMax;
	double nScaleMin;
	double xScaleRate;  // 
	int nXMarkNum;
	Label lblROI;
	int nYMarkNum;
	int pos;
	boolean logScale;
	int nDepthStep;
	double degree;
	boolean UpDateSelect;
	boolean bSwitch;
	boolean bMutilty;
	int HCursor1;
	int HCursor2;
	int nROI;
	boolean HCursor1Active;
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
		//YOffset = 0;
		MaxCount = 0;
		xScaleRate=1;
		nXMarkNum = 0;
		nYMarkNum = 10;
		nDepthStep = 1;
		degree = -Math.PI / 4;
		pos = 0;
		UpDateSelect =false;
		bSwitch = false;
		bMutilty = false;
		HCursor1 = 0;
		HCursor2 = 999;
		HCursor1Active = true;
		addMouseListener(new MouseListener() {
			
			public void mouseUp(MouseEvent arg0) {
			}
			public void mouseDown(MouseEvent arg0) {
				   if (!bMutilty) return;
				   int posM = (int)Math.round((arg0.x - Margin)/xRate);
				   if (Math.abs(HCursor1-posM)<3)
				   {
					   HCursor1Active = true;
					   SpectrumView a=(SpectrumView) getData();
					   a.butLeft.setSelection(true);
					   a.butRight.setSelection(false);
				   }
				   else if (Math.abs(HCursor2-posM)<3)
				   {
					   HCursor1Active = false;
					   SpectrumView a=(SpectrumView) getData();
					   a.butLeft.setSelection(false);
					   a.butRight.setSelection(true);
				   }
				   SetROI();
				   getParent().update();
			}
			public void mouseDoubleClick(MouseEvent arg0) {
				if (!bMutilty) return;
				if (HCursor1Active)
					HCursor1 = (int)Math.round((arg0.x - Margin)/xRate);
				else
					HCursor2 = (int)Math.round((arg0.x - Margin)/xRate);
				if (HCursor1>999) HCursor1 = 999;
				if (HCursor2>999) HCursor2 = 999;
				if (HCursor1<0) HCursor1 = 0;
				if (HCursor2<0) HCursor2 = 0;
				SetROI();
				lblROI.setText(String.valueOf(nROI));
				lblROI.getParent().layout();
				redraw();
			}
		});
	}
	
	void SetROI()
	{
		if (Data==null) return;
		int m=HCursor1;
		int n=HCursor2;
		if (m>n) {m = n; n = HCursor1;}
		int i;
		int sum = 0;
		for(i=m;i<=n;i++)
		{
			sum+=Data[pos][i];
		}
		nROI = sum;
	}
	
	void paintControl(PaintEvent e) {
		GC gc = e.gc;
		setBackground(gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
		
		if (Data==null) return;
		Point size = getSize();
		
		if (Data!=null)
		{
			if (nMaxData==nMinData) AutoSetTransform(gc,0,0,size.x,size.y);
			else SetTransform(0,0,size.x,size.y);
		}
		
		gc.setAntialias(SWT.ON);
		gc.setAdvanced(true);
		
		drawBackground(gc, 0, 0, size.x, size.y);
		
		drawData(gc, 0, 0,size.x, size.y);
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
	
	void setAutoTransform(boolean bResetScale)
	{
		nMaxData = 0;
		nMinData = nMaxData;
		if (bResetScale)
		{
			nScaleMax = 0;
			nScaleMin = nScaleMax;
		}
		MaxCount = 0;
	
	}

	/* （非 Javadoc）
	 * @see org.eclipse.swt.widgets.Canvas#drawBackground(org.eclipse.swt.graphics.GC, int, int, int, int)
	 */
	
	public void drawBackground(GC gc, int x, int y, int width, int height) {
		
		super.drawBackground(gc, x, y, width, height);
		if (Data==null) return;
		if (Data.length>1&& bSwitch && bMutilty) 
		{
				drawAxis(gc, x, y, width, height);
				return;
		}
		
		if (Data.length>1 && !bSwitch) drawAxis3D(gc, x, y, width, height);
		else drawAxis(gc, x, y, width, height);
	}

	public void AutoSetTransform(GC gc,int x, int y, int width, int height)
	{
		if (Data==null) return;
		Point pt = gc.stringExtent("000000");
		//System.out.println("MArgin:"+String.valueOf(Margin));
		Margin = Math.min(Margin,Math.max((int)(width * 0.04),(int)(height * 0.04)));
		//System.out.println("MArgin:"+String.valueOf(Margin));
		Margin = Math.max(Margin, pt.x*2);
		//System.out.println("MArgin:"+String.valueOf(Margin));
		
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
			
		nDepthStep = height / 2 /(Data.length+2);
		if (nDepthStep<2) nDepthStep=2;
		SetTransform(x,y,width,height);

	}

	public void SetTransform(int x, int y, int width, int height)
	{
		if (nScaleMax == nScaleMin)
		{
			nScaleMax = ((int)Math.ceil(nMaxData/1000))*1000;
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
		
		if (Data.length>1 && !bSwitch )
		{
			yRate = nRange  / (height-2*Margin -Math.abs((Data.length+2)*nDepthStep*Math.sin(degree)));
			yLogRate = nLogRange / (height-2*Margin-Math.abs((Data.length+2)*nDepthStep*Math.sin(degree)));
		}
		else
		{
			yRate = nRange  / (height-2*Margin );
			yLogRate = nLogRange / (height-2*Margin);
		}
		
		//if (nScaleMin < 0)
		//	YOffset =  -nScaleMin ;
		//else 
		//	YOffset = 0;
		
		if (Data.length>1 && !bSwitch)
			xRate = (double) (width-2*Margin - Math.abs((Data.length+2) * nDepthStep*Math.cos(degree)))/MaxCount;
		else
			xRate = (double) (width-2*Margin)/MaxCount;
	}
	
	
	public void drawAxis(GC gc, int x, int y, int width, int height) {
		 
		Color gray= getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);		
		Color black= getDisplay().getSystemColor(SWT.COLOR_BLACK);
		
		gc.setForeground(black);
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.drawRectangle(x+Margin, y+Margin, width-2*Margin, height-2*Margin);
		
		gc.setForeground(gray);
		gc.setLineStyle(SWT.LINE_DOT);
		
		double avg ;
		int ys;
		if (logScale)
		{
			drawLogAxisY(gc, x, y, width, height);
		}
		else
		{
			drawAxisY(gc, x, y, width, height);
		}
		//X axis
		avg = MaxCount / nXMarkNum;
		//System.out.println(nXMarkNum);
		Point pt;
		String strT;
		for (int i=0;i<nXMarkNum+1;i++)
		{
			ys = (int)Math.round((i*avg-1)*xRate);
			gc.drawLine(x+Margin+ys,y+Margin,x+Margin+ys,y+height-Margin);
			
			strT = String.valueOf((int)(i*avg*xScaleRate));
			pt = gc.stringExtent(strT);
			if (i>0)
			{
				int n = pt.x/(int)avg;
				if (n>0) n=((i-1)%n)*pt.y;
				gc.drawString(strT,x+Margin+ys-pt.x/2 , y+height-Margin+n,true);
			}
		}
	}
	
	scale CalculateAxisYScale()
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
		
		return new scale(count,div);
	}
	
	public void drawAxisY(GC gc, int x, int y, int width, int height) 
	{
		scale sc = CalculateAxisYScale();
		int ys;
		//Y axis
		for (int i=0;i<sc.count+1;i++)
		{
			double ceil = (((int)nScaleMin/(int)sc.div)+i)*(int)sc.div;
			if (nScaleMin%sc.div > 0) ceil+=sc.div;
			
			ys = height - (int)Math.round(( ceil - nScaleMin)/yRate) -Margin;
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
		if (nLogScaleCount<2)
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
	
	public void drawAxis3D(GC gc, int x, int y, int width, int height) {

		double avg = (nScaleMax-nScaleMin)/10;
		int ys;
		int deltX = (int)Math.abs(Math.round((Data.length+2)*nDepthStep*Math.cos(degree)));
		int deltY = (int)Math.abs(Math.round((Data.length+2)*nDepthStep*Math.sin(degree)));

		Color black= getDisplay().getSystemColor(SWT.COLOR_BLACK);
		Color gray= getDisplay().getSystemColor(SWT.COLOR_GRAY);
		int []p=new int[12];
		p[0]=x+Margin;p[1]=y+Margin+deltY;
		p[2]=x+Margin;p[3]=y+height-Margin;
		p[4]=x+width-Margin-deltX;p[5]=p[3];
		p[6]=x+width-Margin;p[7]=p[5]-deltY;
		p[8]=p[6];p[9]=y+Margin;
		p[10]=x+Margin+deltX;p[11]=p[9];
		gc.setForeground(black);
		gc.setBackground(gray);
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.drawPolygon(p);
		gc.fillPolygon(p);

		gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		gc.setLineStyle(SWT.LINE_DOT);
		// draw Vertical / - scale
		// draw back horizon scale
		if (logScale)
		{
			int nLogScaleCount = (int)Math.floor(nScaleLogMax) 
								- (int)Math.ceil(nScaleLogMin)+1; 
			
			for (int i=0;i<nLogScaleCount;i++)
			{
				double ceil = Math.ceil(nScaleLogMin+i);
				ys = height - (int)Math.round((ceil - nScaleLogMin)/yLogRate)-Margin;
				if (y+ys-deltY < Margin) continue; 
				gc.drawLine((int)Margin+x,y+ys,Margin+deltX+x,y+ys-deltY);
				gc.drawLine((int)Margin+deltX+x,y+ys-deltY,x+width-Margin,y+ys-deltY);

				DecimalFormat   df;
				if (ceil >= 1) df= new   java.text.DecimalFormat("#");
				else df = new java.text.DecimalFormat("#0.0#");
				String strValue  = df.format(Math.pow(10, ceil));
				Point pt=gc.stringExtent(strValue);
				gc.drawString(strValue, Margin-pt.x-3+x, ys+y-pt.y/2,true);
			}	
		}
		else
		{
			scale sc = CalculateAxisYScale();
//			System.out.println("div="+String.valueOf(sc.div)+",count="+String.valueOf(sc.count));
			for (int i=0;i<sc.count+1;i++)
			{
				double ceil = (((int)nScaleMin/(int)sc.div)+i)*(int)sc.div;
				if (nScaleMin%sc.div > 0) ceil+=sc.div;
				
				ys = height - (int)Math.round((ceil - nScaleMin)/yRate)-Margin;
				if (y+ys-deltY < Margin) continue;
				gc.drawLine((int)Margin+x,y+ys,Margin+deltX+x,y+ys-deltY);
				gc.drawLine((int)Margin+deltX+x,y+ys-deltY,x+width-Margin,y+ys-deltY);
				DecimalFormat   df   =new   java.text.DecimalFormat("#");  
				String strValue  = df.format(ceil);
				Point pt=gc.stringExtent(strValue);
				gc.drawString(strValue, Margin-pt.x-3+x, ys+y-pt.y/2,true);
			}
		}

		//draw Horizon / scale
		avg = MaxCount / nXMarkNum;
		
		int intv = (int)(avg*xRate);
		int n=0;
		for (int i=0;i<=nXMarkNum;i++)
		{
			ys = (int)Math.round((i*avg)*xRate);
			gc.drawLine(x+Margin+ys,y+height-Margin,
					x+Margin+ys+deltX,y+height-Margin-deltY);
			String str = String.valueOf((int)(i*avg*xScaleRate));
			Point pt = gc.stringExtent(str);
			if (i>0) 
			{
				n = pt.x/intv+1;
				if (n>1) n=((i-1)%2)*pt.y;
				gc.drawString(str,x+Margin+ys-pt.x/2 ,y+height-Margin+n,true);
			}
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
		if (Data==null) return;
		
		gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
		
		Point pt;
		if (bMutilty && bSwitch)
		{
			drawMultiData2D(gc, x, y, width, height);
			return;
		}
		//draw title
		gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_BLUE));
		if (Index!=null && pos < Index.length)
		{
			
			if (Index[pos]!=null)
			{
				pt = gc.stringExtent(Index[pos]);
				gc.drawString(Index[pos], x+Margin/2, y+Margin/2-pt.y);
			}
			else
			{
				pt = gc.stringExtent("---");
				gc.drawString("---", x+Margin/2, y+Margin/2-pt.y);
			}
				
			
			String strT;
			if (logScale) strT="-LOG-";
			else strT="-LIN-";
			pt=gc.stringExtent(strT);
			gc.drawString(strT, x+width/2-pt.x/2, y+Margin/2-pt.y);
		}
		
		int dX = (int)Math.abs(Math.round((Data.length+2)*nDepthStep*Math.cos(degree)));
		int dY = (int)Math.abs(Math.round((Data.length+2)*nDepthStep*Math.sin(degree)));
		
		pt=gc.stringExtent("Counts");
		gc.drawString("Counts", x+Margin-pt.x, y+dX+Margin-2*pt.y);
		pt=gc.stringExtent("Channel");
		gc.drawString("Channel", x+width-dY- Margin +pt.x/6, y+height-Margin+pt.y);
		
		if ((bSwitch && Data.length > 1)||Data.length==1)
		{
			DrawSelection(gc, Data[pos], x, y, width, height);
			return;
		}
		for (int j = Data.length - 1; j > -1; j--) {
			int deltX = (int) Math.round((j + 2) * nDepthStep
					* Math.cos(degree));
			int deltY = (int) Math.round((j + 2) * nDepthStep
					* Math.sin(degree));

			int[] p;
			 
			{
				p = new int[Data[j].length * 2 + 4];
				p[0] = Margin + x + deltX;
				p[1] = height - Margin + deltY +y;
				if (logScale)
				{
					for (int i = 0; i < Data[j].length; i++) {
						p[2 * (i + 1)] = (int) (Margin + x + (int) Math.round(i
								* xRate))
								+ deltX;
						if (Data[j][i]<nScaleMin) 
						{
							p[2 * (i + 1) + 1] = height - Margin + deltY +y;
							continue;
						}
						if (Data[j][i]>nScaleMax) 
						{
							p[2 * (i + 1) + 1] = height -(int) Math.round(( nScaleLogMax-nScaleLogMin) / yLogRate) 
							- Margin + deltY +y;
							continue;
						}
						p[2 * (i + 1) + 1] = height
						- (int) Math.round(( Math.log10(Data[j][i])-nScaleLogMin) / yLogRate)
						- Margin + deltY +y;
					}
					p[(Data[j].length + 1) * 2] = (int) (Margin + x + (int) Math
							.round(Data[j].length * xRate))
							+ deltX;
					;
					p[(Data[j].length + 1) * 2 + 1] = p[1];
				}
				else
				{
					for (int i = 0; i < Data[j].length; i++) {
						p[2 * (i + 1)] = (int) (Margin + x + (int) Math.round(i
								* xRate))
								+ deltX;
						if (Data[j][i]<nScaleMin) 
						{
							p[2 * (i + 1) + 1] = height - Margin + deltY +y;
							continue;
						}
						if (Data[j][i]>nScaleMax) 
						{
							p[2 * (i + 1) + 1] = height - (int) Math.round(( nScaleMax - nScaleMin) / yRate)
							- Margin + deltY +y;
							continue;
						}
						p[2 * (i + 1) + 1] = height
						- (int) Math.round(( Data[j][i] - nScaleMin) / yRate)
						- Margin + deltY + y;
					}
					p[(Data[j].length + 1) * 2] = (int) (Margin + x + (int) Math
							.round(Data[j].length * xRate))
							+ deltX;
					;
					p[(Data[j].length + 1) * 2 + 1] = p[1];
				}

				if (j == pos) {
					gc.setForeground(gc.getDevice().getSystemColor(
							SWT.COLOR_RED));
					gc.drawPolygon(p);
					gc.fillPolygon(p);
					gc.setForeground(gc.getDevice().getSystemColor(
							SWT.COLOR_BLUE));

				} else {
					gc.drawPolygon(p);
					if (j % 6==0) gc.fillPolygon(p);
				}
			}

		}

	}
	void drawMultiData2D(GC gc,int x, int y, int width, int height)
	{
		gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
		gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_BLUE));
		Point pt ;
		String strT;
		int nCur1Y=0;
		if (logScale) strT="-LOG-";
		else strT="-LIN-";
		pt=gc.stringExtent(strT);
		gc.drawString(strT, x+pt.x/2, y+Margin/2-pt.y);
		pt=gc.stringExtent("Counts");
		gc.drawString("Counts", x+Margin-pt.x, y+Margin-2*pt.y);
		pt=gc.stringExtent("Channel");
		gc.drawString("Channel", x+width-Margin+pt.x/6, y+height-Margin+pt.y);

		//Draw ROI
		String strROI = "ROI:"+ String.valueOf(nROI);
		pt = gc.stringExtent(strROI);
		//gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_BLACK));
		gc.setForeground(getColor(pos));
		gc.drawString(strROI,x+width/2-(pt.x/2), y+Margin-pt.y-3);

		boolean bTwoCol=true;
		for(int i=(pos+1)%Data.length;i<Data.length;i=(i+1)%Data.length)
		{
			//System.out.println(i);
			gc.setForeground(getColor(i));
			if (Index[i]==null)
			{
				pt = gc.stringExtent("---");
				gc.drawString("---", x+Margin/2, y+Margin/2-pt.y);
			}
			else
			{
				String strPri;
				String strV;
				int v1 = (int)Data[i][HCursor1];
				int v2 = (int)Data[i][HCursor2];
				if (i==pos) strPri="*";
				else strPri=" ";
				strV=String.format("(%d,%d),(%d,%d)",HCursor1+1, v1,HCursor2+1,v2);
				strPri+=Index[i]+" "+strV;
				pt = gc.stringExtent(strPri);
				if (i==pos+1 && 2*pt.x>(width-2*Margin)) bTwoCol = false;
				if (bTwoCol)
				{
					if (i<3) gc.drawString(strPri, x+Margin, y+(i+1)*pt.y);
					else gc.drawString(strPri, x+width/2, y+(i-2)*pt.y);
				}
				else
					gc.drawString(strPri, x+Margin, y+(i+1)*pt.y);
			}

			

			int []p = new int[Data[i].length];
			if (logScale) {
				for (int n = 0; n < Data[i].length; n++) {
					if (Math.log10(Data[i][n])<nScaleLogMin)
					{
						p[n]=height- Margin;
					}
					else if (Math.log10(Data[i][n])>nScaleLogMax)
					{
						p[n]= Margin;
					}
					else
					{
						p[n] = height
						- (int) Math.round(( Math.log10(Data[i][n])-nScaleLogMin)/ yLogRate) 
						- Margin ;
					}
					if (n==HCursor1) nCur1Y=Math.min(nCur1Y, p[n]);
				}
			} else {
				for (int n = 0; n < Data[i].length; n++) {
					if (Data[i][n]<nScaleMin)
					{
						p[n]=height- Margin;
					}
					else if (Data[i][n]>nScaleMax)
					{
						p[n]= Margin;
					}
					else
					{
						p[n] = height
						- (int) Math.round((Data[i][n]-nScaleMin)/ yRate)
						- Margin;
					}
					if (n==HCursor1) nCur1Y=Math.min(nCur1Y, p[n]);
				}
			}
			gc.setLineStyle(SWT.LINE_SOLID);
			

			for (int n = 0; n < Data[i].length - 1; n++)
				gc.drawLine((int) (Margin + x + (int) (n * xRate)) ,
						p[n]  +y, 
						Margin + x + (int) ((n + 1) * xRate), 
						p[n + 1]  +y); //+ deltY + deltX
			
			if (i==pos) 
			{
				gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_BLACK));
				String str=String.valueOf(HCursor1+1);
				pt = gc.stringExtent(str);
				gc.drawLine((int)(Margin+x+Math.round(HCursor1*xRate)), Margin+y,
						(int)(Margin+x+Math.round(HCursor1*xRate)),height+y-Margin);
				gc.drawString(str, (int)(Margin+x+HCursor1*xRate), Margin+2+y);
				str = String.valueOf(HCursor2+1);
				pt = gc.stringExtent(str);
				gc.drawLine((int)(Margin+x+HCursor2*xRate), Margin+y,
						(int)(Margin+x+HCursor2*xRate),height+y-Margin);
				if ( (int)(((HCursor2-HCursor1)*xRate)) < 2*pt.x && HCursor1 < HCursor2 )
					gc.drawString(str, (int)(Margin+x+HCursor2*xRate)-pt.x+2, y+Margin+2+pt.y);
				else
					gc.drawString(str, (int)(Margin+x+HCursor2*xRate)-pt.x+2, y+Margin+2);
				return;
			}
		}
	}
	
	private Color getColor(int i) {
		switch(i)
		{
		case 0:
			return getDisplay().getSystemColor(SWT.COLOR_BLUE);
		case 1:
			return getDisplay().getSystemColor(SWT.COLOR_DARK_CYAN);
		case 2:
			return getDisplay().getSystemColor(SWT.COLOR_GREEN);
		case 3:
			return getDisplay().getSystemColor(SWT.COLOR_DARK_MAGENTA);
		case 4:
			return getDisplay().getSystemColor(SWT.COLOR_RED);
		default:
			return getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE);
		}
	}
	void DrawSelection(GC gc, double[] sdata,int x, int y, int width, int height)
	{
		gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_BLUE));
		gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
		if (Index!=null && pos < Index.length)
		{
			Point pt ;
			if (Index[pos]==null)
			{
				pt = gc.stringExtent("---");
				gc.drawString("---", x+Margin/2, y+Margin/2-pt.y);
			}
			else
			{
				pt = gc.stringExtent(Index[pos]);
				gc.drawString(Index[pos], x+Margin/2, y+Margin/2-pt.y);
			}
			
			String strT;
			if (logScale) strT="-LOG-";
			else strT="-LIN-";
			pt=gc.stringExtent(strT);
			gc.drawString(strT, x+width/2-pt.x/2, y+Margin/2-pt.y);
		}
		int []p = new int[sdata.length];
		if (logScale) {
			for (int i = 0; i < sdata.length; i++) {
				if (Math.log10(sdata[i])<nScaleLogMin)
				{
					p[i]=height- Margin;
				}
				else if (Math.log10(sdata[i])>nScaleLogMax)
				{
					p[i]= Margin;
				}
				else
				{
					p[i] = height
						- (int) Math.round(( Math.log10(sdata[i])-nScaleLogMin)/ yLogRate) 
						- Margin ;
				}
			}
		} else {
			for (int i = 0; i < sdata.length; i++) {
				if (sdata[i]<nScaleMin)
				{
					p[i]=height- Margin;
				}
				else if (sdata[i]>nScaleMax)
				{
					p[i]= Margin;
				}
				else
				{
					p[i] = height
						- (int) Math.round((sdata[i]-nScaleMin)/ yRate)
						- Margin;
				}
			}
		}
		gc.setLineStyle(SWT.LINE_SOLID);

		for (int i = 0; i < sdata.length - 1; i++)
			gc.drawLine((int) (Margin + x + (int) (i * xRate)) ,
					p[i]  +y, 
					Margin + x + (int) ((i + 1) * xRate), 
					p[i + 1]  +y); //+ deltY + deltX


	}
	
	void ChangeCur(int i)
	{
		if (Data==null) return;
		if (HCursor1Active)
		{	
		HCursor1+=i;
		if (HCursor1 < 0)
			HCursor1 = 999;
		if (HCursor1 > 999)
			HCursor1 = 0;
		}
		else
		{
			HCursor2+=i;
			if (HCursor2 < 0)
				HCursor2 = 999;
			if (HCursor2 > 999)
				HCursor2 = 0;
		}
		SetROI();
		redraw();
	}


}
