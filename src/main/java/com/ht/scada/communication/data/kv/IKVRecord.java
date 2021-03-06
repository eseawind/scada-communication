package com.ht.scada.communication.data.kv;

import oracle.kv.Key;
import oracle.kv.Value;

public interface IKVRecord {
	public static final String DB_NAME = "db";
	
	Key makeKey();
	void parseKey(Key key);
	
	Value makeValue();
	void parseValue(Value value);
}
