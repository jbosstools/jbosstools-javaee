/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.struts.ui.editor.print;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

public class PrintIconHelper {
	private static Map iconCashe = new HashMap();

	public static Image getPrintImage(Image image, Color bgColor) {
		Image newImage = (Image)iconCashe.get(image);
		if (newImage == null) {
			ImageData imageData = image.getImageData();
			ImageData imgData = (ImageData)imageData.clone();
			ImageData mask = imgData.getTransparencyMask();
			int bgPixelValue = imgData.palette.getPixel(bgColor.getRGB());
	
			for (int x = 0; x < mask.width; x++) { 
				for (int y = 0; y < mask.height; y++) {
					int pixelValue = mask.getPixel(x, y);
					if (pixelValue == 0) {
						imgData.setPixel(x, y, bgPixelValue);
					}
				}
			}		 
			imgData.maskData = null;
	
			newImage = new Image(null, imgData);
			iconCashe.put(image, newImage);
		}

		return newImage;
	}

	public static Image getPrintImage(Image image) {
		return getPrintImage(image, ColorConstants.white);
	}
}
