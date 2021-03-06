package com.ht.scada.communication.entity;

import com.ht.scada.common.tag.util.VarGroupEnum;
import com.ht.scada.communication.data.kv.IKVRecord;
import oracle.kv.Key;
import oracle.kv.Value;
import org.joda.time.LocalDateTime;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * 数据分组记录，存入历史数据库<br/>
 * 按变量分组进行保存
 * @author 薄成文
 * 
 */
public class VarGroupData implements IKVRecord {
	
	public static final String RECORD_TYPE = "VAR_GROUP";
	
	private String code;// 计量点编号(回路号、井号等)
	private VarGroupEnum group;// 变量分组

    /**
     * 遥测变量map, key为变量的varName
     */
	private Map<String, Float> ycValueMap = new HashMap<>();
    /**
     * 遥脉变量map, key为变量的varName
     */
	private Map<String, Double> ymValueMap = new HashMap<>();
    /**
     * 遥信变量map, key为变量的varName
     */
	private Map<String, Boolean> yxValueMap = new HashMap<>();
    /**
     * 遥信数组变量map, key为变量的varName
     */
	private Map<String, float[]> arrayValueMap = new HashMap<>();// 数组包

	private Date datetime;

	public VarGroupData() {
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public VarGroupEnum getGroup() {
		return group;
	}

	public void setGroup(VarGroupEnum group) {
		this.group = group;
	}

	public Map<String, Float> getYcValueMap() {
		return ycValueMap;
	}

	public void setYcValueMap(Map<String, Float> ycValueMap) {
		this.ycValueMap = ycValueMap;
	}

	public Map<String, Double> getYmValueMap() {
		return ymValueMap;
	}

	public void setYmValueMap(Map<String, Double> ymValueMap) {
		this.ymValueMap = ymValueMap;
	}

	public Map<String, Boolean> getYxValueMap() {
		return yxValueMap;
	}

	public void setYxValueMap(Map<String, Boolean> yxValueMap) {
		this.yxValueMap = yxValueMap;
	}

	public Map<String, float[]> getArrayValueMap() {
		return arrayValueMap;
	}

	public void setArrayValueMap(Map<String, float[]> arrayValueMap) {
		this.arrayValueMap = arrayValueMap;
	}

	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	@Override
	public Key makeKey() {
        final String timestamp = LocalDateTime.fromDateFields(datetime).toString();
    	return Key.createKey(Arrays.asList(DB_NAME, RECORD_TYPE, code), Arrays.asList(group.toString(), timestamp));
	}

	@Override
	public void parseKey(Key key) {
		
        final List<String> majorPath = key.getMajorPath();

        if (!RECORD_TYPE.equals(majorPath.get(1))) {
            throw new IllegalArgumentException("Not a fault record: " + key);
        }

        setCode(majorPath.get(2));
        
        final List<String> minorPath = key.getMinorPath();
        try {
			setGroup(VarGroupEnum.valueOf(minorPath.get(0)));
		} catch (Exception e) {
			e.printStackTrace();
		}
        setDatetime(LocalDateTime.parse(minorPath.get(1)).toDate());
	}

	@Override
	public Value makeValue() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
//        	SnappyOutputStream sos = new SnappyOutputStream(baos);
//        	DataOutputStream dos = new DataOutputStream(sos);
        	DataOutputStream dos = new DataOutputStream(baos);

            dos.writeInt(ycValueMap.size());
        	for (Entry<String, Float> entry : ycValueMap.entrySet()) {
	        	dos.writeUTF(entry.getKey());
	        	dos.writeFloat(entry.getValue());
        	}

        	dos.writeInt(ymValueMap.size());
        	for (Entry<String, Double> entry : ymValueMap.entrySet()) {
	        	dos.writeUTF(entry.getKey());
	        	dos.writeDouble(entry.getValue());
        	}

        	dos.writeInt(yxValueMap.size());
        	for (Entry<String, Boolean> entry : yxValueMap.entrySet()) {
	        	dos.writeUTF(entry.getKey());
	        	dos.writeBoolean(entry.getValue());
        	}
        	
        	dos.writeInt(arrayValueMap.size());
        	for (Entry<String, float[]> entry : arrayValueMap.entrySet()) {
	        	dos.writeUTF(entry.getKey());
	        	float[] array = entry.getValue();
	        	dos.writeInt(array.length);
	        	for (float f : array) {
	        		dos.writeFloat(f);
	        	}
        	}

            Value value = Value.createValue(baos.toByteArray());
            baos.close();
            return value;
        } catch (IOException e) {
            e.printStackTrace();
            //throw new RuntimeException(e);
        }

        return null;
        
//		return Value.createValue(baos.toByteArray());
	}
	
	@Override
	public void parseValue(Value value) {
        try {
        	ByteArrayInputStream bais = new ByteArrayInputStream(value.getValue());

//            SnappyInputStream sis = new SnappyInputStream(bais);
//            DataInputStream dis = new DataInputStream(sis);

        	//GZIPInputStream gis = new GZIPInputStream(bais);
        	//DataInputStream dis = new DataInputStream(gis);
        	DataInputStream dis = new DataInputStream(bais);

        	int ycSize = dis.readInt();
        	for (int i = 0; i < ycSize; i++) {
        		ycValueMap.put(dis.readUTF(), dis.readFloat());
        	}
        	
        	int ymSize = dis.readInt();
        	for (int i = 0; i < ymSize; i++) {
        		ymValueMap.put(dis.readUTF(), dis.readDouble());
        	}
        	
        	int yxSize = dis.readInt();
        	for (int i = 0; i < yxSize; i++) {
        		yxValueMap.put(dis.readUTF(), dis.readBoolean());
        	}
        	
        	int arraySize = dis.readInt();
        	for (int i = 0; i < arraySize; i++) {
        		String key = dis.readUTF();
        		float[] array = new float[dis.readInt()];
        		for (int j = 0; j < array.length; j++) {
        			array[j] = dis.readFloat();
        		}
        		arrayValueMap.put(key, array);
        	}
        	
        	dis.close();
		} catch (IOException e) {
            throw new RuntimeException(e);
		}
		
	}
}
