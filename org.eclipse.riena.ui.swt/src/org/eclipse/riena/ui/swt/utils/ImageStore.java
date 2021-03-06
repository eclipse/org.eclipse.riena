/*******************************************************************************
 * Copyright (c) 2007, 2014 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.ui.swt.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.batik.anim.dom.*;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.osgi.service.log.LogService;

import org.eclipse.core.runtime.Assert;
import org.eclipse.equinox.log.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import org.eclipse.riena.core.Log4r;
import org.eclipse.riena.core.singleton.SingletonProvider;
import org.eclipse.riena.core.util.FileUtils;
import org.eclipse.riena.core.util.StringUtils;
import org.eclipse.riena.core.wire.InjectExtension;
import org.eclipse.riena.internal.ui.swt.Activator;
import org.eclipse.riena.ui.core.resource.IIconManager;
import org.eclipse.riena.ui.core.resource.IconManagerProvider;
import org.eclipse.riena.ui.core.resource.IconSize;
import org.eclipse.riena.ui.core.resource.IconState;
import org.eclipse.riena.ui.swt.lnf.LnfManager;

/**
 * The ImageStore returns the images for given names. The images are loaded form and cached. The ImageStore extends the images name, if a state (@see
 * {@link ImageState}) like pressed of hover is given. If the image name has no file extension, the extension ".png" will be added.
 * 
 * @since 6.0
 */
public final class ImageStore {

	private Image missingImage;
	private IImagePathExtension[] iconPaths;

	private final static SingletonProvider<ImageStore> IS = new SingletonProvider<ImageStore>(ImageStore.class);

	private final ArrayList<IImageFind> listOfStrategys = new ArrayList<IImageFind>();

	private static final Logger LOGGER = Log4r.getLogger(Activator.getDefault(), ImageStore.class);

	private final SvgRasterizer svgRasterizer;

	private final Map<String, Boolean> cachedImageNames = new HashMap<String, Boolean>();

	private ImageStore() {
		// utility class
		final ImageOperations availableOperations = new ImageOperations();
		listOfStrategys.add(availableOperations.getPngOperation());
		listOfStrategys.add(availableOperations.getSvgOperation());
		listOfStrategys.add(availableOperations.getPngOperationWithoutImageSize());
		listOfStrategys.add(availableOperations.getPngOperationSecondlastAttempt());
		listOfStrategys.add(availableOperations.getPngDefaultImageOperation());

		svgRasterizer = new SvgRasterizer();
	}

	/**
	 * Returns an instance (always the same) of this class.
	 * 
	 * @return instance of {@code ImageStore}
	 */
	public static ImageStore getInstance() {
		return IS.getInstance();
	}

	/**
	 * Returns the image for the given image name and with the given file extension.
	 * 
	 * @param imageName
	 *            name (ID) of the image
	 * @param fileExtension
	 *            extension of the image file (@see ImageFileExtension)
	 * @param imageSizeRequested
	 *            Image size is necessary for SVG files.
	 * @return image or {@code null} if no image exists for the given name.
	 * @since 6.1
	 */
	public Image getImage(final String imageName, final ImageFileExtension fileExtension, final IconSize imageSizeRequested) {
		Image image = null;
		final ImageStoreStrategyContextHolder context = new ImageStoreStrategyContextHolder(null);

		for (final IImageFind strategy : listOfStrategys) {
			context.applayStrategy(strategy);
			image = context.executeStrategy(imageName, fileExtension, imageSizeRequested);
			if (image != null) {
				return image;
			}
		}
		return null;

	}

	/**
	 * Removes the defaultmapping from the given filename.
	 * 
	 * @param filename
	 * @return the fileName without defaultmapping. Otherwise the filename.
	 */
	private String removeDefaultmapping(final String fileName) {
		if (fileName.length() > 1) {
			final String fileNameWithoutDefaultmapping = fileName.substring(0, fileName.length() - 1);
			return fileNameWithoutDefaultmapping;
		}

		return fileName;
	}

