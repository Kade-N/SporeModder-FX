/****************************************************************************
* Copyright (C) 2019 Eric Mor
*
* This file is part of SporeModder FX.
*
* SporeModder FX is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
****************************************************************************/
package sporemodder.file.simulator;

import java.io.IOException;

import sporemodder.file.ResourceKey;
import sporemodder.file.dbpf.DBPFItem;
import sporemodder.file.dbpf.DatabasePackedFile;
import sporemodder.file.filestructures.MemoryStream;
import sporemodder.file.filestructures.StreamReader;
import sporemodder.file.filestructures.StreamWriter;
import sporemodder.file.prop.PropertyList;

public class cScenarioResource {

	// for testing
	private byte[] propData;
	
	public final PropertyList propertyList = new PropertyList();
	public MemoryStream dataStream;
	public final cScenarioResourceAttributes scenario = new cScenarioResourceAttributes();
	
	public void read(StreamReader stream) throws Exception {
		
		long magic = stream.readLEUInt();
		if (magic != 0xB523D4F9L) {
			throw new IOException("Incorrect magic number.");
		}
		
		int version = stream.readLEInt();
		
		if (version < 3) {
			throw new IOException("Unsupported version.");
		}
		
		propertyList.read(stream);
		
		long endPos = stream.getFilePointer();
		
		stream.seek(8);
		propData = new byte[(int) (endPos - 8)];
		stream.read(propData);
		stream.seek(endPos);
		
		int dataVersion = stream.readLEInt();
		int dbpfSize = stream.readLEInt();
		
		byte[] dbpfData = new byte[dbpfSize];
		stream.read(dbpfData);
		
		MemoryStream dbpfStream = new MemoryStream(dbpfData); 
		DatabasePackedFile dbpf = new DatabasePackedFile();
		dbpf.read(dbpfStream);
		
		dataStream = dbpf.getItem(new ResourceKey(0x1897C18, 0x1897C18, 0x1897C18)).processFile(dbpfStream);
		
		scenario.read(dataStream);
	}
	
	public void write(StreamWriter stream, boolean convertData) throws IOException {
		stream.writeLEUInt(0xB523D4F9L);
		stream.writeLEInt(17);
		
		if (propData != null) {
			stream.write(propData);
		}
		else {
			propertyList.write(stream);
		}
		
		// Data version
		stream.writeLEInt(17);
		
		// Write the DBPF
		MemoryStream dbpfStream = new MemoryStream(); 
		DatabasePackedFile dbpf = new DatabasePackedFile();
		
		DBPFItem item = new DBPFItem();
		item.name.setGroupID(0x1897C18);
		item.name.setInstanceID(0x1897C18);
		item.name.setTypeID(0x1897C18);
		
		if (convertData) {
			dataStream = new MemoryStream();
			//TODO write data
		}
		
		dbpf.writeHeader(dbpfStream);
		dbpf.writeFile(dbpfStream, item, dataStream.toByteArray(), true);
		dbpf.writeIndex(dbpfStream);
		
		// Rewrite the DBPF header
		dbpfStream.seek(0);
		dbpf.writeHeader(dbpfStream);
		
		// DBPF size
		stream.writeLEUInt(dbpfStream.length());
		stream.write(dbpfStream.toByteArray());
	}
	
	
	public String printData() {
		StringBuilder sb = new StringBuilder();
		String tabulation = "\t";
		
		sb.append('{');
		sb.append('\n');
		
		scenario.print(sb, tabulation);
		
		sb.append('}');
		
		return sb.toString();
	}
	
	public String printDataXML() {
		StringBuilder sb = new StringBuilder();
		String tabulation = "\t";
		
		sb.append("<cScenarioResource>\n");
		
		scenario.printXML(sb, tabulation);
		
		sb.append("</cScenarioResource>\n");
		
		return sb.toString();
	}
}
