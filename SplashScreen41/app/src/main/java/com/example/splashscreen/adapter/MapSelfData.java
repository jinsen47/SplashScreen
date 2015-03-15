package com.example.splashscreen.adapter;

import java.io.Serializable;

public class MapSelfData implements Serializable{
    public  int Flag;
    public String startAddr;
    public String endAddr;
    public String geoLat;
    public String geoLon;
    public MapSelfData(int Flag,String startAddr,String endAddr,String geoLat,String geoLon)
    {
    	this.Flag = Flag;
    	this.startAddr = startAddr;
    	this.endAddr = endAddr;
    	this.geoLat = geoLat;
    	this.geoLon = geoLon;
    }
    public void setFlag(int Flag)
    {
       this.Flag = Flag;
    }
    public int getFlag()
    {
    	return Flag;
    }
    
}