	/**
	 * 
	 * Tries to find the right imageSize for the given Image. If the given ImageSize is different from the found imageSize this method will return the given
	 * imageSize. If the imageSize is NONE it will return the found imageSize.
	 * 
	 * @param fullName
	 *            The name of the image, were the method tries to find the attached imageSize.
	 *
	 * @param imageSize
	 *            The given imageSize which will be primarily used over the found imageSize.
	 * 
	 * @returns The corresponding IconSize, or IconSize.NONE if no IconSize could be found.
	 * 
	 */
	private IconSize computeIconSize(final String fullName, final IconSize imageSize) {
		if (imageSize != null && !imageSize.isSizeNone()) {
			return imageSize;
		}

		final String name = FileUtils.getNameWithoutExtension(fullName);
		final IconSize imageSizeFromFileName = getIconSizeFromName(name);

		return imageSizeFromFileName;
	}

	/**
	 * Tries to find out the IconSize of the given image name. If the last letter of the imageName is a mapped IconSize the Method will return this IconSize.
	 * 
	 * 
	 * @param name
	 *            The name of the requested image.
	 * @returns The corresponding IconSize, or IconSize.NONE if no IconSize could be found.
	 */
	private IconSize getIconSizeFromName(final String name) {
		IconSize iconSize = IconSize.NONE;
		if (name.length() > 1) {
			final String defaultmapping = name.substring(name.length() - 1);
			iconSize = IconSize.getIconSizeFromDefaultMapping(defaultmapping);
			if (iconSize == null) {
				return IconSize.NONE;
			}
		}
		return iconSize;
	}

	/**
	 * Returns the ImageDescriptor of the image for the given image name
	 * 
	 * @param imageName
	 *            name (ID) of the image
	 * @param fileExtension
	 *            extension of the image file (@see ImageFileExtension)
	 * @return ImageDescriptor of the image or {@code null} if no image exists for the given name.
	 * @since 6.1
	 */
	public ImageDescriptor getImageDescriptor(final String imageName, final ImageFileExtension fileExtension) {

		final Image image = getImage(imageName, fileExtension);
		if (image == null) {
			return null;
		}

		return ImageDescriptor.createFromImage(image);

	}

	/**
	 * Returns the ImageDescriptor of the image for the given image name and size
	 * 
	 * @param imageName
	 *            name (ID) of the image
	 * @param imageSize
	 *            the IconSize of the image
	 * @return ImageDescriptor of the image or {@code null} if no image exists for the given name.
	 * @since 6.2
	 */
	public ImageDescriptor getImageDescriptor(final String imageName, final IconSize imageSize) {

		final Image image = getImage(imageName, imageSize);
		if (image == null) {
			return null;
		}

		return ImageDescriptor.createFromImage(image);
	}

	/**
	 * Returns the URI of the image for the given image name
	 * 
	 * @param imageName
	 *            name (ID) of the image
	 * @param fileExtension
	 *            extension of the image file (@see ImageFileExtension)
	 * @return URI of the image or {@code null} if no image exists for the given name.
	 * @since 6.1
	 */
	public URI getImageUri(final String imageName, final ImageFileExtension fileExtension) {

		URI uri = null;

		Point dpi = SwtUtilities.convertPointToDpi(SwtUtilities.getDefaultDpi());
		String fullName = getFullScaledName(imageName, fileExtension, dpi);
		uri = getUri(fullName);
		if (uri != null) {
			return uri;
		}

		fullName = getFullName(imageName, fileExtension);
		uri = getUri(fullName);
		if (uri != null) {
			return uri;
		}

		dpi = SwtUtilities.getDefaultDpi();
		fullName = getFullScaledName(imageName, fileExtension, dpi);
		uri = getUri(fullName);
		if (uri != null) {
			return uri;
		}

		final String defaultIconName = getDefaultIconMangerImageName(imageName);
		if (!StringUtils.equals(defaultIconName, imageName)) {
			fullName = getFullName(defaultIconName, fileExtension);
			uri = getUri(fullName);
		}

		return uri;

	}

