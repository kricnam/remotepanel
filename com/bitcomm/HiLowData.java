/**
 * 
 */
package com.bitcomm;
/**
 * @author mxx
 *
 */
public class HiLowData {
	public enum ValueUnit {
		non,
		nGyh,
		uGyh,
		mGyh
	};
	short DataLength;
	char DataNum;
	DateTime date;
	byte cNaIUnit;
	int  nNaIValue;
	int  nNaI_dr_count;
	int  nNaI_cr_count;
	byte cSSDUnit;
	int  nSSDrate;
	int  nSSD_dr_count;
	byte cPT;
	short nMTime;
	int  nStatus;
	short nHVVolt;
	short nThermoral;
	short nVoltage;
	DateTime dateGPS;
	byte lgDegree;
	byte lgMinute;
	short lgSecond10;
	byte laDegree;
	byte laMinute;
	short laSecond10;
	short nAltitude;
	byte nSatlite;
	byte nGeoSys;
	byte cFOMA;
	byte cValidType;
	enum ValidType
	{
		none,
		NaI,
		SSD
	};
	int ToChar(byte Hi,byte Low)
	{
		return Hi << 8 + Low;
	}
	int ToChar(byte[] data, int start)
	{
		return data[start] << 8 + data[start+1];
	}
	int ToInt(byte Hi1,byte Hi2,byte Low1,byte Low2)
	{
		return  Hi1 << 24 + Hi2 << 16 + Low1 << 8 + Low2;
	}
	int ToInt(byte[] data,int start)
	{
		return  data[start]<<24 + data[start+1] << 16 
		+ data[start+2] << 8 + data[start+3];
	}

