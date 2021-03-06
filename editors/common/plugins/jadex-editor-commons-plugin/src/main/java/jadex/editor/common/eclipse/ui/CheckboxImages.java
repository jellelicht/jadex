package jadex.editor.common.eclipse.ui;

/* 
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details.
 */ 

//import static CheckboxImages.Mode.CHECKED;
//import static CheckboxImages.Mode.INDETERMINATE;
//import static CheckboxImages.Mode.UNCHECKED;

import java.util.EnumSet;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * Provides images of the various states of a {@link Button} with {@link SWT#CHECK} style on the
 * current platform and UI theme. Useful in {@link LabelProvider}s that need to display a checkbox
 * image.
 *
 * @see SelectableCheckboxCellEditor
 */
public final class CheckboxImages {
	/*
	 * Original implementation from Tom Schindl, see
	 * http://tom-eclipse-dev.blogspot.com/2007/01/tableviewers-and-nativelooking.html
	 */
	 
	/** The checkbox selection mode. */
	public static enum Mode {
		/** The checked state. */
		CHECKED,
		/**
		 * The indeterminate (grayed) state, typically indicating that some sub-elements are
		 * selected, but not all.
		 */
		INDETERMINATE,
		/** The unchecked state. */
		UNCHECKED
	}

	private final ImageRegistry fRegistry;

	/**
	 * Creates a new image provider using the given image registry, and creating images as children
	 * of the given <code>control</code>'s shell.
	 *
	 * @param registry the image registry to store images in
	 * @param control the control to obtain a {@link Shell} from
	 */
	public CheckboxImages(ImageRegistry registry, Control control) {
//		Arguments.nonNull(registry);
//		Arguments.nonNull(control);
		fRegistry= registry;
		makeShots(control, registry);
	}

	/**
	 * Returns the image of a checkbox on the current platform and UI theme.
	 *
	 * @param selected <code>true</code> for {@link Mode#CHECKED}, <code>false</code> for
	 *        {@link Mode#UNCHECKED}
	 * @param enabled <code>true</code> for an enabled image, <code>false</code> otherwise
	 * @return the corresponding image
	 */
	public Image getCheckboxImage(boolean selected, boolean enabled) {
		return getCheckboxImage(selected ? Mode.CHECKED : Mode.UNCHECKED, enabled);
	}

	/**
	 * Returns the image of a checkbox on the current platform and UI theme.
	 *
	 * @param mode the selection {@link Mode}
	 * @param enabled <code>true</code> for an enabled image, <code>false</code> otherwise
	 * @return the corresponding image
	 */
	public Image getCheckboxImage(Mode mode, boolean enabled) {
		return fRegistry.get(computeKey(mode, enabled));
	}

	private static String computeKey(Mode mode, boolean enabled) {
		return "CheckboxImage:" + mode.name() + ":" + (enabled ? "enabled" : "disabled");
	}

	private static void makeShots(Control control, ImageRegistry registry) {
		Color greenScreen = new Color(control.getDisplay(), 222, 223, 224);
		Shell shell = new Shell(control.getShell(), SWT.NO_TRIM);
		Composite composite= new Composite(shell, SWT.NONE);
		composite.setBackground(greenScreen);

		for (Mode mode: Mode.values())
			for (boolean enabled : new boolean[] {true, false})
				put(registry, computeKey(mode, enabled), printButton(composite, greenScreen, mode, enabled));

		greenScreen.dispose();
		shell.dispose();
	}

	private static void put(ImageRegistry registry, String key, Image image) {
		registry.remove(key);
		registry.put(key, image);
	}

	private static Image printButton(Composite parent, Color greenScreen, Mode mode, boolean enabled) {
		Button button = new Button(parent, SWT.CHECK);
		button.setBackground(greenScreen);
		button.setSelection(EnumSet.of(Mode.CHECKED, Mode.INDETERMINATE).contains(mode));
		button.setEnabled(enabled);
		button.setGrayed(mode == Mode.INDETERMINATE);

		int size= 16; // icon size
		parent.setSize(size, size);
		button.setSize(size - 1, size - 1);
		button.setLocation(1, 1); // add one pixel border

		Device device= parent.getDisplay();
		Image image= new Image(device, size, size);
		GC gc= new GC(image);
		gc.setBackground(greenScreen);
		parent.print(gc);
		gc.dispose();
		button.dispose();

		ImageData data= image.getImageData();
		image.dispose();

		data.transparentPixel= data.palette.getPixel(greenScreen.getRGB());
		Image realImage = new Image(device, data);
		return realImage;
	}
}

