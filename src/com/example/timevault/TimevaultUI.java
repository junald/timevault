package com.example.timevault;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;

public class TimevaultUI extends UI {
	@Override
	public void init(VaadinRequest request) {
		MainUI mu = new MainUI();
		setContent(mu);
	}

}
