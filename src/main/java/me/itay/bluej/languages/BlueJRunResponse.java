package me.itay.bluej.languages;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class BlueJRunResponse {
	
	private PipedInputStream stdout;
	private PipedInputStream stderr;
	private PipedOutputStream stdin;
	
	private PipedOutputStream out = new PipedOutputStream();
	private PipedOutputStream err = new PipedOutputStream();
	private PipedInputStream in = new PipedInputStream();
	
	private int returnCode;
	
	public BlueJRunResponse(int returnCode) {
		this.returnCode = returnCode;
		try {
			stdout = new PipedInputStream(out);
			stderr = new PipedInputStream(err);
			stdin = new PipedOutputStream(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getReturnCode() {
		return returnCode;
	}
	
	public PipedOutputStream getErr() {
		return err;
	}
	
	public PipedOutputStream getOut() {
		return out;
	}
	
	public PipedInputStream getIn() {
		return in;
	}
	
	public OutputStream getStdin() {
		return stdin;
	}
	
	public InputStream getStdout() {
		return stdout;
	}
	
	public InputStream getStderr() {
		return stderr;
	}
	
}
