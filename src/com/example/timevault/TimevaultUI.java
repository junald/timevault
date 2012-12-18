package com.example.timevault;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;

public class TimevaultUI extends UI {
	@Override
	public void init(VaadinRequest request) {
		Label label = new Label("Hello Vaadin user");
		setContent(label);
	}

}