	/**
	 * Tries to find the given image and returns the URI of the image file. The file of the image is searched in every given bundle + icon path. The icon paths
	 * are define via extension points.
	 * 
	 * @param fullName
	 *            complete name of the image (with scaling, with extension etc.)
	 * @return URI of the image or {@code null} if the image file wasn't found
	 * @see #getImageUrl(String)
	 */
	private URI getUri(final String fullName) {

		final URL imageUrl = getImageUrl(fullName);
		if (imageUrl != null) {
			try {
				return imageUrl.toURI();
			} catch (final URISyntaxException ex) {
				LOGGER.log(LogService.LOG_DEBUG, "Can't create URI.", ex); //$NON-NLS-1$
			}
		}

		return null;

	}

	/**
	 * Uses the default icon manager to generate the icon name/ID.
	 * 
	 * @param imageName
	 *            name of the image (icon ID)
	 * @return default icon name/ID
	 */
	private String getDefaultIconMangerImageName(final String imageName) {

		final IIconManager iconManager = IconManagerProvider.getInstance().getIconManager();
		final String name = iconManager.getName(imageName);
		IconSize size = iconManager.getSize(imageName);
		if ((size == null) || (size.getClass() != IconSize.class)) {
			size = IconSize.NONE;
		}
		IconState state = iconManager.getState(imageName);
		if ((state == null) || (state.getClass() != IconState.class)) {
			state = IconState.NORMAL;
		}

		final IIconManager defaultIconManager = IconManagerProvider.getInstance().getDefaultIconManager();
		final String defaultIconName = defaultIconManager.getIconID(name, size, state);

		return defaultIconName;

	}

	/**
	 * Returns the image for the given image name and given state.
	 * 
	 * @param imageName
	 *            name (ID) of the image
	 * @return image or {@code null} if no image exists for the given name.
	 * @since 6.0
	 */
	public Image getImage(final String imageName) {
		return getImage(imageName, ImageFileExtension.PNG, IconSize.NONE);
	}

	/**
	 * Returns the image for the given image name and given size.
	 * 
	 * @param imageName
	 *            name (ID) of the image
	 * @return image or {@code null} if no image exists for the given name.
	 * @since 6.1
	 */
	public Image getImage(final String imageName, final IconSize imageSize) {
		return getImage(imageName, ImageFileExtension.PNG, imageSize);
	}

	public Image getImage(final String imageName, final ImageFileExtension fileExtension) {
		return getImage(imageName, fileExtension, IconSize.NONE);
	}

	/**
	 * Returns the full name of the image.
	 * 
	 * @param imageName
	 *            name (ID) of the image
	 * @param state
	 *            state of the image (@see ImageState)
	 * @param fileExtension
	 *            extension of the image file (@see ImageFileExtension)
	 * @return full name of the image (file name).
	 */
	private String getFullName(final String imageName, final ImageFileExtension fileExtension) {

		if (StringUtils.isEmpty(imageName)) {
			return null;
		}

		String fullName = imageName;

		if (imageName.indexOf('.') < 0) {
			if (fileExtension != null) {
				fullName += "." + fileExtension.getFileNameExtension(); //$NON-NLS-1$
			}
		}

		return fullName;

	}

	private String getFullScaledName(final String imageName, final ImageFileExtension fileExtension, final Point dpi) {
		return getFullScaledName(imageName, fileExtension, dpi, null);
	}

	private String getFullScaledName(final String imageName, final ImageFileExtension fileExtension, final Point dpi, final IconSize imageSize) {

		if (StringUtils.isEmpty(imageName)) {
			return null;
		}
		if (imageName.indexOf('.') >= 0) {
			return null;
		}
		if (fileExtension == null) {
			return null;
		}

		String fullName = addImageScaleSuffix(imageName, fileExtension, dpi, imageSize);
		if (fullName != null) {
			return fullName += "." + fileExtension.getFileNameExtension(); //$NON-NLS-1$
		}
		return null;

	}

	/**
	 * Returns the complete name of the SVG file.
	 * 
	 * @param imageName
	 *            name (ID) of the image
	 * @param imageSize
	 * @return the name of the SVG file or {@code null} if this isn't a SVG file (e.g. imageName has other file extension)
	 */
	private String getFullSvgName(final String imageName) {

		if (StringUtils.isEmpty(imageName)) {
			return null;
		}
		final String svgExtension = '.' + ImageFileExtension.SVG.getFileNameExtension();
		if (imageName.endsWith(svgExtension)) {
			return imageName;
		}
		if (imageName.indexOf('.') >= 0) {
			return null;
		}
		return imageName + svgExtension;
	}

