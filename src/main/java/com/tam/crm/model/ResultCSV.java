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

	@Override public String toString() {
		return "<tr " + (level == Level.ERROR ? "style=\"color:red\"" : "style=\"color:yellow\"") + ">" +
					"<td>" + line + "</td>" +
					"<td>" + level + "</td>" +
					"<td>" + message.replaceAll("\n", "<br>") + "</td>" +
			   "</tr>";
	}
}
