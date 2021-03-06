package com.ht.scada.communication.entity;


import com.ht.scada.communication.util.CommunicationProtocal;

import javax.persistence.Transient;
import java.util.Date;

/**
 * 采集通道模型<br>
 * 描述采集通道通讯规约、通讯基本参数、物理端口信息、通讯帧信息
 * @author 薄成文
 *
 */
public class ChannelInfo {

    private int id;
	private Date updateTime;// 上次更新时间
	private Integer idx;// 序号
	private String name;
	
	/**
	 * 规约类型
	 * IEC104
	 * ModbusTCP
	 * ModbusRTU
	 * DL645
	 */
	private CommunicationProtocal protocal = CommunicationProtocal.IEC104;
	private int offline;// 离线阈值
	private int intvl = 100;//ms

	/**
	 * 物理信息
	 * TCP/IP 通讯方式：	【通讯方式】|【IP】:【端口】
	 * 					tcp/ip|192.168.1.110:4660
	 * DTU 通讯方式：		【通讯方式】|【DTU-ID】:【端口】:【心中信号】:【心跳信号间隔】
	 * 					dtu|2000:9815:hello:180
	 * 串口通讯方式:		【通讯方式】|【端口】：【波特率】:【数据位(5/6/7/8)】:【校验位(无/奇/偶)】:【停止位(1/1.5/2)】
	 * 					serial|1:9600:8:无:1
	 */
	private String portInfo = "10.67.111.176:4660";
	
	/**
	 * 通讯帧<br>
	 * <p><b>modbus</b>: 【设备地址】|【功能码】-【数据地址】-【数据长度】|【优先级】|【帧名称(可省略)】<br>
	 * 多个通讯帧用逗号隔开, 遥控帧优先级设置为0<br>
	 * 1|3-1-10|3|<br>
	 * 1|3-100-10|0|soe<br>
	 * 1-10 13-23|3-1-10|3| 设备地址从1-10, 13-23的连续地址 * </p>
	 * <p><b>iec104</b>: 【召唤类型编码】-【执行间隔(s)】|【帧名称(可省略)】<br>
	 * 多个通讯帧用逗号隔开, 帧间隔设置为0表示只在第一次建立连接时召唤<br>
	 * 0x64-10|总召唤<br>
	 * 0x65-1200|召唤电能<br>
	 * 0x8e-600|召唤定时示功图数据</p>
	 */
//	@Lob
	private String frames;

    @Transient
    private boolean onLine = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getIdx() {
        return idx;
    }

    public void setIdx(Integer idx) {
        this.idx = idx;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CommunicationProtocal getProtocal() {
        return protocal;
    }

    public void setProtocal(CommunicationProtocal protocal) {
        this.protocal = protocal;
    }

    public int getOffline() {
        return offline;
    }

    public void setOffline(int offline) {
        this.offline = offline;
    }

    public int getIntvl() {
        return intvl;
    }

    public void setIntvl(int intvl) {
        this.intvl = intvl;
    }

    public String getPortInfo() {
        return portInfo;
    }

    public void setPortInfo(String portInfo) {
        this.portInfo = portInfo;
    }

    public String getFrames() {
        return frames;
    }

    public void setFrames(String frames) {
        this.frames = frames;
    }

    public boolean isOnLine() {
        return onLine;
    }

    public void setOnLine(boolean onLine) {
        this.onLine = onLine;
    }
}