	HiLowData(byte[] Data)
	{
		DataLength =(short) ToChar(Data,5);
		DataNum = (char) ToChar(Data,7);
		date.year = (short)ToChar(Data,9);
		date.month = Data[11];
		date.day = Data[12];
		date.hour = Data[13];
		date.minute = Data[14];
		cNaIUnit = Data[15];
		nNaIValue = ToInt(Data,16);
		nNaI_dr_count = ToInt(Data,20);
		nNaI_cr_count = ToInt(Data,24);
		cSSDUnit = Data[28];
		nSSDrate = ToInt(Data,29);
		nSSD_dr_count = ToInt(Data,33);
		cPT = Data[34];
		nMTime = (short)ToChar(Data,38);
		nStatus = ToInt(Data,40);
		nHVVolt = (short)ToChar(Data,44);
		nThermoral = (short)ToChar(Data,46);
		nVoltage = (short)ToChar(Data,48);
		dateGPS.year = (short)ToChar(Data,50);
		dateGPS.month = Data[52];
		dateGPS.day = Data[53];
		dateGPS.hour = Data[54];
		dateGPS.minute = Data[55];
		lgDegree = Data[56];
		lgMinute = Data[57];
		lgSecond10 = (short)ToChar(Data, 58);
		laDegree = Data[60];
		laMinute = Data[61];
		laSecond10 = (short)ToChar(Data,62);
		nAltitude = (short)ToChar(Data,64);
		nSatlite = Data[66];
		nGeoSys = Data[67];
		cFOMA = Data[68];
		cValidType = Data[69];
	}
	/**
	 * @return cFOMA
	 */
	public byte getCFOMA() {
		return cFOMA;
	}
	/**
	 * @param cfoma 要设置的 cFOMA
	 */
	public void setCFOMA(byte cfoma) {
		cFOMA = cfoma;
	}
	/**
	 * @return cNaIUnit
	 */
	public byte getCNaIUnit() {
		return cNaIUnit;
	}
	/**
	 * @param naIUnit 要设置的 cNaIUnit
	 */
	public void setCNaIUnit(byte naIUnit) {
		cNaIUnit = naIUnit;
	}
	/**
	 * @return cPT
	 */
	public byte getCPT() {
		return cPT;
	}
	/**
	 * @param cpt 要设置的 cPT
	 */
	public void setCPT(byte cpt) {
		cPT = cpt;
	}
	/**
	 * @return cSSDUnit
	 */
	public byte getCSSDUnit() {
		return cSSDUnit;
	}
	/**
	 * @param unit 要设置的 cSSDUnit
	 */
	public void setCSSDUnit(byte unit) {
		cSSDUnit = unit;
	}
	/**
	 * @return cValidType
	 */
	public byte getCValidType() {
		return cValidType;
	}
	/**
	 * @param validType 要设置的 cValidType
	 */
	public void setCValidType(byte validType) {
		cValidType = validType;
	}
	/**
	 * @return dataLength
	 */
	public short getDataLength() {
		return DataLength;
	}
	/**
	 * @param dataLength 要设置的 dataLength
	 */
	public void setDataLength(short dataLength) {
		DataLength = dataLength;
	}
	/**
	 * @return dataNum
	 */
	public char getDataNum() {
		return DataNum;
	}
	/**
	 * @param dataNum 要设置的 dataNum
	 */
	public void setDataNum(char dataNum) {
		DataNum = dataNum;
	}
	/**
	 * @return date
	 */
	public DateTime getDate() {
		return date;
	}
	/**
	 * @param date 要设置的 date
	 */
	public void setDate(DateTime date) {
		this.date = date;
	}
	/**
	 * @return dateGPS
	 */
	public DateTime getDateGPS() {
		return dateGPS;
	}
	/**
	 * @param dateGPS 要设置的 dateGPS
	 */
	public void setDateGPS(DateTime dateGPS) {
		this.dateGPS = dateGPS;
	}
	/**
	 * @return laDegree
	 */
	public byte getLaDegree() {
		return laDegree;
	}
	/**
	 * @param laDegree 要设置的 laDegree
	 */
	public void setLaDegree(byte laDegree) {
		this.laDegree = laDegree;
	}
	/**
	 * @return laMinute
	 */
	public byte getLaMinute() {
		return laMinute;
	}
	/**
	 * @param laMinute 要设置的 laMinute
	 */
	public void setLaMinute(byte laMinute) {
		this.laMinute = laMinute;
	}
	/**
	 * @return laSecond10
	 */
	public short getLaSecond10() {
		return laSecond10;
	}
	/**
	 * @param laSecond10 要设置的 laSecond10
	 */
	public void setLaSecond10(short laSecond10) {
		this.laSecond10 = laSecond10;
	}
	/**
	 * @return lgDegree
	 */
	public byte getLgDegree() {
		return lgDegree;
	}
	/**
	 * @param lgDegree 要设置的 lgDegree
	 */
	public void setLgDegree(byte lgDegree) {
		this.lgDegree = lgDegree;
	}
	/**
	 * @return lgMinute
	 */
	public byte getLgMinute() {
		return lgMinute;
	}
	/**
	 * @param lgMinute 要设置的 lgMinute
	 */
	public void setLgMinute(byte lgMinute) {
		this.lgMinute = lgMinute;
	}
	/**
	 * @return lgSecond10
	 */
	public short getLgSecond10() {
		return lgSecond10;
	}
	/**
	 * @param lgSecond10 要设置的 lgSecond10
	 */
	public void setLgSecond10(short lgSecond10) {
		this.lgSecond10 = lgSecond10;
	}
	/**
	 * @return nAltitude
	 */
	public short getNAltitude() {
		return nAltitude;
	}
	/**
	 * @param altitude 要设置的 nAltitude
	 */
	public void setNAltitude(short altitude) {
		nAltitude = altitude;
	}
	/**
	 * @return nGeoSys
	 */
	public byte getNGeoSys() {
		return nGeoSys;
	}
	/**
	 * @param geoSys 要设置的 nGeoSys
	 */
	public void setNGeoSys(byte geoSys) {
		nGeoSys = geoSys;
	}
	/**
	 * @return nHVVolt
	 */
	public short getNHVVolt() {
		return nHVVolt;
	}
	/**
	 * @param volt 要设置的 nHVVolt
	 */
	public void setNHVVolt(short volt) {
		nHVVolt = volt;
	}
	/**
	 * @return nMTime
	 */
	public short getNMTime() {
		return nMTime;
	}
	/**
	 * @param time 要设置的 nMTime
	 */
	public void setNMTime(short time) {
		nMTime = time;
	}
	/**
	 * @return nNaI_cr_count
	 */
	public int getNNaI_cr_count() {
		return nNaI_cr_count;
	}
	/**
	 * @param naI_cr_count 要设置的 nNaI_cr_count
	 */
	public void setNNaI_cr_count(int naI_cr_count) {
		nNaI_cr_count = naI_cr_count;
	}
	/**
	 * @return nNaI_dr_count
	 */
	public int getNNaI_dr_count() {
		return nNaI_dr_count;
	}
	/**
	 * @param naI_dr_count 要设置的 nNaI_dr_count
	 */
	public void setNNaI_dr_count(int naI_dr_count) {
		nNaI_dr_count = naI_dr_count;
	}
	/**
	 * @return nNaIValue
	 */
	public int getNNaIValue() {
		return nNaIValue;
	}
	/**
	 * @param naIValue 要设置的 nNaIValue
	 */
	public void setNNaIValue(int naIValue) {
		nNaIValue = naIValue;
	}
	/**
	 * @return nSatlite
	 */
	public byte getNSatlite() {
		return nSatlite;
	}
	/**
	 * @param satlite 要设置的 nSatlite
	 */
	public void setNSatlite(byte satlite) {
		nSatlite = satlite;
	}
	/**
	 * @return nSSD_dr_count
	 */
	public int getNSSD_dr_count() {
		return nSSD_dr_count;
	}
	/**
	 * @param nssd_dr_count 要设置的 nSSD_dr_count
	 */
	public void setNSSD_dr_count(int nssd_dr_count) {
		nSSD_dr_count = nssd_dr_count;
	}
	/**
	 * @return nSSDrate
	 */
	public int getNSSDrate() {
		return nSSDrate;
	}
	/**
	 * @param drate 要设置的 nSSDrate
	 */
	public void setNSSDrate(int drate) {
		nSSDrate = drate;
	}
	/**
	 * @return nStatus
	 */
	public int getNStatus() {
		return nStatus;
	}
	/**
	 * @param status 要设置的 nStatus
	 */
	public void setNStatus(int status) {
		nStatus = status;
	}
	/**
	 * @return nThermoral
	 */
	public short getNThermoral() {
		return nThermoral;
	}
	/**
	 * @param thermoral 要设置的 nThermoral
	 */
	public void setNThermoral(short thermoral) {
		nThermoral = thermoral;
	}
	/**
	 * @return nVoltage
	 */
	public short getNVoltage() {
		return nVoltage;
	}
	/**
	 * @param voltage 要设置的 nVoltage
	 */
	public void setNVoltage(short voltage) {
		nVoltage = voltage;
	}
}
