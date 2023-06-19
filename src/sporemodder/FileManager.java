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
package sporemodder;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javafx.stage.FileChooser.ExtensionFilter;

/**
 * A class that contains multiple utilities to work with files and folders.
 */
public class FileManager extends AbstractManager {

	/**
	 * Returns the current instance of the FileManager class.
	 */
	public static FileManager get() {
		return MainApp.get().getFileManager();
	}

	public static final ExtensionFilter FILEFILTER_ALL = new ExtensionFilter("All Files", "*.*");
	public static final ExtensionFilter FILEFILTER_EXE = new ExtensionFilter("Executable File", "*.exe");
	public static final ExtensionFilter FILEFILTER_DDS = new ExtensionFilter("Direct Draw Surface Texture", "*.dds");
	public static final ExtensionFilter FILEFILTER_PNG = new ExtensionFilter("Portable Network Graphics", "*.png");

	// (*.package, *.db, *.dat, *.pkp, *.pkt, *.pld)
	public static final ExtensionFilter FILEFILTER_DBPF = new ExtensionFilter("Database Packed File", "*.package",
			"*.db", "*.dat", "*.pkp", "*.pkt", "*.pld", "*.spo");
	
	private final Set<String> searchableExtensions = new HashSet<String>();
	
	private final Set<String> protectedPackages = new HashSet<String>();
	
	@Override public void initialize(Properties properties) {

		searchableExtensions.addAll(Arrays.asList(
				"prop_t", "xml", "locale", "txt", "trigger", "tlsa_t", 
				"pctp_t", "pfx", "smt_t", "hlsl", "shader_builder", 
				"vertex_fragment", "pixel_fragment", "anim_t", "cnv_t",
				"lvl_t", "gait_t", "backgroundMap_t", "effectMap_t",
				"cell_t", "globals_t", "look_algorithm_t", "look_table_t",
				"lootTable_t", "populate_t", "powers_t",
				"random_creature_t", "structure_t", "world_t",
				"arth_t", "summary_t"));
		
		protectedPackages.addAll(Arrays.asList("patchdata.package", "spore_audio1.package", "spore_audio2.package", 
				"spore_content.package", "spore_game.package", "spore_graphics.package", "spore_pack_03.package", 
				"bp2_data.package", "ep1_patchdata.package", "spore_ep1_content_01.package", "spore_ep1_content_02.package", 
				"spore_ep1_data.package", "spore_ep1_locale_01.package", "spore_ep1_locale_02.package"));
	}
	
	/**
	 * Returns the substring of the name without containing any extensions. Specifically, everything after a '.' (included) gets removed.
	 * @param name
	 * @return
	 */
	public static String removeExtension(String name) {
		int indexOf = name.indexOf(".");
		return indexOf == -1 ? name : name.substring(0, indexOf);
	}
	
	public static String getExtension(String name) {
		int indexOf = name.lastIndexOf(".");
		return indexOf == -1 ? null : name.substring(indexOf + 1);
	}

	/**
	 * Deletes all the contents of a directory. If the directory does not exist,
	 * this method does nothing.
	 * 
	 * @throws IOException
	 *             If there was an error while deleting the contents of the folder.
	 * @param dir
	 */
	public void deleteDirectory(File dir) throws IOException {
		if (!dir.exists())
			return;

		try {
			Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(final Path file, final IOException e) {
					return handleException(e);
				}

				private FileVisitResult handleException(final IOException e) {
					e.printStackTrace(); // replace with more robust error handling
					return FileVisitResult.TERMINATE;
				}

				@Override
				public FileVisitResult postVisitDirectory(final Path dir, final IOException e) throws IOException {
					if (e != null)
						return handleException(e);
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}
			});
		}
		catch (Exception e) {
			throw new IOException(e);
		}
	}
	
	/**
	 * Returns true if the given file name has a format that is supported by text searching.
	 * @param fileName
	 * @return
	 */
	public boolean isSearchable(String fileName) {
		 int indexOf = fileName.lastIndexOf(".");
		 if (indexOf != -1) {
			 return this.searchableExtensions.contains(fileName.substring(indexOf+1));
		 } else {
			 return false;
		 }
	}
	
	/**
	 * Returns true if the given file belongs to a protected package (a package from the game that should not be repacked)
	 * @param file
	 * @return
	 */
	public boolean isProtectedPackage(File file) {
		return protectedPackages.contains(file.getName().toLowerCase());
	}
	
	/**
	 * Returns the set of all file extensions that support text searching.
	 * @return
	 */
	public Set<String> getSearchableExtensions() {
		return searchableExtensions;
	}
	
	/**
	 * Returns the set of protected package names (packages from the game that should not be repacked)
	 * @return
	 */
	public Set<String> getProtectedPackageNames() {
		return protectedPackages;
	}
}