	/**
	 * Returns the image for the given name. If the image isn't cached, the image is loaded form the resources and stores in the cache of the {@code ImageStore}
	 * .
	 * 
	 * @param fullName
	 *            full name of the image (file name)
	 * @return image or {@code null} if no image exists for the given name.
	 */
	private synchronized Image loadImage(final String fullName) {
		if (StringUtils.isEmpty(fullName)) {
			return null;
		}

		if (Activator.getDefault() == null) {
			return null;
		}

		final ImageRegistry imageRegistry = Activator.getDefault().getImageRegistry();
		Image image = imageRegistry.get(fullName);
		if (image == null || image.isDisposed()) {
			final ImageDescriptor descriptor = getImageDescriptor(fullName);
			if (descriptor == null) {
				return null;
			}
			imageRegistry.remove(fullName);
			imageRegistry.put(fullName, descriptor);
			image = imageRegistry.get(fullName);
		}
		return image;
	}

	private synchronized Image loadSvgImage(final String fullName, final IconSize imageSize) {

		if (StringUtils.isEmpty(fullName)) {
			return null;
		}

		if (Activator.getDefault() == null) {
			return null;
		}

		final ImageRegistry imageRegistry = Activator.getDefault().getImageRegistry();
		final String registryKey = fullName + "?" + imageSize; //$NON-NLS-1$
		Image image = imageRegistry.get(registryKey);
		if (image == null || image.isDisposed()) {
			image = createSvgImage(fullName, imageSize);
			if (image != null) {
				imageRegistry.remove(registryKey);
				imageRegistry.put(registryKey, image);
				image = imageRegistry.get(registryKey);
			}
		}
		return image;
	}

	/**
	 * Loads the given SVG file and creates a SWT image with the given size.
	 * 
	 * @param fullName
	 *            file name of the SVG
	 * @param imageSize
	 *            expected size of the SWT image (if {@code null} the size of the SVG is used)
	 * @return SWT image or {@code null} if creation failed
	 */
	private Image createSvgImage(final String fullName, final IconSize imageSize) {

		final Display display = SwtUtilities.getDisplay();
		if (display == null) {
			return null;
		}

		final URL url = getImageUrl(fullName);
		if (url == null) {
			return null;
		}

		try {
			final Rectangle bounds = getImageBounds(url.toString(), imageSize);

			svgRasterizer.setUrl(url);
			final BufferedImage bi = svgRasterizer.createBufferedImage(bounds);

			final ImageData imageData = SwtUtilities.convertAwtImageToImageData(bi);
			if (imageData == null) {
				return null;
			}

			return new Image(display, imageData);
		} catch (final TranscoderException e) {
			LOGGER.log(LogService.LOG_ERROR, "could transform SVG image:" + fullName, e);
			return null;
		} catch (final IOException e) {
			LOGGER.log(LogService.LOG_ERROR, "could not compute bounds of the image:" + fullName, e);
			return null;
		}
	}

