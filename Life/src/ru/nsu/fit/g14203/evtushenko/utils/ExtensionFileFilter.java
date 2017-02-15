package ru.nsu.fit.g14203.evtushenko.utils;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class ExtensionFileFilter extends FileFilter
{
	private String extension, description;

	public ExtensionFileFilter(String extension, String description)
	{
		this.extension = extension;
		this.description = description;
	}

	@Override
	public boolean accept(File f) {
		return f.isDirectory() || f.getName().toLowerCase().endsWith("."+extension.toLowerCase());
	}

	@Override
	public String getDescription() {
		return description+" (*."+extension+")";
	}
}