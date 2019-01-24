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
package sporemodder.file.cnv;

import java.io.IOException;

import emord.filestructures.StreamReader;
import emord.filestructures.StreamWriter;
import emord.filestructures.Stream.StringEncoding;

public class Conversation {

	public void read(StreamReader stream) throws IOException {
		
		int count = stream.readLEInt();
		
		for (int i = 0; i < count; i++) {
			int id = stream.readLEInt();
			
			String name = stream.readString(StringEncoding.ASCII, stream.readInt());
			
			stream.readBoolean();
			stream.readBoolean();
			stream.readBoolean();
			stream.readBoolean();
			stream.readBoolean();
			stream.readLEInt();
			stream.readInt();
			stream.readInt();
			stream.readInt();
			
			
		}
	}
	
	public void write(StreamWriter stream) throws IOException {
		
	}
}