	/**
	 * Returns the expected (scalded) size/bounds of the image
	 * 
	 * @param url
	 *            the url for the given SVG-Image as String
	 * @param imageSize
	 *            expected size of the SWT image (if {@code null} the size of the SVG is used)
	 * @return bounds of the image
	 * @throws IOException
	 */
	private Rectangle getImageBounds(final String url, final IconSize imageSize) throws IOException {
		int x = 0;
		int y = 0;
		int width = 0;
		int height = 0;

		final String parser = XMLResourceDescriptor.getXMLParserClassName();
		final SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
		Element svgElement = null;
		Document document = null;

		if (url != null) {
			if (!(url.equals(""))) { //$NON-NLS-1$
				document = f.createDocument(url);
			}

		}

		if ((imageSize == null) || (imageSize == IconSize.NONE)) {
			if (document != null) {
				svgElement = document.getDocumentElement();
				final String viewBox = svgElement.getAttribute("viewBox"); //$NON-NLS-1$
				final String widthAsString = svgElement.getAttribute("width"); //$NON-NLS-1$
				final String heightAsString = svgElement.getAttribute("height"); //$NON-NLS-1$
				if (viewBox.equals("")) { //$NON-NLS-1$
					height = Math.round(Float.parseFloat(heightAsString));
					width = Math.round(Float.parseFloat(widthAsString));
				} else {
					final String[] viewBoxValues = viewBox.split("\\s+"); //$NON-NLS-1$
					width = Math.round(Float.parseFloat(viewBoxValues[2]));
					height = Math.round(Float.parseFloat(viewBoxValues[3]));
				}
			}
		} else {
			width = imageSize.getWidth();
			height = imageSize.getHeight();
		}
		x = SwtUtilities.convertXToDpi(x);
		y = SwtUtilities.convertYToDpi(y);
		width = SwtUtilities.convertXToDpi(width);
		height = SwtUtilities.convertYToDpi(height);
		return new Rectangle(x, y, width, height);
	}

	/**
	 * 
	 * @param fullName
	 *            The name of the image with file extension. Precondition: fullName must have the fileextension .svg
	 * @param imageSizeRequested
	 *            The size of the Image.
	 * @return The name of the image with the mapped IconSizeGroupIdentifier.
	 * @since 6.2
	 */
	private String addIconGroupIdentifier(String fullName, final IconSize imageSizeRequested) {
		Assert.isNotNull(fullName);
		Assert.isNotNull(imageSizeRequested);
		Assert.isTrue(fullName.endsWith(".svg"), "imagename must end with '.svg'"); //$NON-NLS-1$ //$NON-NLS-2$

		final String fileExtension = fullName.substring(fullName.length() - ".svg".length()); //$NON-NLS-1$
		String fileName = getFileNameWithoutFileExtension(fullName);

		if (imageSizeRequested.isSizeNone()) {
			return fullName = fileName + fileExtension;
		}

		final String defaultMapping = fileName.substring(fileName.length() - 1);
		if (defaultMapping.equals(imageSizeRequested.getDefaultMapping())) {
			fileName = removeDefaultmapping(fileName);
			if (getIconSizeGroupIdentifier(imageSizeRequested).equals(defaultMapping)) {
				return fullName = fileName + fileExtension;
			}
			return fullName = fileName + getIconSizeGroupIdentifier(imageSizeRequested) + fileExtension;
		}

		return fullName = fileName + getIconSizeGroupIdentifier(imageSizeRequested) + fileExtension;
	}

	/**
	 * @param fullFileName
	 * @return the filenmae without .svg extension.
	 */
	private String getFileNameWithoutFileExtension(final String fullFileName) {
		return fullFileName.substring(0, fullFileName.length() - ".svg".length()); //$NON-NLS-1$
	}

	/**
	 * Returns the String value of the iconsize group for the given IconSize.
	 * 
	 * @param imageSize
	 *            the desired iconSize
	 * @return the Group suffix of the mapped iconsize, returns the given iconsize if the map contains no mapping for this value.
	 */
	private String getIconSizeGroupIdentifier(final IconSize iconSize) {
		return LnfManager.getLnf().getIconSizeGroupIdentifier(iconSize);
	}

	/**
	 * Returns a descriptor of the image for the given name. The file of the image is searched in every given bundle + icon path. The icon paths are define via
	 * extension points.
	 * 
	 * @param fullName
	 *            full name of the image (file name)
	 * @return image descriptor or {@code null} if file does not exists.
	 */
	private ImageDescriptor getImageDescriptor(final String fullName) {

		final URL url = getImageUrl(fullName);
		if (url != null) {
			return ImageDescriptor.createFromURL(url);
		}

		final StringBuilder sb = new StringBuilder();
		sb.append("Image resource \""); //$NON-NLS-1$
		sb.append(fullName);
		sb.append("\" not found in:"); //$NON-NLS-1$

		for (final IImagePathExtension iconPath : iconPaths) {
			sb.append("\n  "); //$NON-NLS-1$
			sb.append(iconPath.getContributingBundle().getLocation());
			sb.append(iconPath.getPath());
		}

		LOGGER.log(LogService.LOG_DEBUG, sb.toString());
		return null;

	}

