package com.tam.crm.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultCSV {
	Long line;
	Level level;
	String message;

	public enum Level {
		ERROR, WARN, INFO
	}

}
