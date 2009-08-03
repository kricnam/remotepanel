/**
 * 
 */
package com.bitcomm;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**
 * @author mxx
 * 
 */
public class AnalogMeter extends Canvas {

	/**
	 * @param parent
	 * @param style
	 */
	public int Height;

	public int Wide;

	public double nValue;

	public int nMinScale;

	public int nMaxScale;

	public int nMajorMark;

	public int nMinormark;

	public int nGaugeStartDegree;

	public int nScaleRadiusPercentage;

	public int nGaugeEndDegree;

	public int nScaleBarWidePercentage;

	public int nScaleRadius;

	public Point pointGaugeCenter;

	public RGB rgbMark;

	public FontData fontLable;

	public RGB rgbBK;

	public String strUnit;

	public AnalogMeter(Composite parent, int style) {
		super(parent, style);
		// TODO 自动生成构造函数存根
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				AnalogMeter.this.paintControl(e);
			}
		});
		nGaugeStartDegree = 120;
		nGaugeEndDegree = 420;

		nMinScale = 0;
		nMaxScale = 180;
		nValue = 60.5;
		rgbBK = new RGB(172, 178, 179);
		strUnit = "nGy/h";

	}

	void drawScale(GC gc) {
		if (pointGaugeCenter == null) {
			pointGaugeCenter = this.getSize();
			pointGaugeCenter.x /= 2;
			pointGaugeCenter.y /= 2;
			nScaleRadius = Math.min(pointGaugeCenter.x, pointGaugeCenter.y) * 90 / 100;
			nScaleBarWidePercentage = 3;
			nScaleRadiusPercentage = 90;
			nMinormark = 50;
			nMajorMark = 10;
		}
		int n;
		double avg;
		int x0;
		int x1;
		int y0;
		int y1;
		int r0;
		int r1;
		int r;
		int N;
		int mark;
		double angle;
		int ratio;

		ratio = nMinormark / nMajorMark;

		avg = (nGaugeEndDegree - nGaugeStartDegree) / nMinormark;
		r0 = nScaleRadius * (100 - nScaleBarWidePercentage) / 100;
		r1 = nScaleRadius * (100 - nScaleBarWidePercentage * 2) / 100;

		N = (nMaxScale - nMinScale) / nMajorMark;

		for (n = 0; n < nMinormark + 1; n = n + 1) {
			angle = ((nGaugeStartDegree + n * avg) / 180) * Math.PI;
			if (n % ratio == 0)
				r = r1;
			else
				r = r0;

			x0 = (int) (r * Math.cos(angle)) + pointGaugeCenter.x;
			y0 = (int) (r * Math.sin(angle)) + pointGaugeCenter.y;
			x1 = (int) (nScaleRadius * Math.cos(angle)) + pointGaugeCenter.x;
			y1 = (int) (nScaleRadius * Math.sin(angle)) + pointGaugeCenter.y;
			gc.drawLine(x0, y0, x1, y1);
			if (r == r1) {
				mark = nMinScale + n * N / ratio;
				Point t = gc.stringExtent(String.valueOf(mark));
				int dx = 0;
				int dy = 0;

				if (x0 > x1 && y0 > y1) {
					dx = 0;
					dy = 0;
				}
				if (x0 < x1 && y0 > y1) {
					dx = -t.x;
				}
				if (x0 > x1 && y0 < y1) {

					dy = -t.y;
				}
				if (x0 < x1 && y0 < y1) {
					dx = -t.x;
					dy = -t.y;
				}
				if (x0 == x1) {
					if (y0 < y1)
						dy = -t.y;
					dx = -t.x / 2;
				}
				if (y0 == y1) {
					if (x0 < x1)
						dx = -t.x;
					dy = -t.y / 2;
				}

				gc.drawString(String.valueOf(mark), x0 + dx, y0 + dy, true);
			}
		}

	}

	void paintControl(PaintEvent e) {
		GC gc = e.gc;
		Point size = getSize();
		drawBackground(gc, 0, 0, size.x, size.y);
		drawDigiValue(gc);
		drawPoint(gc);

	}

	void drawDigiValue(GC gc) {
		FontData fdata = new FontData("NI7SEG", 16, SWT.NORMAL);
		RGB rgb1 = new RGB(222, 231, 214);
		RGB rgb2 = new RGB(22, 22, 22);

		Color bkColor = new Color(gc.getDevice(), rgb1);
		Color fgColor = new Color(gc.getDevice(), rgb2);
		Font ft = new Font(gc.getDevice(), fdata);
		gc.setBackground(bkColor);
		gc.setForeground(fgColor);
		gc.setFont(ft);
		
		Point p = gc.stringExtent("+888.8");
		gc.fillRectangle(pointGaugeCenter.x - p.x / 2, pointGaugeCenter.y-nScaleRadius/2, p.x, p.y);
		String str = String.valueOf(nValue);
		Point pt = gc.stringExtent(str);

		gc.drawString(str, pointGaugeCenter.x - p.x / 2 + (p.x - pt.x),
				pointGaugeCenter.y - nScaleRadius/2);
		bkColor.dispose();
		fgColor.dispose();
		ft.dispose();
	}

	void drawPoint(GC gc) {
		double valueAngle;
		int valueR;
		valueR = nScaleRadius * (100 - nScaleBarWidePercentage * 4) / 100;

		valueAngle = ((nValue > nMinScale) ? nValue - nMinScale : nMinScale)
				* (nGaugeEndDegree - nGaugeStartDegree)
				/ (nMaxScale - nMinScale) + nGaugeStartDegree;
		valueAngle = valueAngle / 180 * Math.PI;

		gc.drawOval(pointGaugeCenter.x - 15, pointGaugeCenter.y - 15, 30, 30);
		Color red = this.getDisplay().getSystemColor(SWT.COLOR_RED);

		gc.setBackground(red);
		gc.fillOval(pointGaugeCenter.x - 15, pointGaugeCenter.y - 15, 30, 30);
		gc.setForeground(red);
		gc.setLineWidth(4);
		gc.drawLine(pointGaugeCenter.x, pointGaugeCenter.y, pointGaugeCenter.x
				+ (int) (valueR * Math.cos(valueAngle)), pointGaugeCenter.y
				+ (int) (valueR * Math.sin(valueAngle)));
		gc.setLineWidth(1);

	}

	@Override
	public void drawBackground(GC gc, int x, int y, int width, int height) {
		// TODO 自动生成方法存根
		super.drawBackground(gc, x, y, width, height);
		
		RGB rgb0 = new RGB(100, 100, 100);
		RGB rgb1 = new RGB(180, 200, 200);
		RGB rgb2 = new RGB(250, 250, 230);
		
		Color bkColor = new Color(gc.getDevice(), rgb1);
		Color bkColor0 = new Color(gc.getDevice(), rgb0);
		Color fgColor = new Color(gc.getDevice(), rgb2);
		
		gc.setBackground(bkColor);
		gc.setForeground(bkColor0);
		
		gc.fillGradientRectangle(x, y, width, height, true);
		
		gc.setBackground(bkColor);
		gc.setForeground(fgColor);
	
		drawScale(gc);
		gc.drawString("uGy/h", pointGaugeCenter.x - 15,
						pointGaugeCenter.y + 30);
		
		bkColor.dispose();
		fgColor.dispose();
		bkColor0.dispose();

	}

	/*
	 * （非 Javadoc）
	 * 
	 * @see org.eclipse.swt.widgets.Control#setSize(int, int)
	 */
	@Override
	public void setSize(int width, int height) {
		// TODO 自动生成方法存根
		super.setSize(width, height);
	}
}