	/**
	 * Tries to find the given image and returns the URL of the image file. The file of the image is searched in every given bundle + icon path. The icon paths
	 * are define via extension points.
	 * 
	 * @param fullName
	 *            complete name of the image (with scaling, with extension etc.)
	 * @return URL of the image or {@code null} if the image file wasn't found
	 */
	private URL getImageUrl(final String fullName) {

		if (iconPaths != null) {
			for (final IImagePathExtension iconPath : iconPaths) {
				final String fullPath = iconPath.getPath() + '/' + fullName;
				final URL url = iconPath.getContributingBundle().getEntry(fullPath);
				if (url != null) {
					return url;
				}
			}
		}

		return null;

	}

	/**
	 * Returns the missing image.
	 * 
	 * @return missing image
	 */
	public synchronized Image getMissingImage() {
		if (missingImage == null) {
			missingImage = ImageDescriptor.getMissingImageDescriptor().createImage();
		}
		return missingImage;
	}

	/**
	 * Adds the suffix of scaling to the given name of the image.
	 * 
	 * @param imageName
	 *            name (ID) of the image
	 * @param fileExtension
	 *            extension of the image file
	 * 
	 * @return image name with suffix of scaling or image name without suffix if no matching image file exists
	 * 
	 * @since 6.0
	 */
	public String addImageScaleSuffix(final String imageName, final ImageFileExtension fileExtension) {
		return addImageScaleSuffix(imageName, fileExtension, SwtUtilities.convertPointToDpi(SwtUtilities.getDefaultDpi()));
	}

	/**
	 * Adds the suffix of scaling to the given name of the image.
	 * 
	 * @param imageName
	 *            name (ID) of the image
	 * @param fileExtension
	 *            extension of the image file
	 * @param dpi
	 *            this display dpi
	 * 
	 * @return image name with suffix of scaling and icon size identifier or image name without suffix if no matching image file exists
	 * 
	 * @since 6.1
	 */
	public String addImageScaleSuffix(final String imageName, final ImageFileExtension fileExtension, final Point dpi) {
		return addImageScaleSuffix(imageName, fileExtension, dpi, null);
	}

	/**
	 * Adds the suffix of scaling to the given name of the image.
	 * 
	 * @param imageName
	 *            name (ID) of the image
	 * @param fileExtension
	 *            extension of the image file
	 * @param dpi
	 *            this display dpi
	 * @param imageSize
	 *            the requested image size
	 * 
	 * 
	 * @return image name with suffix of scaling and icon size identifier or image name without suffix if no matching image file exists
	 * 
	 * @since 6.2
	 */
	public String addImageScaleSuffix(final String imageName, final ImageFileExtension fileExtension, final Point dpi, final IconSize imageSize) {

		if (LnfManager.isLnfCreated()) {
			final String suffix = LnfManager.getLnf().getIconScaleSuffix(dpi);
			if (!StringUtils.isEmpty(suffix)) {
				final List<String> candidates = new ArrayList<String>((Arrays.asList(imageName + suffix)));
				if (imageSize != null) {
					// honor icon size
					candidates.add(imageName + imageSize.getDefaultMapping() + suffix);
				}
				// concrete names first
				Collections.reverse(candidates);
				for (final String candidate : candidates) {
					if (imageExists(candidate, fileExtension)) {
						return candidate;
					}
				}
			}
		}

		return imageName;
	}

	private synchronized boolean imageExists(final String imageName, final ImageFileExtension fileExtension) {
		final String fullName = getFullName(imageName, fileExtension);
		if (cachedImageNames.containsKey(fullName)) {
			return cachedImageNames.get(fullName);
		}
		final ImageDescriptor descriptor = getImageDescriptor(fullName);
		if (descriptor == null) {
			cachedImageNames.put(fullName, false);
		} else {
			cachedImageNames.put(fullName, true);
		}
		return (descriptor != null);
	}

	@InjectExtension
	public void update(final IImagePathExtension[] iconPaths) {
		this.iconPaths = iconPaths;
		this.clearCachedImageNames();
	}

