package utils;


public class SimpleFile {

	private String name;
	private String lastModified;

	public SimpleFile(String name, String lastModified) {
		this.name = name;
		this.lastModified = lastModified;
	}

	public String getName() {
		return name;
	}

	public String getLastModified() {
		return lastModified;
	}

	public void kill() {
		System.out.println("KILLLL");
	}
}