	/**
	 * After updating iconPaths we clear cached Imagenames.
	 */
	private void clearCachedImageNames() {
		this.cachedImageNames.clear();
	}

	private class ImageOperations {

		public PngDefaultOpertion getPngOperation() {
			return new PngDefaultOpertion();
		}

		public SvgDefaultOperation getSvgOperation() {
			return new SvgDefaultOperation();
		}

		public PngOperationNoImageSize getPngOperationWithoutImageSize() {
			return new PngOperationNoImageSize();
		}

		public PngOperationNoImageSizeAndDpi getPngOperationSecondlastAttempt() {
			return new PngOperationNoImageSizeAndDpi();
		}

		public PngDefaultImageOperation getPngDefaultImageOperation() {
			return new PngDefaultImageOperation();
		}

		private class PngDefaultOpertion implements IImageFind {
			public Image find(final String imageName, final ImageFileExtension fileExtension, final IconSize imageSizeRequested) {
				final Point dpi = SwtUtilities.convertPointToDpi(SwtUtilities.getDefaultDpi());
				final String fullFileName = getFullScaledName(imageName, fileExtension, dpi, imageSizeRequested);
				final Image image = loadImage(fullFileName);
				if (image != null) {
					return image;
				}
				return null;
			}
		}

		private class SvgDefaultOperation implements IImageFind {
			public Image find(final String imageName, ImageFileExtension fileExtension, final IconSize imageSizeRequested) {
				fileExtension = ImageFileExtension.SVG;
				final String fullFileName = getFullSvgName(imageName);
				Image image = null;
				if (fullFileName != null) {
					if (imageSizeRequested.isSizeNone()) {
						image = loadSvgImage(imageName, IconSize.NONE);
					} else {
						final String fullNameWithGroupIdentifier = addIconGroupIdentifier(fullFileName, imageSizeRequested);
						image = loadSvgImage(fullNameWithGroupIdentifier, imageSizeRequested);
					}
					if (image != null) {
						return image;
					} else {
						String fileName = getFileNameWithoutFileExtension(fullFileName); //;
						final String defaultMapping = fileName.substring(fileName.length() - 1);
						if (defaultMapping.equals(imageSizeRequested.getDefaultMapping())) {
							fileName = removeDefaultmapping(fileName);
							if (getIconSizeGroupIdentifier(imageSizeRequested).equals(defaultMapping)) {
								fileName = fileName + "." + fileExtension.getFileNameExtension(); //$NON-NLS-1$
							}
						}
						image = loadSvgImage(fileName + "." + fileExtension.getFileNameExtension(), imageSizeRequested); //$NON-NLS-1$
						if (image != null) {
							return image;
						}
						image = loadSvgImage(fullFileName, imageSizeRequested);
						if (image != null) {
							return image;
						}
					}
				}
				return null;
			}

		}

		private class PngOperationNoImageSize implements IImageFind {
			public Image find(final String imageName, final ImageFileExtension fileExtension, final IconSize imageSizeRequested) {
				final Point dpi = SwtUtilities.getDefaultDpi();
				final String fullFileName = getFullScaledName(imageName, fileExtension, dpi);
				final Image image = loadImage(fullFileName);
				if (image != null) {
					return image;
				}
				return null;
			}
		}

		private class PngOperationNoImageSizeAndDpi implements IImageFind {
			public Image find(final String imageName, final ImageFileExtension fileExtension, final IconSize imageSizeRequested) {
				final String fullFileName = getFullName(imageName, fileExtension);
				final Image image = loadImage(fullFileName);
				if (image != null) {
					return image;
				}
				return null;
			}

		}

		private class PngDefaultImageOperation implements IImageFind {

			public Image find(final String imageName, final ImageFileExtension fileExtension, final IconSize imageSizeRequested) {
				final String defaultIconName = getDefaultIconMangerImageName(imageName);
				if (!StringUtils.equals(defaultIconName, imageName)) {
					final String fullFileName = getFullName(defaultIconName, fileExtension);
					final Image image = loadImage(fullFileName);
				}
				return null;
			}

		}

	}

}
